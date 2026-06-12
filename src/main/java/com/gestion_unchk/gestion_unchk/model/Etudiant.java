package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "etudiants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Etudiant {

    @Id
    @Column(name = "utilisateur_id")
    private Long id;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;

    @Column(nullable = false, unique = true, length = 50)
    private String ine;

    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(nullable = false, length = 100)
    private String filiere;

    @Column(nullable = false, length = 50)
    private String promo;

    @Column(name = "annee_debut", nullable = false)
    private Integer anneeDebut;

    @Column(name = "annee_sortie")
    private Integer anneeSortie;

    @Column(columnDefinition = "TEXT")
    private String diplomes;

    @Column(name = "autres_formations", columnDefinition = "TEXT")
    private String autresFormations;
}
