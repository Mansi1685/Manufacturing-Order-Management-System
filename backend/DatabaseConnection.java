import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:postgresql://localhost:5432/manufacturing_erp";

    private static final String USER =
            "postgres";

    private static final String PASSWORD =
            "Mansi@1685";

    public static Connection getConnection() throws SQLException {

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }

    public static void main(String[] args) {

        try {

            Connection connection = getConnection();

            System.out.println("Database connected successfully!");

            connection.close();

        } catch (SQLException e) {

            System.out.println("Database connection failed!");
            e.printStackTrace();

        }
    }
}