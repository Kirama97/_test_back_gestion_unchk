package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.Etudiant;
import com.gestion_unchk.gestion_unchk.model.SuiviEtudiant;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.repository.EtudiantRepository;
import com.gestion_unchk.gestion_unchk.repository.SuiviEtudiantRepository;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api/exports")
public class ExportController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private SuiviEtudiantRepository suiviEtudiantRepository;

    @GetMapping("/personnel/csv")
    public ResponseEntity<byte[]> exportPersonnelCsv() {
        List<Utilisateur> users = utilisateurRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Nom;Prenom;Email;Role;Departement;Statut\n");

        for (Utilisateur u : users) {
            csv.append(u.getId()).append(";")
               .append(u.getNom()).append(";")
               .append(u.getPrenom()).append(";")
               .append(u.getEmail()).append(";")
               .append(u.getRole().name()).append(";")
               .append(u.getDepartement() != null ? u.getDepartement() : "").append(";")
               .append(u.getStatut() != null ? u.getStatut() : "").append("\n");
        }

        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=personnel_unchk.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }

    @GetMapping("/etudiants/csv")
    public ResponseEntity<byte[]> exportEtudiantsCsv() {
        List<Etudiant> etudiants = etudiantRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("INE;Nom;Prenom;Email;Filiere;Promo;Annee Debut;Annee Sortie;Diplomes\n");

        for (Etudiant e : etudiants) {
            csv.append(e.getIne()).append(";")
               .append(e.getUtilisateur().getNom()).append(";")
               .append(e.getUtilisateur().getPrenom()).append(";")
               .append(e.getUtilisateur().getEmail()).append(";")
               .append(e.getFiliere()).append(";")
               .append(e.getPromo()).append(";")
               .append(e.getAnneeDebut()).append(";")
               .append(e.getAnneeSortie() != null ? e.getAnneeSortie() : "").append(";")
               .append(e.getDiplomes() != null ? e.getDiplomes() : "").append("\n");
        }

        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etudiants_unchk.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }

    @GetMapping("/suivi/csv")
    public ResponseEntity<byte[]> exportSuiviCsv() {
        List<SuiviEtudiant> suivis = suiviEtudiantRepository.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ID;Nom;Prenom;INE;Filiere;Statut Insertion;Entreprise;Salaire Initial;Contacts\n");

        for (SuiviEtudiant s : suivis) {
            csv.append(s.getId()).append(";")
               .append(s.getEtudiant().getUtilisateur().getNom()).append(";")
               .append(s.getEtudiant().getUtilisateur().getPrenom()).append(";")
               .append(s.getEtudiant().getIne()).append(";")
               .append(s.getEtudiant().getFiliere()).append(";")
               .append(s.getStatutInsertion()).append(";")
               .append(s.getEntreprise() != null ? s.getEntreprise() : "").append(";")
               .append(s.getSalaireInitial() != null ? s.getSalaireInitial().toString() : "").append(";")
               .append(s.getRegistreContact() != null ? s.getRegistreContact() : "").append("\n");
        }

        byte[] content = csv.toString().getBytes(StandardCharsets.UTF_8);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suivi_insertion_unchk.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(content);
    }
}
