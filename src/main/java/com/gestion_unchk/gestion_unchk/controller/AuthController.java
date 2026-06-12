package com.gestion_unchk.gestion_unchk.controller;

import com.gestion_unchk.gestion_unchk.config.JwtUtils;
import com.gestion_unchk.gestion_unchk.dto.JwtResponse;
import com.gestion_unchk.gestion_unchk.dto.LoginRequest;
import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(loginRequest.getEmail());

        Utilisateur utilisateur = utilisateurRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                utilisateur.getEmail(),
                utilisateur.getRole().name().toLowerCase(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getId()
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body("Non authentifié");
        }
        Utilisateur utilisateur = utilisateurRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));
        
        return ResponseEntity.ok(utilisateur);
    }
}
