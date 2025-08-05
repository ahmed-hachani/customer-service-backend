package org.mounanga.customerservice.service.implementation;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.mounanga.customerservice.entity.CreditRequest;
import org.mounanga.customerservice.entity.ScoreThreshold;
import org.mounanga.customerservice.entity.ScoringParameter;
import org.mounanga.customerservice.enums.FinalDecision;
import org.mounanga.customerservice.enums.Status;
import org.mounanga.customerservice.repository.CreditRequestRepository;
import org.mounanga.customerservice.repository.ScoreThresholdRepository;
import org.mounanga.customerservice.repository.ScoringParametreRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CreditRequestServiceImpl {
    private final CreditRequestRepository repository;
    private final ScoringParametreRepository scoringRepo;
    private final ScoreThresholdServiceImpl thresholdService;
    private final MailService emailService;

    public CreditRequestServiceImpl(CreditRequestRepository repository, ScoringParametreRepository scoringRepo, ScoreThresholdRepository thresholdRepository, ScoreThresholdServiceImpl thresholdService, MailService emailService) {
        this.repository = repository;
        this.scoringRepo = scoringRepo;
        this.thresholdService = thresholdService;
        this.emailService = emailService;
    }
    public CreditRequest createRequest(CreditRequest request, String userId) {
        int score = computeScore(request);
        request.setScore(score);
        request.setUserId(userId);

        ScoreThreshold thresholds = thresholdService.getThresholds().orElseGet(() -> {
            ScoreThreshold t = new ScoreThreshold();
            t.setAccepted(60);
            t.setManualReview(50);
            return t;
        });

        if (score >= thresholds.getAccepted()) {
            request.setStatus(Status.ACCEPTED);
        } else if (score >= thresholds.getManualReview()) {
            request.setStatus(Status.MANUAL_REVIEW);
        } else {
            request.setStatus(Status.DECLINED);
        }

        request.setFinalDecision(FinalDecision.PENDING);
        return repository.save(request);
    }

    public List<CreditRequest> getAllCreditRequests() {
        return repository.findAll();
    }
    public List<CreditRequest> getCreditRequestsByUserId(String userId) {
        return repository.findByUserId(userId);
    }
    public Optional<CreditRequest> getCreditRequestsById(Long id) {
        return repository.findById(id);
    }

    public List<CreditRequest> getRequestsByStatus(Status status) {
        return repository.findByStatus(status);
    }

    public Optional<CreditRequest> reviewManualRequest(Long id, boolean accept) {
        Optional<CreditRequest> optionalRequest = repository.findById(id);
        if (optionalRequest.isPresent()) {
            CreditRequest req = optionalRequest.get();
            if (req.getStatus() == Status.MANUAL_REVIEW) {
                req.setStatus(accept ? Status.ACCEPTED : Status.DECLINED);
                repository.save(req);
            }
        }
        return optionalRequest;
    }
    public Optional<CreditRequest> validateFinalDecision(Long id, boolean approve) {
        Optional<CreditRequest> optional = repository.findById(id);
        if (optional.isPresent()) {
            CreditRequest request = optional.get();
            request.setFinalDecision(approve ? FinalDecision.VALIDATED : FinalDecision.REJECTED);
            repository.save(request);
            // sending email
            emailService.sendFinalDecisionEmail(
                    request.getEmail(),
                    request.getApplicantName(),
                    approve
            );
        }
        return optional;
    }
    private int computeScoreFromDB(CreditRequest request) {
        int score = 0;
        List<ScoringParameter> params = scoringRepo.findAll();

        for (ScoringParameter param : params) {
            String criterion = param.getCriterion();
            String condition = param.getRuleCondition();
            String value = param.getValue();
            int paramScore = param.getScore();

            switch (criterion) {
                case "salary":
                    double salary = request.getSalary();
                    if ("GREATER_THAN".equals(condition) && salary > Double.parseDouble(value)) {
                        score += paramScore;
                    } else if ("LESS_THAN_OR_EQUAL".equals(condition) && salary <= Double.parseDouble(value)) {
                        score += paramScore;
                    } else if ("BETWEEN".equals(condition)) {
                        String[] range = value.split("-");
                        double min = Double.parseDouble(range[0]);
                        double max = Double.parseDouble(range[1]);
                        if (salary >= min && salary <= max) {
                            score += paramScore;
                        }
                    }
                    break;

                case "maritalStatus":
                    if ("EQUALS".equals(condition) && value.equalsIgnoreCase(request.getMaritalStatus())) {
                        score += paramScore;
                    }
                    break;

                case "workSector":
                    if ("EQUALS".equals(condition) && value.equalsIgnoreCase(request.getWorkSector())) {
                        score += paramScore;
                    }
                    break;

                case "numberOfChildren":
                    int children = request.getNumberOfChildren();
                    if ("LESS_THAN_OR_EQUAL".equals(condition) && children <= Integer.parseInt(value)) {
                        score += paramScore;
                    } else if ("GREATER_THAN".equals(condition) && children > Integer.parseInt(value)) {
                        score += paramScore;
                    }
                    break;

                case "dependents":
                    int dependents = request.getDependents();
                    if ("LESS_THAN_OR_EQUAL".equals(condition) && dependents <= Integer.parseInt(value)) {
                        score += paramScore;
                    } else if ("GREATER_THAN".equals(condition) && dependents > Integer.parseInt(value)) {
                        score += paramScore;
                    }
                    break;

                case "firstCredit":
                    if ("BOOLEAN".equals(condition) && Boolean.parseBoolean(value) == request.isFirstCredit()) {
                        score += paramScore;
                    }
                    break;

                case "ownsHouse":
                    if ("BOOLEAN".equals(condition) && Boolean.parseBoolean(value) == request.isOwnsHouse()) {
                        score += paramScore;
                    }
                    break;
            }
        }

        return score;
    }
    private int computeScore(CreditRequest request) {
        int score = 0;

        // 1. Salaire
        if (request.getSalary() > 2500) {
            score += 30;
        } else if (request.getSalary() >= 1500) {
            score += 20;
        } else {
            score += 10;
        }

        // 2. Statut marital
        if ("MARIÉ".equalsIgnoreCase(request.getMaritalStatus())) {
            score += 10;
        } else {
            score += 5;
        }

        // 3. Secteur de travail
        if ("PUBLIC".equalsIgnoreCase(request.getWorkSector())) {
            score += 15;
        } else {
            score += 10;
        }

        // 4. Nombre d’enfants
        if (request.getNumberOfChildren() <= 2) {
            score += 10;
        } else {
            score += 5;
        }

        // 5. Personnes à charge
        if (request.getDependents() <= 3) {
            score += 10;
        } else {
            score += 5;
        }

        // 6. Premier crédit ?
        if (!request.isFirstCredit()) {
            score += 10;
        } else {
            score += 5;
        }

        // 7. Possède une maison ?
        if (request.isOwnsHouse()) {
            score += 15;
        }

        return score;
    }


    public String extractTextFromDocx(File file) throws Exception {
        StringBuilder text = new StringBuilder();
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                text.append(paragraph.getText()).append("\n");
            }
        }
        return text.toString();
    }

    public CreditRequest parseCreditRequest(String text) {
        CreditRequest request = new CreditRequest();

        // 1. Informations Personnelles
        request.setApplicantName(extractField(text, "Nom complet"));
        request.setBirthDate(extractField(text, "Date de naissance"));
        request.setCin(extractField(text, "CIN"));
        request.setAddress(extractField(text, "Adresse"));
        request.setPhone(extractField(text, "Téléphone"));
        request.setEmail(extractField(text, "Email"));

        // 2. Informations Professionnelles
        request.setProfession(extractField(text, "Profession"));
        request.setEmployer(extractField(text, "Employeur"));
        request.setContractType(extractField(text, "Type de contrat (CDI/CDD/etc.) ")); // CDI/CDD
        request.setSeniority(parseSafeInt(extractField(text, "Ancienneté (en années) "), 0));
        request.setSalary(parseSafeDouble(extractField(text, "Salaire mensuel net (TND) "), 0));

        String sector = extractField(text, "Secteur d'activité (Public/Privé) ").toLowerCase();
        request.setWorkSector(sector.contains("public") ? "PUBLIC" : "PRIVÉ");

        // 3. Situation Familiale
        String marital = extractField(text, "Statut marital (Marié/Célibataire/Autre) ").toLowerCase();
        request.setMaritalStatus(marital.contains("mari") ? "MARIÉ" : "CÉLIBATAIRE");

        request.setNumberOfChildren(parseSafeInt(extractField(text, "Nombre d’enfants"), 0));
        request.setDependents(parseSafeInt(extractField(text, "Nombre total de personnes à charge"), 0));

        // 4. Informations Complémentaires
        String firstCredit = extractField(text, "S'agit-il de votre premier crédit ? (Oui / Non) ").toLowerCase();
        request.setFirstCredit(firstCredit.contains("oui"));

        String ownsHouse = extractField(text, "Possédez-vous une maison ? (Oui / Non) ").toLowerCase();
        request.setOwnsHouse(ownsHouse.contains("oui"));

        // 5. Détails de la Demande de Crédit
        request.setRequestedAmount(parseSafeDouble(extractField(text, "Montant souhaité (TND) "), 0));
        request.setRequestedDuration(parseSafeInt(extractField(text, "Durée souhaitée (en mois) "), 0));
        request.setCreditReason(extractField(text, "Motif du crédit"));

        return request;
    }


    private String extractField(String text, String label) {
        Pattern pattern = Pattern.compile(Pattern.quote(label) + "\\s*[:：]\\s*(.+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        System.err.println("Field not found or empty: " + label);
        return "";
    }

    private int parseSafeInt(String value, int fallback) {
        try {
            return Integer.parseInt(value.replaceAll("[^\\d]", ""));
        } catch (Exception e) {
            return fallback;
        }
    }

    private double parseSafeDouble(String value, double fallback) {
        try {
            return Double.parseDouble(value.replaceAll("[^\\d.]", ""));
        } catch (Exception e) {
            return fallback;
        }
    }
    public String correctWithTextBlob(String rawText) {
        try {
            ProcessBuilder builder = new ProcessBuilder("python", "ocr_corrector.py");
            Process process = builder.start();

            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()))) {
                writer.write(rawText);
                writer.flush();
            }

            StringBuilder correctedText = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    correctedText.append(line).append("\n");
                }
            }

            return correctedText.toString().trim();
        } catch (IOException e) {
            e.printStackTrace();
            return rawText; // fallback
        }
    }
    public String storeCinImage(MultipartFile image) {
        try {
            // You can change this location as needed
            String uploadDir = "uploads/cin/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(uploadDir, fileName);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return filePath.toString(); // or a public URL if served from static folder
        } catch (IOException e) {
            throw new RuntimeException("Failed to save CIN image", e);
        }
    }
    // methods for charts :
    public Map<String, Long> getMonthlyRequestStats() {
        List<Object[]> results = repository.countRequestsPerMonth();
        Map<String, Long> stats = new LinkedHashMap<>();
        for (Object[] row : results) {
            stats.put((String) row[0], ((Number) row[1]).longValue());
        }
        return stats;
    }
    public Map<String, Long> getApprovalStatusStats() {
        List<Object[]> results = repository.countByApprovalStatus();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            Status status = (Status) row[0];
            Long count = (Long) row[1];
            stats.put(status.name(), count);
        }
        return stats;
    }
    public Map<String, Long> getFinalDecisionStats() {
        List<Object[]> results = repository.countByFinalDecision();
        Map<String, Long> stats = new HashMap<>();
        for (Object[] row : results) {
            FinalDecision decision = (FinalDecision) row[0];
            Long count = (Long) row[1];
            stats.put(decision.name(), count);
        }
        return stats;
    }
}
