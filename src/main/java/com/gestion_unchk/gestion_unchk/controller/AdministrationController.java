package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.Budget;
import com.gestion_unchk.gestion_unchk.model.Document;
import com.gestion_unchk.gestion_unchk.model.Role;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.repository.BudgetRepository;
import com.gestion_unchk.gestion_unchk.repository.DocumentRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import com.gestion_unchk.gestion_unchk.model.Notification;
import com.gestion_unchk.gestion_unchk.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AdministrationController {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    
    @GetMapping("/documents")
    public List<Document> getAllDocuments(@RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) {
            return documentRepository.findByType(type);
        }
        return documentRepository.findAll();
    }

    @PostMapping("/documents")
    public ResponseEntity<Document> createDocument(@RequestBody Document document, Principal principal) {
        Utilisateur auteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        document.setAuteur(auteur);
        Document saved = documentRepository.save(document);

        
        List<Utilisateur> recipients = utilisateurRepository.findAll();
        for (Utilisateur user : recipients) {
            if (user.getRole() == Role.ETUDIANT || user.getRole() == Role.ENSEIGNANT || user.getRole() == Role.TUTEUR) {
                Notification notif = new Notification();
                notif.setTitre("Nouveau document disponible");
                notif.setDescription("Le document '" + saved.getTitre() + "' a été publié.");
                notif.setCategory("communication");
                notif.setDestinataire(user);
                notif.setDateCreation(LocalDateTime.now());
                notif.setLu(false);
                notificationRepository.save(notif);
            }
        }

        return ResponseEntity.ok(saved);
    }

    
    @GetMapping("/budgets")
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @PostMapping("/budgets")
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        Budget saved = budgetRepository.save(budget);
        return ResponseEntity.ok(saved);
    }

    
    @GetMapping("/personnel")
    public List<Utilisateur> getPersonnel() {
        
        return utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ETUDIANT)
                .collect(Collectors.toList());
    }

    @PostMapping("/personnel")
    public ResponseEntity<Utilisateur> createPersonnel(@RequestBody Utilisateur user) {
        
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        Utilisateur saved = utilisateurRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/personnel/{id}")
    public ResponseEntity<?> deletePersonnel(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/personnel/{id}")
    public ResponseEntity<?> updatePersonnel(@PathVariable Long id, @RequestBody Utilisateur userDetails) {
        return utilisateurRepository.findById(id).map(existingUser -> {
            existingUser.setNom(userDetails.getNom());
            existingUser.setPrenom(userDetails.getPrenom());
            existingUser.setEmail(userDetails.getEmail());
            existingUser.setTelephone(userDetails.getTelephone());
            existingUser.setDepartement(userDetails.getDepartement());
            existingUser.setStatut(userDetails.getStatut());
            if (userDetails.getPhotoProfil() != null) {
                existingUser.setPhotoProfil(userDetails.getPhotoProfil());
            }
            if (userDetails.getMotDePasse() != null && !userDetails.getMotDePasse().isEmpty()) {
                existingUser.setMotDePasse(passwordEncoder.encode(userDetails.getMotDePasse()));
            }
            Utilisateur updated = utilisateurRepository.save(existingUser);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/documents/{id}")
    public ResponseEntity<Document> updateDocument(@PathVariable Long id, @RequestBody Document docDetails) {
        return documentRepository.findById(id).map(existing -> {
            existing.setTitre(docDetails.getTitre());
            existing.setDescription(docDetails.getDescription());
            existing.setType(docDetails.getType());
            if (docDetails.getCheminFichier() != null) {
                existing.setCheminFichier(docDetails.getCheminFichier());
            }
            return ResponseEntity.ok(documentRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/documents/{id}")
    public ResponseEntity<?> deleteDocument(@PathVariable Long id) {
        documentRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/budgets/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable Long id, @RequestBody Budget budgetDetails) {
        return budgetRepository.findById(id).map(existing -> {
            existing.setAnnee(budgetDetails.getAnnee());
            existing.setType(budgetDetails.getType());
            existing.setMontant(budgetDetails.getMontant());
            existing.setDescription(budgetDetails.getDescription());
            if (budgetDetails.getCheminFichier() != null) {
                existing.setCheminFichier(budgetDetails.getCheminFichier());
            }
            return ResponseEntity.ok(budgetRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/budgets/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        budgetRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
