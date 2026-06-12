package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.dto.EtudiantDto;
import com.gestion_unchk.gestion_unchk.model.*;
import com.gestion_unchk.gestion_unchk.repository.EtudiantRepository;
import com.gestion_unchk.gestion_unchk.repository.NoteRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class EtudiantController {

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/etudiants")
    public List<Etudiant> getAllEtudiants() {
        return etudiantRepository.findAll();
    }

    @GetMapping("/etudiants/{id}")
    public ResponseEntity<Etudiant> getEtudiantById(@PathVariable Long id) {
        return etudiantRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/etudiants")
    public ResponseEntity<Etudiant> createEtudiant(@RequestBody EtudiantDto dto) {
        // Create Utilisateur
        Utilisateur user = new Utilisateur();
        user.setNom(dto.getNom());
        user.setPrenom(dto.getPrenom());
        user.setEmail(dto.getEmail());
        user.setMotDePasse(passwordEncoder.encode(dto.getMotDePasse() != null ? dto.getMotDePasse() : "Passer123"));
        user.setRole(Role.ETUDIANT);
        user.setDepartement(dto.getFiliere());
        user.setStatut("Actif");
        
        Utilisateur savedUser = utilisateurRepository.save(user);

        // Create Etudiant
        Etudiant etudiant = new Etudiant();
        etudiant.setId(savedUser.getId());
        etudiant.setUtilisateur(savedUser);
        etudiant.setIne(dto.getIne());
        etudiant.setDateNaissance(dto.getDateNaissance());
        etudiant.setFiliere(dto.getFiliere());
        etudiant.setPromo(dto.getPromo());
        etudiant.setAnneeDebut(dto.getAnneeDebut());
        etudiant.setAnneeSortie(dto.getAnneeSortie());
        etudiant.setDiplomes(dto.getDiplomes());
        etudiant.setAutresFormations(dto.getAutresFormations());

        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        return ResponseEntity.ok(savedEtudiant);
    }

    @DeleteMapping("/etudiants/{id}")
    public ResponseEntity<?> deleteEtudiant(@PathVariable Long id) {
        // Due to CASCADE, deleting the utilisateur will delete the student details too
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // Notes Management
    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/notes/etudiant/{etudiantId}")
    public List<Note> getNotesByEtudiant(@PathVariable Long etudiantId) {
        return noteRepository.findByEtudiantId(etudiantId);
    }

    @PostMapping("/notes")
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note saved = noteRepository.save(note);
        return ResponseEntity.ok(saved);
    }
}
