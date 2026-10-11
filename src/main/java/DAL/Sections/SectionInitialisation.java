package DAL.Sections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public final class SectionInitialisation {
    private SectionInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Section (id SERIAL PRIMARY KEY, nom VARCHAR(30))");
            statement.executeUpdate("ALTER TABLE Section ALTER COLUMN nom TYPE VARCHAR(30)");
        }
    }

    public static void inserer(Connection connexion) throws SQLException {
        // Cette vérification évite les doublons quand on relance le programme.
        String sql = "INSERT INTO Section (nom) SELECT ? "
            + "WHERE NOT EXISTS (SELECT 1 FROM Section WHERE nom = ?)";
        for (String nom : new String[] { "Informatique de gestion", "Droit" }) {
            try (PreparedStatement requete = connexion.prepareStatement(sql)) {
                requete.setString(1, nom);
                requete.setString(2, nom);
                requete.executeUpdate();
            }
        }
    }
}
