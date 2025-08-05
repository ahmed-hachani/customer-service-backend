package org.mounanga.customerservice.web;

import org.mounanga.customerservice.entity.CreditRequest;
import org.mounanga.customerservice.service.implementation.CreditRequestServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.mounanga.customerservice.enums.Status;

import java.io.File;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/ocr")
public class OCRRestController {
    private final CreditRequestServiceImpl creditRequestService;

    public OCRRestController(CreditRequestServiceImpl creditRequestService) {
        this.creditRequestService = creditRequestService;
    }

    @PostMapping("/upload-docx")
    public ResponseEntity<CreditRequest> uploadDocx(@RequestParam("file") MultipartFile file,@RequestParam("cinImage") MultipartFile cinImage, @RequestParam("userId") String userId) throws Exception {
        File convFile = File.createTempFile("ocr_input", ".docx");
        file.transferTo(convFile);

        String rawText = creditRequestService.extractTextFromDocx(convFile);
        String correctedText = creditRequestService.correctWithTextBlob(rawText);
        CreditRequest request = creditRequestService.parseCreditRequest(correctedText);

        // 3. Handle CIN image saving
        String cinImageUrl = creditRequestService.storeCinImage(cinImage);
        request.setCinImageUrl(cinImageUrl); // new field

        return ResponseEntity.ok(creditRequestService.createRequest(request, userId));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CreditRequest>> getByUserId(@PathVariable String userId) {
        List<CreditRequest> requests = creditRequestService.getCreditRequestsByUserId(userId);
        return ResponseEntity.ok(requests);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Optional<CreditRequest>> getByUserId(@PathVariable Long id) {
        Optional<CreditRequest> requests = creditRequestService.getCreditRequestsById(id);
        return ResponseEntity.ok(requests);
    }
    @GetMapping("/status")
    public ResponseEntity<List<CreditRequest>> getRequestsByStatus(@RequestParam Status status) {
        List<CreditRequest> requests = creditRequestService.getRequestsByStatus(status);
        return ResponseEntity.ok(requests);
    }

    // PUT manual review (accept or decline)
    @PutMapping("/manual-review/{id}")
    public ResponseEntity<CreditRequest> reviewManualRequest(
            @PathVariable Long id,
            @RequestParam boolean accept
    ) {
        Optional<CreditRequest> optional = creditRequestService.reviewManualRequest(id, accept);
        return optional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT final decision (validate or reject)
    @PutMapping("/final-decision/{id}")
    public ResponseEntity<CreditRequest> validateFinalDecision(
            @PathVariable Long id,
            @RequestParam boolean approve
    ) {
        Optional<CreditRequest> optional = creditRequestService.validateFinalDecision(id, approve);
        return optional.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // GET all credit requests
    @GetMapping
    public ResponseEntity<List<CreditRequest>> getAllCreditRequests() {
        List<CreditRequest> requests = creditRequestService.getAllCreditRequests();
        return ResponseEntity.ok(requests);
    }
}
