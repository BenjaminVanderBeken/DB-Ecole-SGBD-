package DAL;
//accès aux données
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConnectionFactory {

    private final String url;
    private final String user;
    private final String password;

    public ConnectionFactory(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public Connection createConnection() throws SQLException {
        // La création des connexions est regroupée ici.
        return DriverManager.getConnection(url, user, password);
    }
}
