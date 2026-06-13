-- ==============================================================
-- V1 : Initialisation du schema de la base gestion_unchk
-- ==============================================================

-- Table des utilisateurs
CREATE TABLE utilisateurs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    departement VARCHAR(100),
    statut VARCHAR(50) DEFAULT 'Actif'
);

-- Table des étudiants (extension de la table utilisateurs)
CREATE TABLE etudiants (
    utilisateur_id BIGINT PRIMARY KEY,
    ine VARCHAR(50) NOT NULL UNIQUE,
    date_naissance DATE NOT NULL,
    filiere VARCHAR(100) NOT NULL,
    promo VARCHAR(50) NOT NULL,
    annee_debut INT NOT NULL,
    annee_sortie INT,
    diplomes TEXT,
    autres_formations TEXT,
    FOREIGN KEY (utilisateur_id) REFERENCES utilisateurs(id) ON DELETE CASCADE
);

-- Table des comptes rendus
CREATE TABLE comptes_rendus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    chemin_document VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    auteur_id BIGINT NOT NULL,
    FOREIGN KEY (auteur_id) REFERENCES utilisateurs(id)
);

-- Table des documents
CREATE TABLE documents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(200) NOT NULL,
    description TEXT,
    type VARCHAR(50) NOT NULL,
    chemin_fichier VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    auteur_id BIGINT NOT NULL,
    FOREIGN KEY (auteur_id) REFERENCES utilisateurs(id)
);

-- Table des budgets
CREATE TABLE budgets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    annee INT NOT NULL,
    type VARCHAR(50) NOT NULL,
    montant DECIMAL(15, 2) NOT NULL,
    description TEXT,
    chemin_fichier VARCHAR(255),
    date_creation TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Table des formations (matières / cours)
CREATE TABLE formations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    date_debut DATE NOT NULL,
    date_fin DATE NOT NULL,
    type_formation VARCHAR(50) NOT NULL,
    niveau VARCHAR(50) NOT NULL,
    montant_financement DECIMAL(12, 2),
    type_financement VARCHAR(100),
    formateur_id BIGINT,
    FOREIGN KEY (formateur_id) REFERENCES utilisateurs(id)
);

-- Table des emplois du temps
CREATE TABLE emplois_du_temps (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    formation_id BIGINT NOT NULL,
    jour_semaine VARCHAR(15) NOT NULL,
    heure_debut TIME NOT NULL,
    heure_fin TIME NOT NULL,
    salle VARCHAR(50) NOT NULL,
    matiere VARCHAR(150) NOT NULL,
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE
);

-- Table des notes
CREATE TABLE notes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id BIGINT NOT NULL,
    formation_id BIGINT NOT NULL,
    valeur DECIMAL(4, 2) NOT NULL,
    type VARCHAR(20) NOT NULL,
    date_saisie TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (etudiant_id) REFERENCES etudiants(utilisateur_id) ON DELETE CASCADE,
    FOREIGN KEY (formation_id) REFERENCES formations(id) ON DELETE CASCADE
);

-- Table de suivi des étudiants (insertion)
CREATE TABLE suivi_etudiants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    etudiant_id BIGINT NOT NULL UNIQUE,
    registre_contact VARCHAR(255),
    bilan_stages TEXT,
    statut_insertion VARCHAR(30) NOT NULL,
    salaire_initial DECIMAL(12, 2),
    entreprise VARCHAR(150),
    FOREIGN KEY (etudiant_id) REFERENCES etudiants(utilisateur_id) ON DELETE CASCADE
);

-- Table des partenaires
CREATE TABLE partenaires (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(150) NOT NULL,
    type_partenariat VARCHAR(100),
    contact_email VARCHAR(150),
    description TEXT
);

-- Insérer les utilisateurs par défaut (le mot de passe crypté est "Passer123")
-- BCrypt : $2a$10$b7UutYeGDbUw7EhXPg03P.tRAuYMd6U7jTp.x.delijGpn7X2jm9e
INSERT INTO utilisateurs (nom, prenom, email, mot_de_passe, role, departement, statut) VALUES
('Sarr', 'Abdoulaye', 'admin@gmail.com', '$2a$10$b7UutYeGDbUw7EhXPg03P.tRAuYMd6U7jTp.x.delijGpn7X2jm9e', 'ADMIN', 'Scolarité', 'Actif'),
('Thiam', 'Diene', 'etudiant@gmail.com', '$2a$10$b7UutYeGDbUw7EhXPg03P.tRAuYMd6U7jTp.x.delijGpn7X2jm9e', 'ETUDIANT', 'Informatique', 'Actif'),
('Ndiaye', 'Moussa', 'enseignant@gmail.com', '$2a$10$b7UutYeGDbUw7EhXPg03P.tRAuYMd6U7jTp.x.delijGpn7X2jm9e', 'ENSEIGNANT', 'Informatique', 'Actif'),
('Diop', 'Fatou', 'tuteur@gmail.com', '$2a$10$b7UutYeGDbUw7EhXPg03P.tRAuYMd6U7jTp.x.delijGpn7X2jm9e', 'TUTEUR', 'Mathématiques', 'Actif'),
('Diallo', 'Ibrahima', 'insertion@gmail.com', '$2a$10$b7UutYeGDbUw7EhXPg03P.tRAuYMd6U7jTp.x.delijGpn7X2jm9e', 'INSERTION', 'Relations Professionnelles', 'Actif');

-- Insérer les détails de l'étudiant de test
INSERT INTO etudiants (utilisateur_id, ine, date_naissance, filiere, promo, annee_debut, annee_sortie, diplomes, autres_formations) VALUES
(2, 'INE-2026-9876', '2002-04-12', 'Génie Logiciel', 'Promo 8', 2024, 2026, 'Licence Math-Info', 'Certification Oracle Java');

-- Insérer quelques formations pour le test
INSERT INTO formations (nom, date_debut, date_fin, type_formation, niveau, montant_financement, type_financement, formateur_id) VALUES
('Technologies Web', '2026-02-01', '2026-06-30', 'INITIALE', 'Master 1', 0.00, 'Public', 3),
('Bases de Données', '2026-02-01', '2026-06-30', 'INITIALE', 'Master 1', 0.00, 'Public', 4);

-- Insérer des emplois du temps
INSERT INTO emplois_du_temps (formation_id, jour_semaine, heure_debut, heure_fin, salle, matiere) VALUES
(1, 'LUNDI', '14:00:00', '16:00:00', 'Amphi A', 'Technologies Web'),
(2, 'MERCREDI', '09:00:00', '12:00:00', 'Salle 102', 'Bases de Données');

-- Insérer des notes pour l'étudiant
INSERT INTO notes (etudiant_id, formation_id, valeur, type) VALUES
(2, 1, 16.50, 'DEVOIR'),
(2, 2, 14.00, 'DEVOIR');

-- Insérer des informations de suivi d'insertion pour l'étudiant
INSERT INTO suivi_etudiants (etudiant_id, registre_contact, bilan_stages, statut_insertion, salaire_initial, entreprise) VALUES
(2, 'diene.thiam@unchk.edu.sn | +221 77 123 45 67', 'Stage de fin d\'études effectué chez Orange Sénégal - Excellent retour.', 'SALARIE', 450000.00, 'Orange Sénégal');

-- Insérer des partenaires par défaut
INSERT INTO partenaires (nom, type_partenariat, contact_email, description) VALUES
('Orange Sénégal', 'Stages et Insertion', 'contact@orange.sn', 'Partenaire historique pour l\'accueil des stagiaires en Génie Logiciel.'),
('Senelec', 'Recherche et Développement', 'contact@senelec.sn', 'Partenariat technique sur les systèmes d\'information géographiques.');
