package com.gestion_unchk.gestion_unchk.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "partenaires")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Partenaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(name = "type_partenariat", length = 100)
    private String typePartenariat;

    @Column(name = "contact_email", length = 150)
    private String contactEmail;

    @Column(columnDefinition = "TEXT")
    private String description;
}
