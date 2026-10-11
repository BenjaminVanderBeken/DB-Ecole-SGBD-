package DAL.SeancePersonnes;

import DAL.Personnes.PersonneInitialisation;
import DAL.Seances.SeanceInitialisation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public final class SeancePersonneInitialisation {
    private SeancePersonneInitialisation() { }

    public static void creer(Connection connexion) throws SQLException {
        try (Statement statement = connexion.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS Seance_Personne ("
                + "id_personne INT REFERENCES Personne(id), id_seance INT REFERENCES Seance(id), "
                + "PRIMARY KEY (id_personne, id_seance))");
        }
    }

    public static void inserer(Connection connexion) throws SQLException {
        inserer(connexion, "2025-09-08", "2066", "HelHa", "Systèmes d'exploitation", "Durant", "Richard");
        inserer(connexion, "2025-09-15", "2066", "HelHa", "Systèmes d'exploitation", "Durant", "Richard");
        inserer(connexion, "2025-09-22", "PS02", "SLuc", "Systèmes d'exploitation", "Durant", "Richard");
        inserer(connexion, "2025-09-09", "3023", "HelHa", "Base des réseaux", "Durant", "Richard");
        inserer(connexion, "2025-09-09", "3023", "HelHa", "Base des réseaux", "Ortiz", "Valerie");
        inserer(connexion, "2025-09-16", "PS02", "SLuc", "Base des réseaux", "Durant", "Richard");
        inserer(connexion, "2025-09-23", "PS02", "SLuc", "Base des réseaux", "Durant", "Richard");
    }

    private static void inserer(Connection connexion, String date, String numero, String lieu,
            String cours, String nom, String prenom) throws SQLException {
        int idPersonne = PersonneInitialisation.rechercherId(connexion, nom, prenom);
        int idSeance = SeanceInitialisation.rechercherId(connexion, date, numero, lieu, cours);
        try (PreparedStatement requete = connexion.prepareStatement("INSERT INTO Seance_Personne (id_personne, id_seance) VALUES (?, ?) "
                + "ON CONFLICT (id_personne, id_seance) DO NOTHING")) {
            requete.setInt(1, idPersonne);
            requete.setInt(2, idSeance);
            requete.executeUpdate();
        }
    }
}
