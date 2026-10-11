package DAL.Status;

import BL.Status;
import java.util.ArrayList;

public interface IStatusDAO {
    ArrayList<Status> getStatus();
    boolean addStatus(String nom);
    boolean updateStatus(int id, String nom);
    boolean deleteStatus(int id);
}

