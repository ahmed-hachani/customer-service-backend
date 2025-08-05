package org.mounanga.customerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScoringParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String criterion; // e.g., "salary", "maritalStatus"
    @Column(name = "rule_condition") // Renamed to avoid conflict

    private String ruleCondition; // e.g., "GREATER_THAN", "BETWEEN", "EQUALS"
    private String value;     // e.g., "2500" or "1500-2500" or "MARIÉ"
    private int score;
}
