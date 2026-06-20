package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalTime;

@Entity
@Table(name = "emplois_du_temps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmploiDuTemps {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "classe_id")
    private Classe classe;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "cours_id")
    private Cours cours;

    @Column(name = "jour_semaine", nullable = false, length = 15)
    private String jourSemaine; 

    @Column(name = "heure_debut", nullable = false)
    private LocalTime heureDebut;

    @Column(name = "heure_fin", nullable = false)
    private LocalTime heureFin;

    @Column(nullable = false, length = 50)
    private String salle;

    @Column(nullable = false, length = 150)
    private String matiere;
}
