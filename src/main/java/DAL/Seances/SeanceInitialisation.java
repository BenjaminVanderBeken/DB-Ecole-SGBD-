package DAL.Seances;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import java.sql.Timestamp;
import java.time.LocalDate;

public final class SeanceInitialisation {
    private SeanceInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Seance (id SERIAL PRIMARY KEY, "
                + "id_cours INT REFERENCES Cours(id), id_local INT REFERENCES Local(id), date TIMESTAMP)");
        }
    }

    public static void inserer(Connection connexion) throws SQLException {
        inserer(connexion, "2025-09-08", "2066", "HelHa", "Systèmes d'exploitation");
        inserer(connexion, "2025-09-15", "2066", "HelHa", "Systèmes d'exploitation");
        inserer(connexion, "2025-09-22", "PS02", "SLuc", "Systèmes d'exploitation");
        inserer(connexion, "2025-09-09", "3023", "HelHa", "Base des réseaux");
        inserer(connexion, "2025-09-16", "PS02", "SLuc", "Base des réseaux");
        inserer(connexion, "2025-09-23", "PS02", "SLuc", "Base des réseaux");
    }

    private static void inserer(Connection connexion, String date, String numero, String lieu,
            String cours) throws SQLException {
        // Le tableau ne donne pas d'heure : la date est enregistrée à minuit.
        Timestamp instant = Timestamp.valueOf(LocalDate.parse(date).atStartOfDay());
        int idLocal;
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT id FROM Local WHERE numero = ? AND lieu = ?")) {
            recherche.setString(1, numero);
            recherche.setString(2, lieu);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (!resultat.next()) {
                    throw new SQLException("Reference introuvable.");
                }
                int id = resultat.getInt(1);
                if (resultat.next()) {
                    throw new SQLException("Reference ambigue.");
                }
                idLocal = id;
            }
        }
        int idCours;
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT id FROM Cours WHERE nom = ?")) {
            recherche.setString(1, cours);
            try (ResultSet resultat = recherche.executeQuery()) {
                if (!resultat.next()) {
                    throw new SQLException("Reference introuvable.");
                }
                int id = resultat.getInt(1);
                if (resultat.next()) {
                    throw new SQLException("Reference ambigue.");
                }
                idCours = id;
            }
        }
        try (PreparedStatement requete = connexion.prepareStatement("INSERT INTO Seance (date, id_local, id_cours) SELECT ?, ?, ? "
                + "WHERE NOT EXISTS (SELECT 1 FROM Seance WHERE date = ? AND id_local = ? AND id_cours = ?)")) {
            requete.setTimestamp(1, instant);
            requete.setInt(2, idLocal);
            requete.setInt(3, idCours);
            requete.setTimestamp(4, instant);
            requete.setInt(5, idLocal);
            requete.setInt(6, idCours);
            requete.executeUpdate();
        }
    }

    public static int rechercherId(Connection connexion, String date, String numero, String lieu,
            String cours) throws SQLException {
        try (PreparedStatement recherche = connexion.prepareStatement("SELECT s.id FROM Seance s JOIN Local l ON s.id_local = l.id "
                + "JOIN Cours c ON s.id_cours = c.id WHERE s.date = ? "
                + "AND l.numero = ? AND l.lieu = ? AND c.nom = ?")) {
            recherche.setTimestamp(1, Timestamp.valueOf(LocalDate.parse(date).atStartOfDay()));
            recherche.setString(2, numero);
            recherche.setString(3, lieu);
            recherche.setString(4, cours);
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
