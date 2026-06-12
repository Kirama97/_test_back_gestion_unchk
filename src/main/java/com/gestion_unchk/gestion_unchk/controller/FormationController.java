package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.EmploiDuTemps;
import com.gestion_unchk.gestion_unchk.model.Formation;
import com.gestion_unchk.gestion_unchk.repository.EmploiDuTempsRepository;
import com.gestion_unchk.gestion_unchk.repository.FormationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class FormationController {

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private EmploiDuTempsRepository emploiDuTempsRepository;

    // Formations / Cours
    @GetMapping("/formations")
    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    @PostMapping("/formations")
    public ResponseEntity<Formation> createFormation(@RequestBody Formation formation) {
        Formation saved = formationRepository.save(formation);
        return ResponseEntity.ok(saved);
    }

    // Emplois du temps
    @GetMapping("/emplois-du-temps")
    public List<EmploiDuTemps> getEmploiDuTemps(@RequestParam(required = false) Long formationId) {
        if (formationId != null) {
            return emploiDuTempsRepository.findByFormationId(formationId);
        }
        return emploiDuTempsRepository.findAll();
    }

    @PostMapping("/emplois-du-temps")
    public ResponseEntity<EmploiDuTemps> createEmploiDuTemps(@RequestBody EmploiDuTemps emploiDuTemps) {
        EmploiDuTemps saved = emploiDuTempsRepository.save(emploiDuTemps);
        return ResponseEntity.ok(saved);
    }

    // Module 4: Réunions liées à la préparation des cours, suivi tutorat, évaluations
    @GetMapping("/reunions")
    public List<Map<String, Object>> getReunions() {
        Map<String, Object> r1 = new HashMap<>();
        r1.put("id", 1L);
        r1.put("titre", "Réunion de cadrage - Technologies Web");
        r1.put("type", "Préparation des cours");
        r1.put("date", "2026-06-15T10:00:00");
        r1.put("salle", "Virtuelle (Google Meet)");
        r1.put("statut", "Planifiée");

        Map<String, Object> r2 = new HashMap<>();
        r2.put("id", 2L);
        r2.put("titre", "Suivi tutorat collectif - Promo 8");
        r2.put("type", "Suivi tutorat");
        r2.put("date", "2026-06-18T15:00:00");
        r2.put("salle", "Virtuelle (Zoom)");
        r2.put("statut", "Planifiée");

        Map<String, Object> r3 = new HashMap<>();
        r3.put("id", 3L);
        r3.put("titre", "Préparation des examens Semestre 2");
        r3.put("type", "Préparation des évaluations");
        r3.put("date", "2026-06-22T09:00:00");
        r3.put("salle", "Virtuelle (Teams)");
        r3.put("statut", "Planifiée");

        return List.of(r1, r2, r3);
    }
}
