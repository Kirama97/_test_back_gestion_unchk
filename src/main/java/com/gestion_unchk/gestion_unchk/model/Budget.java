package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer annee;

    @Column(nullable = false, length = 50)
    private String type; 

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "chemin_fichier", length = 255)
    private String cheminFichier;

    @Column(name = "date_creation", insertable = false, updatable = false)
    private LocalDateTime dateCreation;
}
