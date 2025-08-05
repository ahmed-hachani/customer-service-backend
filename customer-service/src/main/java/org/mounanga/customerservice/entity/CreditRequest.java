package org.mounanga.customerservice.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.mounanga.customerservice.enums.FinalDecision;
import org.mounanga.customerservice.enums.Status;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreditRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1. Informations Personnelles
    private String applicantName;
    private String birthDate;
    private String cin;
    private String address;
    private String phone;
    private String email;

    // 2. Informations Professionnelles
    private String profession;
    private String employer;
    private String contractType; // e.g. "CDI", "CDD"
    private int seniority; // in years
    private double salary;
    private String workSector; // "PUBLIC", "PRIVÉ"

    // 3. Situation Familiale
    private String maritalStatus; // "MARIÉ", "CÉLIBATAIRE", etc.
    private int numberOfChildren;
    private int dependents;

    // 4. Informations Complémentaires
    private boolean firstCredit;
    private boolean ownsHouse;

    // 5. Détails de la Demande de Crédit
    private double requestedAmount;
    private int requestedDuration; // in months
    private String creditReason;   // e.g., "Construction"

    // System-generated
    private int score;

    private String cinImageUrl;


    @Enumerated(EnumType.STRING)
    private Status status;
    @Enumerated(EnumType.STRING)
    private FinalDecision finalDecision = FinalDecision.PENDING;
    private String userId;
    @CreationTimestamp
    private LocalDateTime createdAt;
}
