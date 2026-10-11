package DAL.Personnes;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public final class PersonneInitialisation {
    private PersonneInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Personne (id SERIAL PRIMARY KEY, "
                + "id_status INT REFERENCES Status(id), nom VARCHAR(15), prenom VARCHAR(15))");
        }
    }
//Ajoute les personnes dans la table 
    public static void inserer(Connection connexion) throws SQLException {
        inserer(connexion, "Poulet", "Gilles", "Charge de cours");
        inserer(connexion, "Godissart", "Emmanuel", "Charge de cours");
        inserer(connexion, "Lai", "Valeria", "Employe administratif");
        inserer(connexion, "Mairesse", "David", "Employe administratif");
        inserer(connexion, "Durant", "Richard", "Etudiant");
        inserer(connexion, "Ortiz", "Valerie", "Etudiant");
    }
//Méthode Ajoute une personne 
    private static void inserer(Connection connexion, String nom, String prenom, String status)
            throws SQLException {
        // On cherche l'identifiant du statut plutôt que de supposer sa valeur.
        int idStatus;
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT id FROM Status WHERE nom = ?")) {
            recherche.setString(1, status);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (!resultat.next()) {
                    throw new SQLException("Reference introuvable.");
                }
                int id = resultat.getInt(1);
                if (resultat.next()) {
                    throw new SQLException("Reference ambigue.");
                }
                idStatus = id;
            }
        }
        try (PreparedStatement requete = connexion.prepareStatement("INSERT INTO Personne (nom, prenom, id_status) SELECT ?, ?, ? "
                + "WHERE NOT EXISTS (SELECT 1 FROM Personne WHERE nom = ? AND prenom = ? AND id_status = ?)")) {
            requete.setString(1, nom);
            requete.setString(2, prenom);
            requete.setInt(3, idStatus);
            requete.setString(4, nom);
            requete.setString(5, prenom);
            requete.setInt(6, idStatus);
            requete.executeUpdate();
        }
    }

    public static int rechercherId(Connection connexion, String nom, String prenom) throws SQLException {
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT id FROM Personne WHERE nom = ? AND prenom = ?")) {
            recherche.setString(1, nom);
            recherche.setString(2, prenom);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (!resultat.next()) {
                    throw new SQLException("Reference introuvable.");
                }
                int id = resultat.getInt(1);
                if (resultat.next()) {
                    throw new SQLException("Reference ambigue.");
                }
                return id;
            }
        }
    }
}
