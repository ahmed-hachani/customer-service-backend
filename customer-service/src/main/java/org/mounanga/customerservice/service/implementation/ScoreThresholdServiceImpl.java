package org.mounanga.customerservice.service.implementation;

import org.mounanga.customerservice.entity.ScoreThreshold;
import org.mounanga.customerservice.repository.ScoreThresholdRepository;
import org.mounanga.customerservice.repository.ScoringParametreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ScoreThresholdServiceImpl {


    private final ScoreThresholdRepository repository;
    public ScoreThresholdServiceImpl (ScoreThresholdRepository repository){
        this.repository = repository;
    }

    public ScoreThreshold saveThresholds(ScoreThreshold thresholds) {
        // If one already exists, update it
        if (!repository.findAll().isEmpty()) {
            ScoreThreshold existing = repository.findAll().get(0);
            existing.setAccepted(thresholds.getAccepted());
            existing.setManualReview(thresholds.getManualReview());
            return repository.save(existing);
        }
        return repository.save(thresholds);
    }

    public Optional<ScoreThreshold> getThresholds() {
        return repository.findAll().stream().findFirst();
    }
}

