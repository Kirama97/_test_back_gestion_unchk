package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.model.*;
import com.gestion_unchk.gestion_unchk.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FormationController {

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private EmploiDuTempsRepository emploiDuTempsRepository;

    @Autowired
    private ReunionRepository reunionRepository;

    
    @GetMapping("/formations")
    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    @PostMapping("/formations")
    public ResponseEntity<Formation> createFormation(@RequestBody Formation formation) {
        Formation saved = formationRepository.save(formation);
        return ResponseEntity.ok(saved);
    }

    @Autowired
    private CoursRepository coursRepository;

    @Autowired
    private EtudiantRepository etudiantRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private void notifyClassStudents(Classe classObj, EmploiDuTemps schedule, boolean isNew) {
        if (classObj != null && classObj.getId() != null) {
            List<Etudiant> students = etudiantRepository.findByClasseId(classObj.getId());
            String timeStr = (schedule.getHeureDebut() != null ? schedule.getHeureDebut().toString() : "") 
                + " - " + (schedule.getHeureFin() != null ? schedule.getHeureFin().toString() : "");
            String prefix = isNew ? "Le cours de '" : "Le cours planifié de '";
            String action = isNew ? "' a été planifié le " : "' a été modifié. Nouveau créneau : ";
            String desc = prefix + schedule.getMatiere() + action + schedule.getJourSemaine() + " de " + timeStr + " (Salle: " + schedule.getSalle() + ").";
            
            for (Etudiant student : students) {
                if (student.getUtilisateur() != null) {
                    Notification notif = new Notification();
                    notif.setTitre(isNew ? "Nouveau cours planifié" : "Cours planifié modifié");
                    notif.setDescription(desc);
                    notif.setCategory("schedule");
                    notif.setLu(false);
                    notif.setDestinataire(student.getUtilisateur());
                    notif.setDateCreation(java.time.LocalDateTime.now());
                    notificationRepository.save(notif);
                }
            }
        }
    }

    
    @GetMapping("/emplois-du-temps")
    public List<EmploiDuTemps> getEmploiDuTemps(@RequestParam(required = false) Long formationId) {
        if (formationId != null) {
            return emploiDuTempsRepository.findByFormationId(formationId);
        }
        return emploiDuTempsRepository.findAll();
    }

    @PostMapping("/emplois-du-temps")
    public ResponseEntity<?> createEmploiDuTemps(@RequestBody EmploiDuTemps emploiDuTemps) {
        if (emploiDuTemps.getCours() == null || emploiDuTemps.getCours().getId() == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Le cours associé est obligatoire."));
        }
        
        Cours cours = coursRepository.findById(emploiDuTemps.getCours().getId()).orElse(null);
        if (cours == null) {
            return ResponseEntity.badRequest().body(java.util.Map.of("message", "Cours non trouvé."));
        }
        
        if (cours.getEnseignant() != null) {
            Long teacherId = cours.getEnseignant().getId();
            String day = emploiDuTemps.getJourSemaine();
            
            List<EmploiDuTemps> teacherSlots = emploiDuTempsRepository.findByCoursEnseignantId(teacherId);
            boolean exists = teacherSlots.stream()
                    .anyMatch(slot -> slot.getJourSemaine().equalsIgnoreCase(day) && 
                                     slot.getCours() != null && 
                                     slot.getCours().getId().equals(cours.getId()));
            if (exists) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "Erreur : Cet enseignant a déjà planifié ce cours pour cette journée."));
            }
        }
        
        if (emploiDuTemps.getMatiere() == null && cours.getMatiere() != null) {
            emploiDuTemps.setMatiere(cours.getMatiere().getNom());
        }
        
        EmploiDuTemps saved = emploiDuTempsRepository.save(emploiDuTemps);
        notifyClassStudents(saved.getClasse() != null ? saved.getClasse() : cours.getClasse(), saved, true);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/emplois-du-temps/{id}")
    public ResponseEntity<?> updateEmploiDuTemps(@PathVariable Long id, @RequestBody EmploiDuTemps details) {
        return emploiDuTempsRepository.findById(id).map(existing -> {
            if (details.getCours() == null || details.getCours().getId() == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "Le cours associé est obligatoire."));
            }
            
            Cours cours = coursRepository.findById(details.getCours().getId()).orElse(null);
            if (cours == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("message", "Cours non trouvé."));
            }
            
            if (cours.getEnseignant() != null) {
                Long teacherId = cours.getEnseignant().getId();
                String day = details.getJourSemaine();
                
                List<EmploiDuTemps> teacherSlots = emploiDuTempsRepository.findByCoursEnseignantId(teacherId);
                boolean exists = teacherSlots.stream()
                        .anyMatch(slot -> !slot.getId().equals(id) && 
                                         slot.getJourSemaine().equalsIgnoreCase(day) && 
                                         slot.getCours() != null && 
                                         slot.getCours().getId().equals(cours.getId()));
                if (exists) {
                    return ResponseEntity.badRequest().body(java.util.Map.of("message", "Erreur : Cet enseignant a déjà planifié ce cours pour cette journée."));
                }
            }
            
            existing.setCours(cours);
            existing.setJourSemaine(details.getJourSemaine());
            existing.setHeureDebut(details.getHeureDebut());
            existing.setHeureFin(details.getHeureFin());
            existing.setSalle(details.getSalle());
            existing.setMatiere(cours.getMatiere() != null ? cours.getMatiere().getNom() : details.getMatiere());
            if (details.getClasse() != null) {
                existing.setClasse(details.getClasse());
            }
            if (details.getFormation() != null) {
                existing.setFormation(details.getFormation());
            }
            
            EmploiDuTemps saved = emploiDuTempsRepository.save(existing);
            notifyClassStudents(saved.getClasse(), saved, false);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/emplois-du-temps/{id}")
    public ResponseEntity<?> deleteEmploiDuTemps(@PathVariable Long id) {
        if (emploiDuTempsRepository.existsById(id)) {
            emploiDuTempsRepository.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    
    @GetMapping("/reunions")
    public List<Reunion> getReunions() {
        return reunionRepository.findAll();
    }

    @PostMapping("/reunions")
    public ResponseEntity<Reunion> createReunion(@RequestBody Reunion reunion) {
        Reunion saved = reunionRepository.save(reunion);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/reunions/{id}")
    public ResponseEntity<Reunion> updateReunion(@PathVariable Long id, @RequestBody Reunion reunionDetails) {
        return reunionRepository.findById(id).map(existing -> {
            existing.setTitre(reunionDetails.getTitre());
            existing.setType(reunionDetails.getType());
            existing.setDate(reunionDetails.getDate());
            existing.setSalle(reunionDetails.getSalle());
            existing.setStatut(reunionDetails.getStatut());
            Reunion saved = reunionRepository.save(existing);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/reunions/{id}")
    public ResponseEntity<?> deleteReunion(@PathVariable Long id) {
        reunionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
