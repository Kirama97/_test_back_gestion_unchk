-- ==============================================================
-- V3 : Table des notifications pour le portail
-- ==============================================================

CREATE TABLE notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(150) NOT NULL,
    description TEXT,
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    lu BOOLEAN DEFAULT FALSE,
    category VARCHAR(50),
    destinataire_id BIGINT,
    FOREIGN KEY (destinataire_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Insérer quelques notifications par défaut
INSERT INTO notifications (titre, description, category, destinataire_id) VALUES
('Nouvel emploi du temps', 'L''emploi du temps de la classe IDA Master 1 a été mis à jour.', 'schedule', 2),
('Note publiée', 'Votre note pour le module "Technologies Web" est disponible.', 'exam', 2),
('Circulaire centrale', 'La note de service concernant la semaine culturelle est disponible.', 'communication', 2);
