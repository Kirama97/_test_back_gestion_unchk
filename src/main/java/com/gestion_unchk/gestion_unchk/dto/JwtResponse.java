package com.gestion_unchk.gestion_unchk.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class JwtResponse {
    private String token;
    private String email;
    private String role;
    private String nom;
    private String prenom;
    private Long id;
}
