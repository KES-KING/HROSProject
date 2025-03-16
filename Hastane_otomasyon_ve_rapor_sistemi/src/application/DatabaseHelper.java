package application;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseHelper {
    private static DatabaseHelper instance;
    private Connection connection;
    private final String url = "jdbc:mysql://127.0.0.1:3306/Hrosmanagement";
    private final String user = "appuser";
    private final String password = "a1iA9GaXpjTKGq9lEgB!";

    private DatabaseHelper() {
        // Bağlantıyı başlangıçta açmayıp gerektiğinde açacağız.
    }

    public static DatabaseHelper getInstance() {
        if (instance == null) {
            instance = new DatabaseHelper();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            // Bağlantı kapalıysa veya hiç açılmamışsa yeniden aç
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Veritabanına yeniden bağlanıldı!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Veritabanı bağlantısı başarısız: " + e.getMessage());
        }
        return connection;
    }
}