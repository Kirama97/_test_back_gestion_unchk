package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.EmploiDuTemps;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EmploiDuTempsRepository extends JpaRepository<EmploiDuTemps, Long> {
    List<EmploiDuTemps> findByFormationId(Long formationId);
    List<EmploiDuTemps> findByClasseId(Long classeId);
    List<EmploiDuTemps> findByCoursEnseignantId(Long enseignantId);
}
