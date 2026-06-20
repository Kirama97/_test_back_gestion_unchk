package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "suivi_etudiants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuiviEtudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id", nullable = false, unique = true)
    private Etudiant etudiant;

    @Column(name = "registre_contact", length = 255)
    private String registreContact;

    @Column(name = "bilan_stages", columnDefinition = "TEXT")
    private String bilanStages;

    @Column(name = "statut_insertion", nullable = false, length = 30)
    private String statutInsertion; 

    @Column(name = "salaire_initial", precision = 12, scale = 2)
    private BigDecimal salaireInitial;

    @Column(length = 150)
    private String entreprise;
}
