package com.gestion_unchk.gestion_unchk.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EtudiantDto {
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private String ine;
    private LocalDate dateNaissance;
    private String filiere;
    private String promo;
    private Integer anneeDebut;
    private Integer anneeSortie;
    private String diplomes;
    private String autresFormations;
}
