package DAL.Sections;

import BL.Section;
import DAL.ConnectionFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SectionDAO implements ISectionDAO, AutoCloseable {
    private final Connection connexion;
    private PreparedStatement insertSection;
    private PreparedStatement updateSection;
    private PreparedStatement deleteSection;
    private PreparedStatement getIDSection;
    private PreparedStatement getSections;
//Voir page 42 du cours 
    public SectionDAO(ConnectionFactory factory) throws SQLException {
        connexion = factory.createConnection();
        // On prépare les requêtes une fois pour les réutiliser ensuite.
        try {
            insertSection = connexion.prepareStatement(
                "INSERT INTO Section (nom) VALUES (?)");
            updateSection = connexion.prepareStatement(
                "UPDATE Section SET nom = ? WHERE id = ?");
            deleteSection = connexion.prepareStatement(
                "DELETE FROM Section WHERE id = ?");
            getIDSection = connexion.prepareStatement(
                "SELECT id FROM Section WHERE nom = ?");
            getSections = connexion.prepareStatement(
                "SELECT id, nom FROM Section ORDER BY id");
        } catch (SQLException e) {
            try {
                close();
            } catch (SQLException fermetureErreur) {
                e.addSuppressed(fermetureErreur);
            }
            throw e;
        }
    }
//Voir page 35 du cours 
    @Override
    public ArrayList<Section> getSections() {
        ArrayList<Section> liste = new ArrayList<>();
        try (ResultSet resultat = getSections.executeQuery()) {
            while (resultat.next()) {
                liste.add(new Section(resultat.getInt("id"), resultat.getString("nom")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de lire la table Section.", e);
        }
        return liste;
    }

    @Override
    public int getIDSection(String nom) {
        try {
            getIDSection.setString(1, nom);
            try (ResultSet resultat = getIDSection.executeQuery()) {
                if (resultat.next()) {
                    return resultat.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de rechercher la section.", e);
        }
        return -1;
    }

    @Override
    public boolean addSection(String nom) {
        try {
            insertSection.setString(1, nom);
            return insertSection.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible d'ajouter un Section.", e);
        }
    }

    @Override
    public boolean updateSection(int id, String nom) {
        try {
            updateSection.setString(1, nom);
            updateSection.setInt(2, id);
            return updateSection.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de modifier un Section.", e);
        }
    }

    @Override
    public boolean deleteSection(int id) {
        try {
            deleteSection.setInt(1, id);
            return deleteSection.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new IllegalStateException("Impossible de supprimer un Section.", e);
        }
    }

    @Override
    public void close() throws SQLException {
        // On ferme toutes les requêtes avant la connexion, même si l'une échoue.
        SQLException erreur = null;
        PreparedStatement[] requetes = {
            insertSection, updateSection, deleteSection, getIDSection, getSections
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
