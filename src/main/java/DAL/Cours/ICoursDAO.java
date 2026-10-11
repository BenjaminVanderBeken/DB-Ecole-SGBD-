package DAL.Cours;

import BL.Cours;
import java.util.ArrayList;

public interface ICoursDAO {
    ArrayList<Cours> getCours();
    boolean addCours(String nom, int idSection);
    boolean updateCours(int id, String nom, int idSection);
    boolean deleteCours(int id);
}

