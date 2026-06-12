package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.CompteRendu;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.repository.CompteRenduRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CommunicationController {

    @Autowired
    private CompteRenduRepository compteRenduRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping("/comptes-rendus")
    public List<CompteRendu> getAllComptesRendus() {
        return compteRenduRepository.findAll();
    }

    @PostMapping("/comptes-rendus")
    public ResponseEntity<CompteRendu> createCompteRendu(@RequestBody CompteRendu compteRendu, Principal principal) {
        Utilisateur auteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        compteRendu.setAuteur(auteur);
        CompteRendu saved = compteRenduRepository.save(compteRendu);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/notifications")
    public List<Map<String, Object>> getNotifications(Principal principal) {
        // Mocking dynamic notifications
        Map<String, Object> n1 = new HashMap<>();
        n1.put("id", 1L);
        n1.put("title", "Nouvel emploi du temps");
        n1.put("description", "L'emploi du temps du semestre a été mis à jour.");
        n1.put("time", "Il y a 10 min");
        n1.put("read", false);
        n1.put("category", "schedule");

        Map<String, Object> n2 = new HashMap<>();
        n2.put("id", 2L);
        n2.put("title", "Note publiée");
        n2.put("description", "Votre note pour le module 'Technologies Web' est disponible.");
        n2.put("time", "Il y a 2 h");
        n2.put("read", false);
        n2.put("category", "exam");

        Map<String, Object> n3 = new HashMap<>();
        n3.put("id", 3L);
        n3.put("title", "Circulaire centrale");
        n3.put("description", "La note de service concernant la semaine culturelle est disponible.");
        n3.put("time", "Hier");
        n3.put("read", true);
        n3.put("category", "communication");

        return List.of(n1, n2, n3);
    }
}
