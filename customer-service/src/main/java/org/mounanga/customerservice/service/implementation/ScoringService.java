package org.mounanga.customerservice.service.implementation;

import lombok.AllArgsConstructor;
import org.mounanga.customerservice.entity.CreditRequest;
import org.mounanga.customerservice.entity.ScoringParameter;
import org.mounanga.customerservice.repository.ScoringParametreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ScoringService {

    private final ScoringParametreRepository scoringRepo;

    public int computeScore(CreditRequest request) {
        int score = 0;

        // Salary
        List<ScoringParameter> salaryParams = scoringRepo.findByCriterion("salary");
        for (ScoringParameter param : salaryParams) {
            double salary = request.getSalary();
            if ("GREATER_THAN".equals(param.getRuleCondition()) && salary > Double.parseDouble(param.getValue())) {
                score += param.getScore();
            } else if ("BETWEEN".equals(param.getRuleCondition())) {
                String[] range = param.getValue().split("-");
                if (salary >= Double.parseDouble(range[0]) && salary <= Double.parseDouble(range[1])) {
                    score += param.getScore();
                }
            }
        }

        // Marital Status
        List<ScoringParameter> maritalParams = scoringRepo.findByCriterion("maritalStatus");
        for (ScoringParameter param : maritalParams) {
            if ("EQUALS".equals(param.getRuleCondition()) && param.getValue().equalsIgnoreCase(request.getMaritalStatus())) {
                score += param.getScore();
            }
        }

        // Work Sector
        List<ScoringParameter> sectorParams = scoringRepo.findByCriterion("workSector");
        for (ScoringParameter param : sectorParams) {
            if ("EQUALS".equals(param.getRuleCondition()) && param.getValue().equalsIgnoreCase(request.getWorkSector())) {
                score += param.getScore();
            }
        }

        // Number of Children
        List<ScoringParameter> childrenParams = scoringRepo.findByCriterion("numberOfChildren");
        for (ScoringParameter param : childrenParams) {
            int children = request.getNumberOfChildren();
            if ("LESS_THAN_OR_EQUAL".equals(param.getRuleCondition()) && children <= Integer.parseInt(param.getValue())) {
                score += param.getScore();
            }
        }

        // Dependents
        List<ScoringParameter> dependentsParams = scoringRepo.findByCriterion("dependents");
        for (ScoringParameter param : dependentsParams) {
            int dependents = request.getDependents();
            if ("LESS_THAN_OR_EQUAL".equals(param.getRuleCondition()) && dependents <= Integer.parseInt(param.getValue())) {
                score += param.getScore();
            }
        }

        // First Credit
        List<ScoringParameter> firstCreditParams = scoringRepo.findByCriterion("firstCredit");
        for (ScoringParameter param : firstCreditParams) {
            boolean firstCredit = request.isFirstCredit();
            if ("BOOLEAN".equals(param.getRuleCondition()) && Boolean.parseBoolean(param.getValue()) == firstCredit) {
                score += param.getScore();
            }
        }

        // Owns House
        List<ScoringParameter> houseParams = scoringRepo.findByCriterion("ownsHouse");
        for (ScoringParameter param : houseParams) {
            boolean ownsHouse = request.isOwnsHouse();
            if ("BOOLEAN".equals(param.getRuleCondition()) && Boolean.parseBoolean(param.getValue()) == ownsHouse) {
                score += param.getScore();
            }
        }

        return score;
    }
}
