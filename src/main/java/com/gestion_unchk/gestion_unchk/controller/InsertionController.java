package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.Partenaire;
import com.gestion_unchk.gestion_unchk.model.SuiviEtudiant;
import com.gestion_unchk.gestion_unchk.repository.PartenaireRepository;
import com.gestion_unchk.gestion_unchk.repository.SuiviEtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class InsertionController {

    @Autowired
    private SuiviEtudiantRepository suiviEtudiantRepository;

    @Autowired
    private PartenaireRepository partenaireRepository;

    // Student professional follow-up
    @GetMapping("/insertion/suivi")
    public List<SuiviEtudiant> getAllSuiviEtudiants() {
        return suiviEtudiantRepository.findAll();
    }

    @PostMapping("/insertion/suivi")
    public ResponseEntity<SuiviEtudiant> createOrUpdateSuiviEtudiant(@RequestBody SuiviEtudiant suiviEtudiant) {
        // If a suivi already exists for the student, update it
        if (suiviEtudiant.getEtudiant() != null && suiviEtudiant.getEtudiant().getId() != null) {
            suiviEtudiantRepository.findByEtudiantId(suiviEtudiant.getEtudiant().getId())
                    .ifPresent(existing -> suiviEtudiant.setId(existing.getId()));
        }
        SuiviEtudiant saved = suiviEtudiantRepository.save(suiviEtudiant);
        return ResponseEntity.ok(saved);
    }

    // Statistics for Insertion Module
    @GetMapping("/insertion/stats")
    public ResponseEntity<Map<String, Object>> getInsertionStats() {
        List<SuiviEtudiant> suiviList = suiviEtudiantRepository.findAll();
        
        long autoEmploi = 0;
        long salarie = 0;
        long enRecherche = 0;
        
        for (SuiviEtudiant s : suiviList) {
            if ("AUTO_EMPLOI".equalsIgnoreCase(s.getStatutInsertion())) {
                autoEmploi++;
            } else if ("SALARIE".equalsIgnoreCase(s.getStatutInsertion())) {
                salarie++;
            } else {
                enRecherche++;
            }
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("autoEmploi", autoEmploi);
        stats.put("salarie", salarie);
        stats.put("enRecherche", enRecherche);
        stats.put("totalSuivis", suiviList.size());
        
        return ResponseEntity.ok(stats);
    }

    // Partners management
    @GetMapping("/partenaires")
    public List<Partenaire> getAllPartenaires() {
        return partenaireRepository.findAll();
    }

    @PostMapping("/partenaires")
    public ResponseEntity<Partenaire> createPartenaire(@RequestBody Partenaire partenaire) {
        Partenaire saved = partenaireRepository.save(partenaire);
        return ResponseEntity.ok(saved);
    }
}
