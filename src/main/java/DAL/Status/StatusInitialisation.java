package DAL.Status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public final class StatusInitialisation {
    private StatusInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        // Il faut 30 caractères pour garder "Employe administratif" en entier.
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Status (id SERIAL PRIMARY KEY, nom VARCHAR(30))");
            statement.executeUpdate("ALTER TABLE Status ALTER COLUMN nom TYPE VARCHAR(30)");
        }
    }

    public static void inserer(Connection connexion) throws SQLException {
        String sql = "INSERT INTO Status (nom) SELECT ? "
            + "WHERE NOT EXISTS (SELECT 1 FROM Status WHERE nom = ?)";
        for (String nom : new String[] { "Etudiant", "Charge de cours", "Employe administratif" }) {
            try (PreparedStatement requete = connexion.prepareStatement(sql)) {
                requete.setString(1, nom);
                requete.setString(2, nom);
                requete.executeUpdate();
            }
        }
    }
}
