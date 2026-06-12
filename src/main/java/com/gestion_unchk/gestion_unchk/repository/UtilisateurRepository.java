package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.Utilisateur;
import com.gestion_unchk.gestion_unchk.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByRole(Role role);
}
