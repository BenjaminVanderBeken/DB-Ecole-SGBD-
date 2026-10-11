package BL;

public class Cours {
    private final int id;
    private String nom;
    private int idSection;

    public Cours(int id, String nom, int idSection) {
        this.id = id;
        this.nom = nom;
        this.idSection = idSection;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public int getIdSection() { return idSection; }
    public void setIdSection(int idSection) { this.idSection = idSection; }
}

