package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "reunions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titre;

    @Column(nullable = false, length = 100)
    private String type; 

    @Column(nullable = false)
    private LocalDateTime date;

    @Column(length = 150)
    private String salle;

    @Column(length = 50)
    private String statut = "Planifiée";
}
