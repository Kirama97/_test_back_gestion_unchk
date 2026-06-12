package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "formations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(name = "date_debut", nullable = false)
    private LocalDate dateDebut;

    @Column(name = "date_fin", nullable = false)
    private LocalDate dateFin;

    @Column(name = "type_formation", nullable = false, length = 50)
    private String typeFormation; // INITIALE, CONTINUE, CERTIFICATION, PRIVE

    @Column(nullable = false, length = 50)
    private String niveau;

    @Column(name = "montant_financement", precision = 12, scale = 2)
    private BigDecimal montantFinancement;

    @Column(name = "type_financement", length = 100)
    private String typeFinancement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formateur_id")
    private Utilisateur formateur; // Enseignant ou Tuteur responsable
}
