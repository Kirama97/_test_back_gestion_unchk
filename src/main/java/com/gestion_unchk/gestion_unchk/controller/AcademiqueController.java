package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.*;
import com.gestion_unchk.gestion_unchk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/academique")
public class AcademiqueController {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private FiliereRepository filiereRepository;

    @Autowired
    private ClasseRepository classeRepository;

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    

    @GetMapping("/promotions")
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    @PostMapping("/promotions")
    public ResponseEntity<Promotion> createPromotion(@RequestBody Promotion promotion) {
        return ResponseEntity.ok(promotionRepository.save(promotion));
    }

    @PutMapping("/promotions/{id}")
    public ResponseEntity<Promotion> updatePromotion(@PathVariable Long id, @RequestBody Promotion promotion) {
        promotion.setId(id);
        return ResponseEntity.ok(promotionRepository.save(promotion));
    }

    @DeleteMapping("/promotions/{id}")
    public ResponseEntity<?> deletePromotion(@PathVariable Long id) {
        promotionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    

    @GetMapping("/filieres")
    public List<Filiere> getAllFilieres() {
        return filiereRepository.findAll();
    }

    @PostMapping("/filieres")
    public ResponseEntity<Filiere> createFiliere(@RequestBody Filiere filiere) {
        return ResponseEntity.ok(filiereRepository.save(filiere));
    }

    @PutMapping("/filieres/{id}")
    public ResponseEntity<Filiere> updateFiliere(@PathVariable Long id, @RequestBody Filiere filiere) {
        filiere.setId(id);
        return ResponseEntity.ok(filiereRepository.save(filiere));
    }

    @DeleteMapping("/filieres/{id}")
    public ResponseEntity<?> deleteFiliere(@PathVariable Long id) {
        filiereRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    

    @GetMapping("/classes")
    public List<Classe> getAllClasses() {
        return classeRepository.findAll();
    }

    @GetMapping("/classes/promotion/{promotionId}")
    public List<Classe> getClassesByPromotion(@PathVariable Long promotionId) {
        return classeRepository.findByPromotionId(promotionId);
    }

    @GetMapping("/classes/filiere/{filiereId}")
    public List<Classe> getClassesByFiliere(@PathVariable Long filiereId) {
        return classeRepository.findByFiliereId(filiereId);
    }

    @PostMapping("/classes")
    public ResponseEntity<Classe> createClasse(@RequestBody Classe classe) {
        return ResponseEntity.ok(classeRepository.save(classe));
    }

    @PutMapping("/classes/{id}")
    public ResponseEntity<Classe> updateClasse(@PathVariable Long id, @RequestBody Classe classeDetails) {
        return classeRepository.findById(id).map(existing -> {
            existing.setNom(classeDetails.getNom());
            existing.setNiveauEtude(classeDetails.getNiveauEtude());
            
            if (classeDetails.getPromotion() != null) {
                existing.setPromotion(classeDetails.getPromotion());
            }
            if (classeDetails.getFiliere() != null) {
                existing.setFiliere(classeDetails.getFiliere());
            }
            
            Utilisateur oldTuteur = existing.getTuteur();
            Utilisateur newTuteur = classeDetails.getTuteur();
            
            existing.setTuteur(newTuteur);
            Classe saved = classeRepository.save(existing);
            
            
            if (newTuteur != null && (oldTuteur == null || !oldTuteur.getId().equals(newTuteur.getId()))) {
                utilisateurRepository.findById(newTuteur.getId()).ifPresent(tutor -> {
                    Notification notif = new Notification();
                    notif.setTitre("Nouvelle classe affectée");
                    notif.setDescription("La classe '" + saved.getNom() + "' vous a été affectée pour le suivi du tutorat.");
                    notif.setCategory("schedule");
                    notif.setLu(false);
                    notif.setDestinataire(tutor);
                    notif.setDateCreation(java.time.LocalDateTime.now());
                    notificationRepository.save(notif);
                });
            }
            
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/classes/{id}")
    public ResponseEntity<?> deleteClasse(@PathVariable Long id) {
        classeRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    

    @GetMapping("/matieres")
    public List<Matiere> getAllMatieres() {
        return matiereRepository.findAll();
    }

    @PostMapping("/matieres")
    public ResponseEntity<Matiere> createMatiere(@RequestBody Matiere matiere) {
        return ResponseEntity.ok(matiereRepository.save(matiere));
    }

    @DeleteMapping("/matieres/{id}")
    public ResponseEntity<?> deleteMatiere(@PathVariable Long id) {
        matiereRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
