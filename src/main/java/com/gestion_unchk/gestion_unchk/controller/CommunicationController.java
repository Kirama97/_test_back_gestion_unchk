package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.CompteRendu;
import com.gestion_unchk.gestion_unchk.model.Notification;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.model.Role;
import com.gestion_unchk.gestion_unchk.repository.CompteRenduRepository;
import com.gestion_unchk.gestion_unchk.repository.NotificationRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommunicationController {

    @Autowired
    private CompteRenduRepository compteRenduRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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

        // Generate notification for all students and teachers
        List<Utilisateur> recipients = utilisateurRepository.findAll();
        for (Utilisateur user : recipients) {
            if (user.getRole() == Role.ETUDIANT || user.getRole() == Role.ENSEIGNANT || user.getRole() == Role.TUTEUR) {
                Notification notif = new Notification();
                notif.setTitre("Nouveau compte rendu disponible");
                notif.setDescription("Le compte rendu '" + saved.getTitre() + "' a été publié.");
                notif.setCategory("communication");
                notif.setDestinataire(user);
                notif.setDateCreation(LocalDateTime.now());
                notif.setLu(false);
                notificationRepository.save(notif);
            }
        }

        return ResponseEntity.ok(saved);
    }

    @GetMapping("/notifications")
    public List<Notification> getNotifications(Principal principal) {
        if (principal == null) {
            return List.of();
        }
        return notificationRepository.findByDestinataireEmailOrderByDateCreationDesc(principal.getName());
    }

    @PutMapping("/notifications/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        return notificationRepository.findById(id).map(notif -> {
            notif.setLu(true);
            notificationRepository.save(notif);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
