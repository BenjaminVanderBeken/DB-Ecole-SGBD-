package DAL.CoursPersonnes;

import DAL.Personnes.PersonneInitialisation;
import DAL.Cours.CoursInitialisation;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;

public final class CoursPersonneInitialisation {
    private CoursPersonneInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Cours_Personne ("
                + "id_personne INT REFERENCES Personne(id), id_cours INT REFERENCES Cours(id), "
                + "annee_academique DATE, PRIMARY KEY (id_personne, id_cours), "
                + "CHECK (EXTRACT(MONTH FROM annee_academique) = 9 "
                + "AND EXTRACT(DAY FROM annee_academique) = 1))");
        }
    }

    public static void inserer(Connection connexion, int anneeAcademique) throws SQLException {
        // L'année académique commence toujours le 1er septembre.
        Date annee = Date.valueOf(LocalDate.of(anneeAcademique, 9, 1));
        inserer(connexion, "Poulet", "Gilles", "Systèmes d'exploitation", annee);
        inserer(connexion, "Godissart", "Emmanuel", "Base des réseaux", annee);
        inserer(connexion, "Durant", "Richard", "Systèmes d'exploitation", annee);
        inserer(connexion, "Durant", "Richard", "Base des réseaux", annee);
        inserer(connexion, "Ortiz", "Valerie", "Base des réseaux", annee);
    }

    private static void inserer(Connection connexion, String nom, String prenom, String cours,
            Date annee) throws SQLException {
        coursPersonne(connexion, PersonneInitialisation.rechercherId(connexion, nom, prenom),
            CoursInitialisation.rechercherId(connexion, cours), annee);
    }

    private static void coursPersonne(Connection connexion, int personne, int cours, Date annee)
            throws SQLException {
        // La clé du MPD autorise une seule année par couple personne/cours.
        try (PreparedStatement recherche = connexion.prepareStatement(
                "SELECT annee_academique FROM Cours_Personne WHERE id_personne = ? AND id_cours = ?")) {
            recherche.setInt(1, personne);
            recherche.setInt(2, cours);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (resultat.next()) {
                    if (!annee.equals(resultat.getDate(1))) {
                        throw new SQLException("Une autre annee existe pour ce couple personne/cours.");
                    }
                    return;
                }
            }
        }
        try (PreparedStatement requete = connexion.prepareStatement(
                "INSERT INTO Cours_Personne (id_personne, id_cours, annee_academique) VALUES (?, ?, ?)")) {
            requete.setInt(1, personne);
            requete.setInt(2, cours);
            requete.setDate(3, annee);
            requete.executeUpdate();
        }
    }

}
