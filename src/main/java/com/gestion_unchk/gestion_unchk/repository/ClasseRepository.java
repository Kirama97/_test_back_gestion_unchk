package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.Classe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ClasseRepository extends JpaRepository<Classe, Long> {
    List<Classe> findByPromotionId(Long promotionId);
    List<Classe> findByFiliereId(Long filiereId);
    List<Classe> findByPromotionIdAndFiliereId(Long promotionId, Long filiereId);
}
