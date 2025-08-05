package org.mounanga.customerservice.web;

import org.mounanga.customerservice.entity.ScoringParameter;
import org.mounanga.customerservice.repository.ScoringParametreRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/scoring-parameters")

public class ScoringParameterRestController {
    private final ScoringParametreRepository repo;

    public ScoringParameterRestController(ScoringParametreRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<ScoringParameter> getAll() {
        return repo.findAll();
    }

    @PostMapping
    public ScoringParameter create(@RequestBody ScoringParameter param) {
        return repo.save(param);
    }

    @PutMapping("/{id}")
    public ScoringParameter update(@PathVariable Long id, @RequestBody ScoringParameter updated) {
        return repo.findById(id).map(param -> {
            param.setCriterion(updated.getCriterion());
            param.setRuleCondition(updated.getRuleCondition());
            param.setValue(updated.getValue());
            param.setScore(updated.getScore());
            return repo.save(param);
        }).orElseThrow(() -> new RuntimeException("Parameter not found"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
