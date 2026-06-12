package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.Budget;
import com.gestion_unchk.gestion_unchk.model.Document;
import com.gestion_unchk.gestion_unchk.model.Role;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.repository.BudgetRepository;
import com.gestion_unchk.gestion_unchk.repository.DocumentRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
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
    private PasswordEncoder passwordEncoder;

    // Document archiving
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
        return ResponseEntity.ok(saved);
    }

    // Budget Management
    @GetMapping("/budgets")
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAll();
    }

    @PostMapping("/budgets")
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        Budget saved = budgetRepository.save(budget);
        return ResponseEntity.ok(saved);
    }

    // HR - Personnel Directory
    @GetMapping("/personnel")
    public List<Utilisateur> getPersonnel() {
        // Return all staff members (non-students)
        return utilisateurRepository.findAll().stream()
                .filter(u -> u.getRole() != Role.ETUDIANT)
                .collect(Collectors.toList());
    }

    @PostMapping("/personnel")
    public ResponseEntity<Utilisateur> createPersonnel(@RequestBody Utilisateur user) {
        // Encrypt the password before saving
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));
        Utilisateur saved = utilisateurRepository.save(user);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/personnel/{id}")
    public ResponseEntity<?> deletePersonnel(@PathVariable Long id) {
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
