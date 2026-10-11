package DAL.Cours;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public final class CoursInitialisation {
    private CoursInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Cours (id SERIAL PRIMARY KEY, "
                + "nom VARCHAR(30), id_section INT REFERENCES Section(id))");
        }
        // Compatibilite avec l'ancienne colonne section_id.
        try (PreparedStatement requete = connexion.prepareStatement(
                "SELECT column_name FROM information_schema.columns "
                + "WHERE table_schema = current_schema() AND table_name = ?")) {
            requete.setString(1, "cours");
            boolean ancienne = false;
            boolean nouvelle = false;
            try (ResultSet resultat = requete.executeQuery()) {
                while (resultat.next()) {
                    ancienne |= "section_id".equals(resultat.getString(1));
                    nouvelle |= "id_section".equals(resultat.getString(1));
                }
            }
            if (ancienne && !nouvelle) {
                try (Statement statement = connexion.createStatement()) {
                    statement.executeUpdate("ALTER TABLE Cours RENAME COLUMN section_id TO id_section");
                }
            }
        }
    }

    public static void inserer(Connection connexion) throws SQLException {
        inserer(connexion, "Base des réseaux", "Informatique de gestion");
        inserer(connexion, "Systèmes d'exploitation", "Informatique de gestion");
        inserer(connexion, "Programmation orientée objet", "Informatique de gestion");
        inserer(connexion, "Droit civil", "Droit");
        inserer(connexion, "Droit commercial", "Droit");
    }

    private static void inserer(Connection connexion, String nom, String section) throws SQLException {
        int idSection;
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT id FROM Section WHERE nom = ?")) {
            recherche.setString(1, section);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (!resultat.next()) {
                    throw new SQLException("Reference introuvable.");
                }
                int id = resultat.getInt(1);
                if (resultat.next()) {
                    throw new SQLException("Reference ambigue.");
                }
                idSection = id;
            }
        }
        try (PreparedStatement requete = connexion.prepareStatement("INSERT INTO Cours (nom, id_section) SELECT ?, ? "
                + "WHERE NOT EXISTS (SELECT 1 FROM Cours WHERE nom = ? AND id_section = ?)")) {
            requete.setString(1, nom);
            requete.setInt(2, idSection);
            requete.setString(3, nom);
            requete.setInt(4, idSection);
            requete.executeUpdate();
        }
    }

    public static int rechercherId(Connection connexion, String nom) throws SQLException {
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT id FROM Cours WHERE nom = ?")) {
            recherche.setString(1, nom);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (!resultat.next()) {
                    throw new SQLException("Cours introuvable : " + nom);
                }
                int id = resultat.getInt("id");
                if (resultat.next()) {
                    throw new SQLException("Plusieurs cours portent ce nom : " + nom);
                }
                return id;
            }
        }
    }
}
