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

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

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
        return coursRepository.findById(coursId).map(cours -> {
            sequence.setCours(cours);
            Sequence saved = sequenceRepository.save(sequence);
            
            // Notify all students in the class of this course
            if (cours.getClasse() != null) {
                List<Etudiant> students = etudiantRepository.findByClasseId(cours.getClasse().getId());
                for (Etudiant student : students) {
                    if (student.getUtilisateur() != null) {
                        // 1. Create standard notification (bell)
                        Notification notif = new Notification();
                        boolean isDevoir = saved.getExerciceChemin() != null || 
                                           (saved.getTitre() != null && saved.getTitre().toLowerCase().contains("devoir"));
                        
                        notif.setTitre(isDevoir ? "Nouveau devoir disponible" : "Nouveau cours disponible");
                        notif.setDescription("La séquence '" + saved.getTitre() + "' a été ajoutée dans le cours " + cours.getMatiere().getNom() + ".");
                        notif.setCategory("schedule");
                        notif.setLu(false);
                        notif.setDestinataire(student.getUtilisateur());
                        notif.setDateCreation(java.time.LocalDateTime.now());
                        notificationRepository.save(notif);

                        // 2. Create message notification (balloon)
                        Notification msg = new Notification();
                        String senderName = cours.getEnseignant() != null ? 
                                           (cours.getEnseignant().getPrenom() + " " + cours.getEnseignant().getNom()) : 
                                           "Votre Enseignant";
                        msg.setTitre(senderName);
                        msg.setDescription("Bonjour, j'ai publié le cours/devoir '" + saved.getTitre() + "' pour aujourd'hui.");
                        msg.setCategory("message");
                        msg.setLu(false);
                        msg.setDestinataire(student.getUtilisateur());
                        msg.setDateCreation(java.time.LocalDateTime.now());
                        notificationRepository.save(msg);
                    }
                }
            }
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
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
