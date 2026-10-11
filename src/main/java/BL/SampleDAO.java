package BL;
import DAL.ConnectionFactory;
import DAL.Sections.SectionDAO;
import DAL.Status.StatusDAO;
import DAL.Cours.CoursDAO;

public class SampleDAO {
//Voir page 24
    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory(
            "jdbc:postgresql://localhost:5432/ecole",
            "postgres",
            "root"
        );

        try (SectionDAO sectionDAO = new SectionDAO(factory);
             StatusDAO statusDAO = new StatusDAO(factory);
             CoursDAO coursDAO = new CoursDAO(factory)) {
            System.out.println("Sections :");
            for (Section section : sectionDAO.getSections()) {
                System.out.println(section.getId() + " - " + section.getNom());
            }

            System.out.println("\nStatus :");
            for (Status status : statusDAO.getStatus()) {
                System.out.println(status.getId() + " - " + status.getNom());
            }

            System.out.println("\nCours :");
            for (Cours cours : coursDAO.getCours()) {
                System.out.println(cours.getId() + " - " + cours.getNom()
                    + " (section : " + cours.getIdSection() + ")");
            }
        }
    }
}
