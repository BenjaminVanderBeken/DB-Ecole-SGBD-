import DAL.ConnectionFactory;
import java.sql.Connection;
import java.sql.SQLException;

import DAL.Sections.SectionInitialisation;
import DAL.Status.StatusInitialisation;
import DAL.Cours.CoursInitialisation;
import DAL.Personnes.PersonneInitialisation;
import DAL.Locaux.LocalInitialisation;
import DAL.CoursPersonnes.CoursPersonneInitialisation;
import DAL.Seances.SeanceInitialisation;
import DAL.SeancePersonnes.SeancePersonneInitialisation;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Ecole {
    public static void main(String[] args) {
        String serveur = "jdbc:postgresql://127.0.0.1:5432/";
        String user = "postgres";
        String password = "root";

        ConnectionFactory serveurFactory = new ConnectionFactory(serveur + "postgres", user, password);
        ConnectionFactory ecoleFactory = new ConnectionFactory(serveur + "ecole", user, password);

        try {
            creer(serveurFactory);
            initialiser(ecoleFactory, 2025);
            System.out.println("Tables et donnees disponibles.");
            System.out.println("Lance SampleDAO pour afficher Section, Status et Cours.");
        } catch (SQLException e) {
            System.err.println("Erreur PostgreSQL : " + e.getMessage());
        }
    }

    public static void creer(ConnectionFactory serveurFactory) throws SQLException {
        try (Connection connexion = serveurFactory.createConnection();
             PreparedStatement requete = connexion.prepareStatement(
                 "SELECT 1 FROM pg_database WHERE datname = ?")) {
            requete.setString(1, "ecole");
            boolean existe;
            try (ResultSet resultat = requete.executeQuery()) {
                existe = resultat.next();
            }
            if (!existe) {
                try (Statement statement = connexion.createStatement()) {
                    statement.executeUpdate("CREATE DATABASE ecole");
                }
            }
        }
    }

    public static void initialiser(ConnectionFactory factory, int anneeAcademique) throws SQLException {
        try (Connection connexion = factory.createConnection()) {
            connexion.setAutoCommit(false);
            // On valide l'ensemble à la fin, ou on annule si une étape échoue.
            try {
                SectionInitialisation.creer(connexion);
                StatusInitialisation.creer(connexion);
                CoursInitialisation.creer(connexion);
                PersonneInitialisation.creer(connexion);
                LocalInitialisation.creer(connexion);
                CoursPersonneInitialisation.creer(connexion);
                SeanceInitialisation.creer(connexion);
                SeancePersonneInitialisation.creer(connexion);

                SectionInitialisation.inserer(connexion);
                StatusInitialisation.inserer(connexion);
                CoursInitialisation.inserer(connexion);
                PersonneInitialisation.inserer(connexion);
                LocalInitialisation.inserer(connexion);
                CoursPersonneInitialisation.inserer(connexion, anneeAcademique);
                SeanceInitialisation.inserer(connexion);
                SeancePersonneInitialisation.inserer(connexion);
                connexion.commit();
            } catch (SQLException | RuntimeException e) {
                try {
                    connexion.rollback();
                } catch (SQLException rollbackErreur) {
                    e.addSuppressed(rollbackErreur);
                }
                throw e;
            }
        }
    }
}
