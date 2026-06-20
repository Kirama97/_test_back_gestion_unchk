package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.Annonce;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.repository.AnnonceRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/api/annonces")
public class AnnonceController {

    @Autowired
    private AnnonceRepository annonceRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @GetMapping
    public List<Annonce> getAllAnnonces() {
        return annonceRepository.findAllByOrderByDatePublicationDesc();
    }

    @GetMapping("/type/{type}")
    public List<Annonce> getAnnoncesByType(@PathVariable String type) {
        return annonceRepository.findByTypeOrderByDatePublicationDesc(type);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Annonce> getAnnonceById(@PathVariable Long id) {
        return annonceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Annonce> createAnnonce(@RequestBody Annonce annonce, Principal principal) {
        Utilisateur auteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        annonce.setAuteur(auteur);
        annonce.setDatePublication(LocalDateTime.now());
        return ResponseEntity.ok(annonceRepository.save(annonce));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Annonce> updateAnnonce(@PathVariable Long id, @RequestBody Annonce annonce, Principal principal) {
        Utilisateur auteur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        annonce.setId(id);
        annonce.setAuteur(auteur);
        return ResponseEntity.ok(annonceRepository.save(annonce));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnnonce(@PathVariable Long id) {
        annonceRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
