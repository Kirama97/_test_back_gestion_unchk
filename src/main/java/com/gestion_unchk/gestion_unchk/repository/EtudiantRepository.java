package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByIne(String ine);
    List<Etudiant> findByFiliere(String filiere);
}
