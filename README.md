# Gestion d'une école — DAL et BL

## Description

Ce projet réalise l'exercice de conversion d'une application Java de gestion d'école en deux couches :

- **DAL (Data Access Layer)** : accès à PostgreSQL avec les interfaces et les DAO de Section, Status et Cours. Les requêtes sont préparées dans le constructeur, conservées comme attributs et réutilisées pour lire, ajouter, modifier et supprimer les données. La méthode `close()` ferme les requêtes puis la connexion.
- **BL (Business Layer)** : classes Section, Status et Cours, et affichage du contenu des trois tables avec SampleDAO.

La partie « Pour aller plus loin » crée les tables du MPD et insère les données des tableaux 2.1 à 2.7 : 
Section, 
Cours, 
Status, 
Personne, 
Cours_Personne, 
Local et
 Seance. 
 Seance_Personne enregistre les participations aux séances.

Le projet utilise **Java**, **JDBC** et **PostgreSQL**. JDBC permet d'exécuter les requêtes SQL depuis Java. La classe **ConnectionFactory** centralise la création des connexions PostgreSQL selon le patron Factory.

## Prérequis

- Un JDK Java 25 pour compiler et exécuter le projet.
- PostgreSQL démarré et accessible sur le port 5432.
- VS Code avec les extensions Java pour utiliser le bouton Run.
- Le pilote JDBC PostgreSQL `postgresql-42.7.13.jar`, présent dans le dossier `lib`.

Le projet se lance avec la configuration Java de VS Code, sans Maven.

## Installation

1. Ouvrir le dossier du projet dans VS Code.
2. Vérifier que PostgreSQL est démarré.
3. Adapter l'adresse du serveur, l'utilisateur et le mot de passe dans `src/main/java/Ecole.java` et `src/main/java/BL/SampleDAO.java`.
4. Ouvrir `Ecole.java` et cliquer sur **Run** au-dessus de la méthode `main`.
5. Ouvrir `BL/SampleDAO.java` et cliquer sur **Run** pour afficher Section, Status et Cours.

La configuration actuelle utilise le serveur local, l'utilisateur `postgres` et le mot de passe `root`. L'utilisateur PostgreSQL doit pouvoir créer la base `ecole` si elle n'existe pas.

## Exemples

Après l'exécution d'Ecole, le programme affiche :

```text
Tables et donnees disponibles.
Lance SampleDAO pour afficher Section, Status et Cours.
```

SampleDAO affiche les identifiants et les noms des sections, des statuts et des cours. Pour chaque cours, il affiche aussi l'identifiant de sa section. Les identifiants sont générés par PostgreSQL et peuvent varier.

Les insertions vérifient les données déjà présentes pour permettre de relancer l'initialisation sans les ajouter à nouveau. Une référence manquante ou ambiguë provoque une erreur.

L'année académique utilisée est **2025–2026**, enregistrée comme `2025-09-01`. Les séances utilisent les dates du tableau 2.7, avec l'heure `00:00:00`, car aucune heure n'est fournie. La séance du 09/09/2025 au local 3023 pour Base des réseaux est partagée par Durant Richard et Ortiz Valerie.

Les colonnes `Section.nom` et `Status.nom` acceptent 30 caractères pour conserver les libellés complets, notamment « Informatique de gestion » et « Employe administratif ».

## Organisation du code

```text
src/main/java/
├── BL/                  Classes métier et SampleDAO
├── DAL/
│   ├── ConnectionFactory.java
│   ├── Sections/        Interface, DAO et initialisation
│   ├── Status/          Interface, DAO et initialisation
│   ├── Cours/           Interface, DAO et initialisation
│   ├── Personnes/       Initialisation
│   ├── CoursPersonnes/  Initialisation
│   ├── Locaux/          Initialisation
│   ├── Seances/         Initialisation
│   └── SeancePersonnes/ Initialisation
└── Ecole.java           Création de la base, des tables et insertion
```

`Ecole.java` enchaîne les étapes. Chaque fichier d'initialisation contient directement ses requêtes JDBC et renseigne les paramètres avec `setString()`, `setInt()`, `setDate()` ou `setTimestamp()`.

## Contributions

Pour modifier cet exercice, conserver la séparation entre DAL et BL, utiliser des requêtes préparées pour les valeurs SQL et fermer les ressources JDBC avec `try-with-resources`. Vérifier la compilation avec Java 25 et le fonctionnement des opérations concernées sur PostgreSQL.

## Licence

Aucune licence n'est définie pour ce projet.



