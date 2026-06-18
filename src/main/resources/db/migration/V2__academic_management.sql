-- ==============================================================
-- V2 : Schema de gestion academique universitaire (UN-CHK)
-- ==============================================================

-- 1. Table des promotions
CREATE TABLE promotions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Table des filieres
CREATE TABLE filieres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL UNIQUE,
    code VARCHAR(20) NOT NULL UNIQUE
);

-- 3. Table des classes (Liaison Filiere + Promotion + Niveau)
CREATE TABLE classes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    promotion_id BIGINT NOT NULL,
    filiere_id BIGINT NOT NULL,
    niveau_etude VARCHAR(50) NOT NULL,
    FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE,
    FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE CASCADE
);

-- 4. Table des matières
CREATE TABLE matieres (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    description TEXT
);

-- 5. Table des cours (Matiere + Enseignant + Classe)
CREATE TABLE cours (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    matiere_id BIGINT NOT NULL,
    enseignant_id BIGINT,
    classe_id BIGINT NOT NULL,
    FOREIGN KEY (matiere_id) REFERENCES matieres(id) ON DELETE CASCADE,
    FOREIGN KEY (enseignant_id) REFERENCES utilisateurs(id) ON DELETE SET NULL,
    FOREIGN KEY (classe_id) REFERENCES classes(id) ON DELETE CASCADE
);

-- 6. Table des sequences
CREATE TABLE sequences (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cours_id BIGINT NOT NULL,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    date_debut DATE,
    date_fin DATE,
    document_chemin VARCHAR(255),
    exercice_chemin VARCHAR(255),
    FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE
);

-- 7. Table des annonces
CREATE TABLE annonces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    contenu TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    date_publication TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    auteur_id BIGINT NOT NULL,
    FOREIGN KEY (auteur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- 8. Modifier la table utilisateurs pour ajouter le telephone
ALTER TABLE utilisateurs ADD COLUMN telephone VARCHAR(30);

-- 9. Modifier la table etudiants pour ajouter classe_id, promotion_id, filiere_id
ALTER TABLE etudiants ADD COLUMN classe_id BIGINT;
ALTER TABLE etudiants ADD COLUMN promotion_id BIGINT;
ALTER TABLE etudiants ADD COLUMN filiere_id BIGINT;
ALTER TABLE etudiants ADD COLUMN niveau_etude VARCHAR(50) DEFAULT 'Master 1';
ALTER TABLE etudiants ADD COLUMN adresse VARCHAR(255) DEFAULT 'Dakar, Sénégal';
ALTER TABLE etudiants ADD COLUMN genre VARCHAR(30) DEFAULT 'Masculin';
ALTER TABLE etudiants ADD FOREIGN KEY (classe_id) REFERENCES classes(id) ON DELETE SET NULL;
ALTER TABLE etudiants ADD FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE SET NULL;
ALTER TABLE etudiants ADD FOREIGN KEY (filiere_id) REFERENCES filieres(id) ON DELETE SET NULL;

-- 10. Modifier la table notes pour lier a matiere, classe, promotion, sequence et session
ALTER TABLE notes MODIFY COLUMN formation_id BIGINT NULL;
ALTER TABLE notes ADD COLUMN matiere_id BIGINT;
ALTER TABLE notes ADD COLUMN classe_id BIGINT;
ALTER TABLE notes ADD COLUMN promotion_id BIGINT;
ALTER TABLE notes ADD COLUMN sequence_id BIGINT;
ALTER TABLE notes ADD COLUMN session VARCHAR(50) DEFAULT 'Principale';
ALTER TABLE notes ADD FOREIGN KEY (matiere_id) REFERENCES matieres(id) ON DELETE CASCADE;
ALTER TABLE notes ADD FOREIGN KEY (classe_id) REFERENCES classes(id) ON DELETE CASCADE;
ALTER TABLE notes ADD FOREIGN KEY (promotion_id) REFERENCES promotions(id) ON DELETE CASCADE;
ALTER TABLE notes ADD FOREIGN KEY (sequence_id) REFERENCES sequences(id) ON DELETE SET NULL;

-- 11. Modifier la table emplois_du_temps
ALTER TABLE emplois_du_temps MODIFY COLUMN formation_id BIGINT NULL;
ALTER TABLE emplois_du_temps ADD COLUMN classe_id BIGINT;
ALTER TABLE emplois_du_temps ADD COLUMN cours_id BIGINT;
ALTER TABLE emplois_du_temps ADD FOREIGN KEY (classe_id) REFERENCES classes(id) ON DELETE CASCADE;
ALTER TABLE emplois_du_temps ADD FOREIGN KEY (cours_id) REFERENCES cours(id) ON DELETE CASCADE;

-- ==============================================================
-- INSERTION DES DONNEES DE SEED
-- ==============================================================

-- Remplir le telephone par defaut pour les utilisateurs existants
UPDATE utilisateurs SET telephone = '+221 77 123 45 67' WHERE id = 1;
UPDATE utilisateurs SET telephone = '+221 77 234 56 78' WHERE id = 2;
UPDATE utilisateurs SET telephone = '+221 77 345 67 89' WHERE id = 3;
UPDATE utilisateurs SET telephone = '+221 77 456 78 90' WHERE id = 4;
UPDATE utilisateurs SET telephone = '+221 77 567 89 01' WHERE id = 5;

-- Ajouter des promotions
INSERT INTO promotions (id, nom) VALUES
(1, 'Promotion 8'),
(2, 'Promotion 9'),
(3, 'Promotion 10');

-- Ajouter des filieres
INSERT INTO filieres (id, nom, code) VALUES
(1, 'Informatique et Développement d''Applications', 'IDA'),
(2, 'Systèmes, Télécommunications et Réseaux Numériques', 'STRN'),
(3, 'Multimédia, Internet et Communication', 'MIC'),
(4, 'Énergies Alternatives et Systèmes', 'EAS');

-- Ajouter des classes
INSERT INTO classes (id, nom, promotion_id, filiere_id, niveau_etude) VALUES
(1, 'IDA Master 1 - Promotion 8', 1, 1, 'Master 1'),
(2, 'MIC Licence 3 - Promotion 9', 2, 3, 'Licence 3'),
(3, 'STRN Licence 2 - Promotion 10', 3, 2, 'Licence 2');

-- Associer l'etudiant test (id = 2, Diene Thiam) a la classe 1
UPDATE etudiants SET 
classe_id = 1,
promotion_id = 1,
filiere_id = 1,
niveau_etude = 'Master 1',
adresse = 'Parcelles Assainies, Dakar',
genre = 'Masculin'
WHERE utilisateur_id = 2;

-- Ajouter des matieres
INSERT INTO matieres (id, nom, code, description) VALUES
(1, 'Technologies Web', 'TECHWEB', 'Apprentissage d''HTML, CSS, JS et frameworks web'),
(2, 'Bases de Données SQL', 'BDDSQL', 'Modélisation relationnelle et requêtes SQL complexes'),
(3, 'Développement React', 'REACTJS', 'Interfaces riches en composant et hooks avec React'),
(4, 'Node.js & Express', 'NODEJS', 'Développement back-end asynchrone et API REST'),
(5, 'Spring Boot avec Java', 'SPRINGBOOT', 'Développement microservices et API robustes en Java'),
(6, 'Git & GitHub', 'GITGITHUB', 'Gestion de version et collaboration en équipe');

-- Ajouter des cours
INSERT INTO cours (id, matiere_id, enseignant_id, classe_id) VALUES
(1, 1, 3, 1), -- Tech Web enseigne par Moussa Ndiaye a la classe IDA M1
(2, 2, 4, 1), -- BDD enseigne par Fatou Diop a la classe IDA M1
(3, 3, 3, 1), -- React par Moussa Ndiaye a IDA M1
(4, 5, 3, 1); -- Spring Boot par Moussa Ndiaye a IDA M1

-- Ajouter des sequences pour les cours
INSERT INTO sequences (id, cours_id, titre, description, date_debut, date_fin, document_chemin, exercice_chemin) VALUES
(1, 1, 'Introduction au Web et HTML5', 'Cette séquence aborde l''histoire du web et la structure HTML5.', '2026-02-01', '2026-02-15', '/docs/html5_intro.pdf', '/exercises/tp1_cv.pdf'),
(2, 1, 'Styliser avec CSS3 et Modèle de boîte', 'Introduction aux styles CSS, sélecteurs et box model.', '2026-02-16', '2026-02-28', '/docs/css3_boxmodel.pdf', '/exercises/tp2_portfolio.pdf'),
(3, 1, 'Mise en page moderne : Flexbox et Grid', 'Layouts complexes avec Flexbox et Grid.', '2026-03-01', '2026-03-15', '/docs/css_layout.pdf', '/exercises/tp3_site.pdf');

-- Lier les notes existantes aux matieres, classes, promotions et sequences
UPDATE notes SET matiere_id = 1, classe_id = 1, promotion_id = 1, sequence_id = 1 WHERE id = 1;
UPDATE notes SET matiere_id = 2, classe_id = 1, promotion_id = 1, sequence_id = NULL WHERE id = 2;

-- Lier les emplois du temps existants a la classe et au cours
UPDATE emplois_du_temps SET classe_id = 1, cours_id = 1 WHERE id = 1;
UPDATE emplois_du_temps SET classe_id = 1, cours_id = 2 WHERE id = 2;

-- Ajouter des annonces academiques
INSERT INTO annonces (id, titre, contenu, type, auteur_id) VALUES
(1, 'Nouvel emploi du temps du Semestre 2', 'L''emploi du temps général du Semestre 2 est disponible en ligne. Veuillez vérifier vos créneaux horaires.', 'CHANGEMENT_EDT', 1),
(2, 'Publication des notes du module Technologies Web', 'Les notes du devoir 1 pour le module "Technologies Web" ont été publiées par l''enseignant.', 'ACADEMIQUE', 1),
(3, 'Réunion d''information sur l''insertion professionnelle', 'L''administration convie tous les étudiants en Master 1 à une réunion d''orientation professionnelle ce vendredi à 10h.', 'REUNION', 1);
