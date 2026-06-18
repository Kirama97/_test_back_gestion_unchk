package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.*;
import com.gestion_unchk.gestion_unchk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for Cours and Sequences management.
 */
@RestController
@RequestMapping("/api/cours")
public class CoursController {

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private SequenceRepository sequenceRepository;

    // ──────────────────── Cours ────────────────────

    @GetMapping
    public List<Cours> getAllCours() {
        return coursRepository.findAll();
    }

    @GetMapping("/classe/{classeId}")
    public List<Cours> getCoursByClasse(@PathVariable Long classeId) {
        return coursRepository.findByClasseId(classeId);
    }

    @GetMapping("/enseignant/{enseignantId}")
    public List<Cours> getCoursByEnseignant(@PathVariable Long enseignantId) {
        return coursRepository.findByEnseignantId(enseignantId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cours> getCoursById(@PathVariable Long id) {
        return coursRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Cours> createCours(@RequestBody Cours cours) {
        return ResponseEntity.ok(coursRepository.save(cours));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cours> updateCours(@PathVariable Long id, @RequestBody Cours cours) {
        cours.setId(id);
        return ResponseEntity.ok(coursRepository.save(cours));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCours(@PathVariable Long id) {
        coursRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ──────────────────── Séquences ────────────────────

    @GetMapping("/{coursId}/sequences")
    public List<Sequence> getSequencesByCours(@PathVariable Long coursId) {
        return sequenceRepository.findByCoursId(coursId);
    }

    @GetMapping("/sequences/{id}")
    public ResponseEntity<Sequence> getSequenceById(@PathVariable Long id) {
        return sequenceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{coursId}/sequences")
    public ResponseEntity<Sequence> createSequence(@PathVariable Long coursId, @RequestBody Sequence sequence) {
        coursRepository.findById(coursId).ifPresent(sequence::setCours);
        return ResponseEntity.ok(sequenceRepository.save(sequence));
    }

    @PutMapping("/sequences/{id}")
    public ResponseEntity<Sequence> updateSequence(@PathVariable Long id, @RequestBody Sequence sequence) {
        sequence.setId(id);
        return ResponseEntity.ok(sequenceRepository.save(sequence));
    }

    @DeleteMapping("/sequences/{id}")
    public ResponseEntity<?> deleteSequence(@PathVariable Long id) {
        sequenceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
