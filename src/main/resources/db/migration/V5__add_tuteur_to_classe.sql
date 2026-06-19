-- Migration V5 : Affectation de tuteurs aux classes entières

ALTER TABLE classes ADD COLUMN tuteur_id BIGINT NULL;
ALTER TABLE classes ADD CONSTRAINT fk_classes_tuteur FOREIGN KEY (tuteur_id) REFERENCES utilisateurs(id) ON DELETE SET NULL;
