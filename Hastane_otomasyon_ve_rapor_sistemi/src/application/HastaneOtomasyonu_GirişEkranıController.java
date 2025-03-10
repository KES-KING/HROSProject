package application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.Node;
import javafx.scene.Parent;

public class HastaneOtomasyonu_GirişEkranıController {

    @FXML
    private TextField txtUsername;

    @FXML
    private TextField txtPassword;
    
    @FXML
    private TextField txtUsernameE;

    @FXML
    private TextField txtPasswordD;

    private Connection connectDB() {
        String url = "jdbc:mysql://127.0.0.1:3306/Hrosmanagement";
        String user = "appuser";  // MySQL kullanıcı adı
        String password = "a1iA9GaXpjTKGq9lEgB!";  // Eğer şifre koyduysan buraya ekle

        try {
            Connection conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String tcNo = txtUsername.getText();
        String sifre = txtPassword.getText();

        if (tcNo.isEmpty() || sifre.isEmpty()) {
            showAlert(AlertType.WARNING, "Uyarı", "Tüm alanları doldurun!");
            return;
        }

        String sql = "SELECT yonetici FROM personel WHERE usertc = ? AND passwd = ?";

        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tcNo);
            stmt.setString(2, sifre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int yonetici = rs.getInt("yonetici"); // 1 veya 0 değerini al

                showAlert(AlertType.INFORMATION, "Başarılı", "Giriş başarılı!");

                if (yonetici == 1) {
                    openDashboard(event, "PersonelPaneli.fxml"); // Yönetici ekranı aç 1
                } else {
                    openDashboard(event, "doktor yönetim paneli.fxml"); // Kullanıcı ekranı aç 0
                }
            } else {
                showAlert(AlertType.ERROR, "Hata", "T.C. Kimlik Numarası veya Şifre hatalı!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Bağlantı Hatası", "Veritabanı bağlantısı başarısız!");
        }
    }

    @FXML
    private void handleLoginnorm(ActionEvent event) {
        String tcNo = txtUsernameE.getText();
        String sifre = txtPasswordD.getText();

        if (tcNo.isEmpty() || sifre.isEmpty()) {
            showAlert(AlertType.WARNING, "Uyarı", "Tüm alanları doldurun!");
            return;
        }

        String sql = "SELECT * FROM personel WHERE usertc = ? AND passwd = ?";
        
        try (Connection conn = connectDB();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tcNo);
            stmt.setString(2, sifre);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                showAlert(AlertType.INFORMATION, "Başarılı", "Giriş başarılı!");
                opennormekran(event);
            } else {
                showAlert(AlertType.ERROR, "Hata", "T.C. Kimlik Numarası veya Şifre hatalı!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Bağlantı Hatası", "Veritabanı bağlantısı başarısız!");
        }
    }

    private void openDashboard(ActionEvent event, String fxmlFile) throws IOException {
        // Yeni pencereyi aç
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Ana Sayfa");
        stage.setScene(new Scene(root));
        stage.show();

        // Giriş ekranını kapat
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    private void openControldashboard(ActionEvent event) throws IOException {
        // Yeni pencereyi aç
        FXMLLoader loader = new FXMLLoader(getClass().getResource("PersonelPaneli.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Personel Yönetim Paneli");
        stage.setScene(new Scene(root));
        stage.show();

        // Giriş ekranını kapat
        ((Node) event.getSource()).getScene().getWindow().hide();
    }
    private void opennormekran(ActionEvent event) throws IOException {
        // Yeni pencereyi aç
        FXMLLoader loader = new FXMLLoader(getClass().getResource("HastaRandevuPaneli.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Ana Sayfa");
        stage.setScene(new Scene(root));
        stage.show();

        // Giriş ekranını kapat
        ((Node) event.getSource()).getScene().getWindow().hide();
    }

    public void openkayıtekranı(ActionEvent event) {
        try {
            // Yeni formu yükle
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("kayıt paneli.fxml"));
            Parent root = fxmlLoader.load();

            // Yeni pencereyi aç
            Stage stage = new Stage();
            stage.setTitle("Personel Kayıt Ekranı");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
