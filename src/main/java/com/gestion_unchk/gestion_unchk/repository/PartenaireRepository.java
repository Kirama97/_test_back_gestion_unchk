package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.Partenaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartenaireRepository extends JpaRepository<Partenaire, Long> {
}
