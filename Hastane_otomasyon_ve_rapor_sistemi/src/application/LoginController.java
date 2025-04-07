package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import java.io.IOException;
import java.sql.*;

public class LoginController {
    @FXML private TextField txtTC;
    @FXML private TextField txtSifre;
    @FXML private TextField patienttC;
    @FXML private TextField PatientPassword;
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/HrosSQL?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "javauser";
    private static final String DB_PASSWORD = "javauser";

    @FXML
    private void handleGirisAction() {
        String tcNo = txtTC.getText().trim();
        String sifre = txtSifre.getText().trim();

        if (!isValidTC(tcNo)) {
            showAlert("Hata", "Geçersiz TC Kimlik No formatı!");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id, userpassword, administration FROM Personeldoc WHERE userssn = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tcNo);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String dbSifre = rs.getString("userpassword");
                    
                    if (sifre.equals(dbSifre)) {
                        boolean isAdmin = rs.getBoolean("administration");
                        int userId = rs.getInt("id");
                        
                        SessionManager.login(userId, isAdmin);
                        redirectToDashboard(isAdmin);
                    } else {
                        showAlert("Hata", "Hatalı şifre!");
                    }
                } else {
                    showAlert("Hata", "Kullanıcı bulunamadı!");
                }
            }
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", "Bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handlepublic() {
        String tcNo = patienttC.getText().trim();
        String sifre = PatientPassword.getText().trim();
        
        if (tcNo.isEmpty() || sifre.isEmpty()) {
            showAlert("Hata", "TC Kimlik No ve şifre alanları boş bırakılamaz!");
            return;
        }
        
        if (!isValidTC(tcNo)) {
            showAlert("Hata", "Geçersiz TC Kimlik No formatı!");
            return;
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT id FROM PatientCustomer WHERE userssn = ? AND userpassword = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, tcNo);
                stmt.setString(2, sifre);
                
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    SessionManager.login(userId, false);
                    openRandevuPanel(userId);
                } else {
                    showAlert("Hata", "Geçersiz TC Kimlik No veya şifre!");
                }
            }
        } catch (SQLException e) {
            showAlert("Veritabanı Hatası", "Bağlantı hatası: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void openRandevuPanel(int userId) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HastaRandevuPaneli.fxml"));
            Parent root = loader.load();
            
            // Controller'a kullanıcı ID'sini geçir
            hastarandevucontroller controller = loader.getController();
            controller.setCurrentUserId(userId);
            
            Stage stage = (Stage) patienttC.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hasta Randevu Paneli");
            
        } catch (IOException e) {
            showAlert("Dosya Hatası", "Arayüz dosyası yüklenemedi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isValidTC(String tcNo) {
        return tcNo != null && tcNo.length() == 11 && tcNo.matches("\\d+");
    }

    private void redirectToDashboard(boolean isAdmin) {
        try {
            String fxmlPath = isAdmin ? "PersonelPaneli.fxml" : "doktoryönetimpaneli.fxml";
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            
            Stage stage = (Stage) txtTC.getScene().getWindow();
            stage.setScene(new Scene(root));
            
        } catch (Exception e) {
            showAlert("Hata", "Panel yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("kayıtpanelitasarım.fxml"));
            Parent newFormParent = loader.load();
            
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(newFormParent);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Hata", "Kayıt paneli yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}