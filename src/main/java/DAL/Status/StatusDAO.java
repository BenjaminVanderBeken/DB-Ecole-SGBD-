package DAL.Status;

import BL.Status;
import DAL.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class StatusDAO implements IStatusDAO, AutoCloseable {
    private final Connection connexion;
    private PreparedStatement insertStatus;
    private PreparedStatement updateStatus;
    private PreparedStatement deleteStatus;
    private PreparedStatement getStatus;

    public StatusDAO(ConnectionFactory factory) throws SQLException {
        connexion = factory.createConnection();
        // On prépare les requêtes une fois pour les réutiliser ensuite.
        try {
            insertStatus = connexion.prepareStatement(
                "INSERT INTO Status (nom) VALUES (?)");
            updateStatus = connexion.prepareStatement(
                "UPDATE Status SET nom = ? WHERE id = ?");
            deleteStatus = connexion.prepareStatement(
                "DELETE FROM Status WHERE id = ?");
            getStatus = connexion.prepareStatement(
                "SELECT id, nom FROM Status ORDER BY id");
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
    public ArrayList<Status> getStatus() {
        ArrayList<Status> liste = new ArrayList<>();
        try (ResultSet resultat = getStatus.executeQuery()) {
            while (resultat.next()) {
                liste.add(new Status(resultat.getInt("id"), resultat.getString("nom")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de lire la table Status.", e);
        }
        return liste;
    }

    @Override
    public boolean addStatus(String nom) {
        try {
            insertStatus.setString(1, nom);
            return insertStatus.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter un Status.", e);
        }
    }

    @Override
    public boolean updateStatus(int id, String nom) {
        try {
            updateStatus.setString(1, nom);
            updateStatus.setInt(2, id);
            return updateStatus.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de modifier un Status.", e);
        }
    }

    @Override
    public boolean deleteStatus(int id) {
        try {
            deleteStatus.setInt(1, id);
            return deleteStatus.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer un Status.", e);
        }
    }

    @Override
    public void close() throws SQLException {
        // On ferme toutes les requêtes avant la connexion, même si l'une échoue.
        SQLException erreur = null;
        PreparedStatement[] requetes = {
            insertStatus, updateStatus, deleteStatus, getStatus
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

