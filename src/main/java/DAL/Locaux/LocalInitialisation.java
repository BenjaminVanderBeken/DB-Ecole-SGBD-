package DAL.Locaux;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public final class LocalInitialisation {
    private LocalInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Local (id SERIAL PRIMARY KEY, numero VARCHAR(5), "
                + "lieu VARCHAR(10), type VARCHAR(3) CHECK (type IN ('INF', 'STD')))");
        }
    }

    public static void inserer(Connection connexion) throws SQLException {
        inserer(connexion, "3023", "HelHa", "STD");
        inserer(connexion, "PS02", "SLuc", "INF");
        inserer(connexion, "2066", "HelHa", "INF");
        inserer(connexion, "3220", "HelHa", "STD");
    }

    private static void inserer(Connection connexion, String numero, String lieu, String type)
            throws SQLException {
        try (PreparedStatement requete = connexion.prepareStatement("INSERT INTO Local (numero, lieu, type) SELECT ?, ?, ? "
                + "WHERE NOT EXISTS (SELECT 1 FROM Local WHERE numero = ? AND lieu = ?)")) {
            requete.setString(1, numero);
            requete.setString(2, lieu);
            requete.setString(3, type);
            requete.setString(4, numero);
            requete.setString(5, lieu);
            requete.executeUpdate();
        }
    }
}
