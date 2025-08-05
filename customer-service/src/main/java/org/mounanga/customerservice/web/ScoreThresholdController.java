package org.mounanga.customerservice.web;

import org.mounanga.customerservice.entity.ScoreThreshold;
import org.mounanga.customerservice.service.implementation.ScoreThresholdServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/score-thresholds")
@CrossOrigin(origins = "http://localhost:4200")
public class ScoreThresholdController {

    @Autowired
    private ScoreThresholdServiceImpl service;

    @GetMapping
    public ScoreThreshold getThresholds() {
        return service.getThresholds().orElseGet(() -> {
            ScoreThreshold defaultThreshold = new ScoreThreshold();
            defaultThreshold.setAccepted(60);
            defaultThreshold.setManualReview(50);
            return service.saveThresholds(defaultThreshold);
        });
    }

    @PostMapping
    public ScoreThreshold saveThresholds(@RequestBody ScoreThreshold thresholds) {
        return service.saveThresholds(thresholds);
    }
}
