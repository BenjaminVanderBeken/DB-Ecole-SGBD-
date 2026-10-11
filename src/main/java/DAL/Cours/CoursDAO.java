package DAL.Cours;

import BL.Cours;
import DAL.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CoursDAO implements ICoursDAO, AutoCloseable {
    private final Connection connexion;
    private PreparedStatement insertCours;
    private PreparedStatement updateCours;
    private PreparedStatement deleteCours;
    private PreparedStatement getCours;

    public CoursDAO(ConnectionFactory factory) throws SQLException {
        connexion = factory.createConnection();
        // On prépare les requêtes une fois pour les réutiliser ensuite.
        try {
            insertCours = connexion.prepareStatement(
                "INSERT INTO Cours (nom, id_section) VALUES (?, ?)");
            updateCours = connexion.prepareStatement(
                "UPDATE Cours SET nom = ?, id_section = ? WHERE id = ?");
            deleteCours = connexion.prepareStatement(
                "DELETE FROM Cours WHERE id = ?");
            getCours = connexion.prepareStatement(
                "SELECT id, nom, id_section FROM Cours ORDER BY id");
        } catch (SQLException e) {
            try {
                close();
            } catch (SQLException fermetureErreur) {
                e.addSuppressed(fermetureErreur);
            }
            throw e;
        }
    }

    @Override
    public ArrayList<Cours> getCours() {
        ArrayList<Cours> liste = new ArrayList<>();
        try (ResultSet resultat = getCours.executeQuery()) {
            while (resultat.next()) {
                liste.add(new Cours(resultat.getInt("id"), resultat.getString("nom"), resultat.getInt("id_section")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de lire la table Cours.", e);
        }
        return liste;
    }

    @Override
    public boolean addCours(String nom, int idSection) {
        try {
            insertCours.setString(1, nom);
            insertCours.setInt(2, idSection);
            return insertCours.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter un Cours.", e);
        }
    }

    @Override
    public boolean updateCours(int id, String nom, int idSection) {
        try {
            updateCours.setString(1, nom);
            updateCours.setInt(2, idSection);
            updateCours.setInt(3, id);
            return updateCours.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de modifier un Cours.", e);
        }
    }

    @Override
    public boolean deleteCours(int id) {
        try {
            deleteCours.setInt(1, id);
            return deleteCours.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer un Cours.", e);
        }
    }

    @Override
    public void close() throws SQLException {
        // On ferme toutes les requêtes avant la connexion, même si l'une échoue.
        SQLException erreur = null;
        PreparedStatement[] requetes = {
            insertCours, updateCours, deleteCours, getCours
        };
        for (PreparedStatement requete : requetes) {
            if (requete != null) {
                try {
                    requete.close();
                } catch (SQLException e) {
                    if (erreur == null) {
                        erreur = e;
                    } else {
                        erreur.addSuppressed(e);
                    }
                }
            }
        }
        try {
            connexion.close();
        } catch (SQLException e) {
            if (erreur == null) {
                erreur = e;
            } else {
                erreur.addSuppressed(e);
            }
        }
        if (erreur != null) {
            throw erreur;
        }
    }
}

