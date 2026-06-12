package com.gestion_unchk.gestion_unchk.repository;

import com.gestion_unchk.gestion_unchk.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByType(String type);
}
