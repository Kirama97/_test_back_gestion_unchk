package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.SuiviEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SuiviEtudiantRepository extends JpaRepository<SuiviEtudiant, Long> {
    Optional<SuiviEtudiant> findByEtudiantId(Long etudiantId);
}
