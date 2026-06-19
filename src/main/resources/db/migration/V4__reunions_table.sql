-- ==============================================================
-- V4 : Table des réunions académiques
-- ==============================================================

CREATE TABLE reunions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    type VARCHAR(100) NOT NULL,
    date TIMESTAMP NOT NULL,
    salle VARCHAR(150),
    statut VARCHAR(50) DEFAULT 'Planifiée'
);

-- Insérer les réunions par défaut
INSERT INTO reunions (titre, type, date, salle, statut) VALUES
('Réunion de cadrage - Technologies Web', 'Préparation des cours', '2026-06-15 10:00:00', 'Virtuelle (Google Meet)', 'Planifiée'),
('Suivi tutorat collectif - Promo 8', 'Suivi tutorat', '2026-06-18 15:00:00', 'Virtuelle (Zoom)', 'Planifiée'),
('Préparation des examens Semestre 2', 'Préparation des évaluations', '2026-06-22 09:00:00', 'Virtuelle (Teams)', 'Planifiée');
