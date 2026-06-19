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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
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

    // ──────────────────── Personnel Exports ────────────────────

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

    @GetMapping("/personnel/pdf")
    public ResponseEntity<byte[]> exportPersonnelPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4);
            com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font fontTitle = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(16);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Liste du Personnel - UNCHK", fontTitle);
            title.setAlignment(com.lowagie.text.Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(6);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Nom");
            table.addCell("Prenom");
            table.addCell("Email");
            table.addCell("Role");
            table.addCell("Departement");

            List<Utilisateur> users = utilisateurRepository.findAll();
            for (Utilisateur u : users) {
                table.addCell(String.valueOf(u.getId()));
                table.addCell(u.getNom());
                table.addCell(u.getPrenom());
                table.addCell(u.getEmail());
                table.addCell(u.getRole().name());
                table.addCell(u.getDepartement() != null ? u.getDepartement() : "");
            }

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=personnel_unchk.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/personnel/excel")
    public ResponseEntity<byte[]> exportPersonnelExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Personnel");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nom");
            header.createCell(2).setCellValue("Prenom");
            header.createCell(3).setCellValue("Email");
            header.createCell(4).setCellValue("Role");
            header.createCell(5).setCellValue("Departement");

            List<Utilisateur> users = utilisateurRepository.findAll();
            int rowIdx = 1;
            for (Utilisateur u : users) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(u.getId());
                row.createCell(1).setCellValue(u.getNom());
                row.createCell(2).setCellValue(u.getPrenom());
                row.createCell(3).setCellValue(u.getEmail());
                row.createCell(4).setCellValue(u.getRole().name());
                row.createCell(5).setCellValue(u.getDepartement() != null ? u.getDepartement() : "");
            }

            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=personnel_unchk.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ──────────────────── Etudiants Exports ────────────────────

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

    @GetMapping("/etudiants/pdf")
    public ResponseEntity<byte[]> exportEtudiantsPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
            com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font fontTitle = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(16);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Liste des Etudiants - UNCHK", fontTitle);
            title.setAlignment(com.lowagie.text.Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);
            table.addCell("INE");
            table.addCell("Nom");
            table.addCell("Prenom");
            table.addCell("Email");
            table.addCell("Filiere");
            table.addCell("Promo");
            table.addCell("Debut");
            table.addCell("Sortie");

            List<Etudiant> etudiants = etudiantRepository.findAll();
            for (Etudiant e : etudiants) {
                table.addCell(e.getIne());
                table.addCell(e.getUtilisateur().getNom());
                table.addCell(e.getUtilisateur().getPrenom());
                table.addCell(e.getUtilisateur().getEmail());
                table.addCell(e.getFiliere());
                table.addCell(e.getPromo());
                table.addCell(String.valueOf(e.getAnneeDebut()));
                table.addCell(e.getAnneeSortie() != null ? String.valueOf(e.getAnneeSortie()) : "");
            }

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etudiants_unchk.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/etudiants/excel")
    public ResponseEntity<byte[]> exportEtudiantsExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Etudiants");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("INE");
            header.createCell(1).setCellValue("Nom");
            header.createCell(2).setCellValue("Prenom");
            header.createCell(3).setCellValue("Email");
            header.createCell(4).setCellValue("Filiere");
            header.createCell(5).setCellValue("Promo");
            header.createCell(6).setCellValue("Annee Debut");
            header.createCell(7).setCellValue("Annee Sortie");

            List<Etudiant> etudiants = etudiantRepository.findAll();
            int rowIdx = 1;
            for (Etudiant e : etudiants) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getIne());
                row.createCell(1).setCellValue(e.getUtilisateur().getNom());
                row.createCell(2).setCellValue(e.getUtilisateur().getPrenom());
                row.createCell(3).setCellValue(e.getUtilisateur().getEmail());
                row.createCell(4).setCellValue(e.getFiliere());
                row.createCell(5).setCellValue(e.getPromo());
                row.createCell(6).setCellValue(e.getAnneeDebut());
                row.createCell(7).setCellValue(e.getAnneeSortie() != null ? String.valueOf(e.getAnneeSortie()) : "");
            }

            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=etudiants_unchk.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ──────────────────── Suivi Insertion Exports ────────────────────

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

    @GetMapping("/suivi/pdf")
    public ResponseEntity<byte[]> exportSuiviPdf() {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            com.lowagie.text.Document document = new com.lowagie.text.Document(com.lowagie.text.PageSize.A4.rotate());
            com.lowagie.text.pdf.PdfWriter.getInstance(document, out);
            document.open();

            com.lowagie.text.Font fontTitle = com.lowagie.text.FontFactory.getFont(com.lowagie.text.FontFactory.HELVETICA_BOLD);
            fontTitle.setSize(16);
            com.lowagie.text.Paragraph title = new com.lowagie.text.Paragraph("Suivi Insertion Professionnelle - UNCHK", fontTitle);
            title.setAlignment(com.lowagie.text.Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new com.lowagie.text.Paragraph(" "));

            com.lowagie.text.pdf.PdfPTable table = new com.lowagie.text.pdf.PdfPTable(8);
            table.setWidthPercentage(100);
            table.addCell("ID");
            table.addCell("Nom");
            table.addCell("Prenom");
            table.addCell("INE");
            table.addCell("Filiere");
            table.addCell("Statut Insertion");
            table.addCell("Entreprise");
            table.addCell("Salaire Initial");

            List<SuiviEtudiant> suivis = suiviEtudiantRepository.findAll();
            for (SuiviEtudiant s : suivis) {
                table.addCell(String.valueOf(s.getId()));
                table.addCell(s.getEtudiant().getUtilisateur().getNom());
                table.addCell(s.getEtudiant().getUtilisateur().getPrenom());
                table.addCell(s.getEtudiant().getIne());
                table.addCell(s.getEtudiant().getFiliere());
                table.addCell(s.getStatutInsertion());
                table.addCell(s.getEntreprise() != null ? s.getEntreprise() : "");
                table.addCell(s.getSalaireInitial() != null ? String.valueOf(s.getSalaireInitial()) : "");
            }

            document.add(table);
            document.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suivi_insertion_unchk.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/suivi/excel")
    public ResponseEntity<byte[]> exportSuiviExcel() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Suivi Insertion");
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("ID");
            header.createCell(1).setCellValue("Nom");
            header.createCell(2).setCellValue("Prenom");
            header.createCell(3).setCellValue("INE");
            header.createCell(4).setCellValue("Filiere");
            header.createCell(5).setCellValue("Statut Insertion");
            header.createCell(6).setCellValue("Entreprise");
            header.createCell(7).setCellValue("Salaire Initial");

            List<SuiviEtudiant> suivis = suiviEtudiantRepository.findAll();
            int rowIdx = 1;
            for (SuiviEtudiant s : suivis) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(s.getId());
                row.createCell(1).setCellValue(s.getEtudiant().getUtilisateur().getNom());
                row.createCell(2).setCellValue(s.getEtudiant().getUtilisateur().getPrenom());
                row.createCell(3).setCellValue(s.getEtudiant().getIne());
                row.createCell(4).setCellValue(s.getEtudiant().getFiliere());
                row.createCell(5).setCellValue(s.getStatutInsertion());
                row.createCell(6).setCellValue(s.getEntreprise() != null ? s.getEntreprise() : "");
                row.createCell(7).setCellValue(s.getSalaireInitial() != null ? s.getSalaireInitial().doubleValue() : 0.0);
            }

            workbook.write(out);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suivi_insertion_unchk.xlsx")
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(out.toByteArray());
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
