package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.dto.EtudiantDto;
import com.gestion_unchk.gestion_unchk.model.*;
import com.gestion_unchk.gestion_unchk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
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
    private EmploiDuTempsRepository emploiDuTempsRepository;

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired(required = false)
    private ClasseRepository classeRepository;

    @Autowired(required = false)
    private FiliereRepository filiereRepository;

    @Autowired(required = false)
    private PromotionRepository promotionRepository;

    // ──────────────────── Profil étudiant connecté ────────────────────

    @GetMapping("/etudiant/me")
    public ResponseEntity<?> getMyProfile(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        Etudiant etudiant = etudiantRepository.findByUtilisateurId(utilisateur.getId())
                .orElse(null);

        if (etudiant == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(etudiant);
    }

    // ──────────────────── CRUD Étudiants ────────────────────

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

    @GetMapping("/etudiants/classe/{classeId}")
    public List<Etudiant> getEtudiantsByClasse(@PathVariable Long classeId) {
        return etudiantRepository.findByClasseId(classeId);
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
        user.setTelephone(dto.getTelephone());
        user.setStatut("Actif");
        user.setPhotoProfil(dto.getPhotoProfil());
        
        Utilisateur savedUser = utilisateurRepository.save(user);

        // Create Etudiant
        Etudiant etudiant = new Etudiant();
        etudiant.setId(savedUser.getId());
        etudiant.setUtilisateur(savedUser);
        etudiant.setIne(dto.getIne());
        etudiant.setDateNaissance(dto.getDateNaissance());
        etudiant.setFiliere(dto.getFiliere());
        etudiant.setPromo(dto.getPromo());
        etudiant.setNiveauEtude(dto.getNiveauEtude());
        etudiant.setAdresse(dto.getAdresse());
        etudiant.setGenre(dto.getGenre());
        etudiant.setAnneeDebut(dto.getAnneeDebut());
        etudiant.setAnneeSortie(dto.getAnneeSortie());
        etudiant.setDiplomes(dto.getDiplomes());
        etudiant.setAutresFormations(dto.getAutresFormations());

        // Set academic relationships
        if (dto.getClasseId() != null && classeRepository != null) {
            classeRepository.findById(dto.getClasseId()).ifPresent(etudiant::setClasse);
        }
        if (dto.getFiliereId() != null && filiereRepository != null) {
            filiereRepository.findById(dto.getFiliereId()).ifPresent(etudiant::setFiliereObj);
        }
        if (dto.getPromotionId() != null && promotionRepository != null) {
            promotionRepository.findById(dto.getPromotionId()).ifPresent(etudiant::setPromotionObj);
        }

        Etudiant savedEtudiant = etudiantRepository.save(etudiant);
        return ResponseEntity.ok(savedEtudiant);
    }

    @PutMapping("/etudiants/{id}")
    public ResponseEntity<?> updateEtudiant(@PathVariable Long id, @RequestBody EtudiantDto dto) {
        Etudiant etudiant = etudiantRepository.findById(id).orElse(null);
        if (etudiant == null) return ResponseEntity.notFound().build();

        Utilisateur user = etudiant.getUtilisateur();
        if (dto.getNom() != null) user.setNom(dto.getNom());
        if (dto.getPrenom() != null) user.setPrenom(dto.getPrenom());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getTelephone() != null) user.setTelephone(dto.getTelephone());
        if (dto.getPhotoProfil() != null) user.setPhotoProfil(dto.getPhotoProfil());
        utilisateurRepository.save(user);

        if (dto.getDateNaissance() != null) etudiant.setDateNaissance(dto.getDateNaissance());
        if (dto.getFiliere() != null) etudiant.setFiliere(dto.getFiliere());
        if (dto.getPromo() != null) etudiant.setPromo(dto.getPromo());
        if (dto.getNiveauEtude() != null) etudiant.setNiveauEtude(dto.getNiveauEtude());
        if (dto.getAdresse() != null) etudiant.setAdresse(dto.getAdresse());
        if (dto.getGenre() != null) etudiant.setGenre(dto.getGenre());

        if (dto.getClasseId() != null && classeRepository != null) {
            classeRepository.findById(dto.getClasseId()).ifPresent(etudiant::setClasse);
        }
        if (dto.getFiliereId() != null && filiereRepository != null) {
            filiereRepository.findById(dto.getFiliereId()).ifPresent(etudiant::setFiliereObj);
        }
        if (dto.getPromotionId() != null && promotionRepository != null) {
            promotionRepository.findById(dto.getPromotionId()).ifPresent(etudiant::setPromotionObj);
        }

        return ResponseEntity.ok(etudiantRepository.save(etudiant));
    }

    @DeleteMapping("/etudiants/{id}")
    public ResponseEntity<?> deleteEtudiant(@PathVariable Long id) {
        // Due to CASCADE, deleting the utilisateur will delete the student details too
        utilisateurRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ──────────────────── Notes ────────────────────

    @GetMapping("/notes")
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    @GetMapping("/notes/etudiant/{etudiantId}")
    public List<Note> getNotesByEtudiant(@PathVariable Long etudiantId) {
        return noteRepository.findByEtudiantId(etudiantId);
    }

    @GetMapping("/notes/me")
    public ResponseEntity<?> getMyNotes(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        Utilisateur user = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        return ResponseEntity.ok(noteRepository.findByEtudiantId(user.getId()));
    }

    @PostMapping("/notes")
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        Note saved = noteRepository.save(note);
        
        // Find student and send notification
        etudiantRepository.findById(saved.getEtudiant().getId()).ifPresent(etudiant -> {
            Notification notif = new Notification();
            notif.setTitre("Nouvelle note publiée");
            String matName = saved.getMatiere() != null ? saved.getMatiere().getNom() : "un module";
            notif.setDescription("Votre note pour " + matName + " a été publiée. Valeur : " + saved.getValeur() + "/20.");
            notif.setCategory("exam");
            notif.setLu(false);
            notif.setDestinataire(etudiant.getUtilisateur());
            notif.setDateCreation(java.time.LocalDateTime.now());
            notificationRepository.save(notif);
        });
        
        return ResponseEntity.ok(saved);
    }

    // ──────────────────── Emploi du temps ────────────────────

    @GetMapping("/emploi-du-temps/me")
    public ResponseEntity<?> getMyEmploiDuTemps(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        Utilisateur user = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        Etudiant etudiant = etudiantRepository.findByUtilisateurId(user.getId()).orElse(null);
        if (etudiant == null || etudiant.getClasse() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(emploiDuTempsRepository.findByClasseId(etudiant.getClasse().getId()));
    }

    @GetMapping("/emploi-du-temps/classe/{classeId}")
    public List<EmploiDuTemps> getEmploiDuTempsByClasse(@PathVariable Long classeId) {
        return emploiDuTempsRepository.findByClasseId(classeId);
    }

    @GetMapping("/emploi-du-temps/enseignant/{enseignantId}")
    public List<EmploiDuTemps> getEmploiDuTempsByEnseignant(@PathVariable Long enseignantId) {
        return emploiDuTempsRepository.findByCoursEnseignantId(enseignantId);
    }

    // ──────────────────── Cours de l'étudiant ────────────────────

    @GetMapping("/cours/me")
    public ResponseEntity<?> getMyCours(Principal principal) {
        if (principal == null) return ResponseEntity.status(401).build();
        Utilisateur user = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        Etudiant etudiant = etudiantRepository.findByUtilisateurId(user.getId()).orElse(null);
        if (etudiant == null || etudiant.getClasse() == null) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(coursRepository.findByClasseId(etudiant.getClasse().getId()));
    }
}
