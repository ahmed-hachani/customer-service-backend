package org.mounanga.customerservice.service.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendFinalDecisionEmail(String to, String applicantName, boolean approved) {
        String subject = "Décision finale concernant votre demande de crédit";
        String body;

        if (approved) {
            body = String.format(
                    "Bonjour %s,\n\n" +
                            "Nous avons le plaisir de vous informer que votre demande de crédit a été acceptée.\n\n" +
                            "Veuillez nous visiter afin de compléter les procédures nécessaires à la finalisation de votre dossier.\n\n" +
                            "Notre équipe reste à votre disposition pour toute question ou information complémentaire.\n\n" +
                            "Nous vous remercions de votre confiance et vous souhaitons une excellente continuation.\n\n" +
                            "Cordialement,\n" +
                            "L’équipe CréditExpress\n" +
                            "[Coordonnées / Téléphone / Email]\n" +
                            "[Site web, si applicable]",
                    applicantName
            );
        } else {
            body = String.format(
                    "Bonjour %s,\n\n" +
                            "Nous vous remercions pour l’intérêt porté à CréditExpress.\n\n" +
                            "Après étude attentive de votre dossier, nous regrettons de vous informer que votre demande de crédit a été refusée.\n\n" +
                            "N’hésitez pas à nous contacter si vous souhaitez des précisions ou des conseils pour d’éventuelles demandes futures.\n\n" +
                            "Nous restons à votre disposition.\n\n" +
                            "Cordialement,\n" +
                            "L’équipe CréditExpress\n" +
                            "[Coordonnées / Téléphone / Email]\n" +
                            "[Site web, si applicable]",
                    applicantName
            );
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}