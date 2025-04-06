package application;

import javafx.collections.FXCollections;

import javafx.collections.transformation.FilteredList;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class KayıtPanelController {
	// Database Configuration
	private static final String DB_URL = "jdbc:mysql://localhost:3306/HrosSQL";
	private static final String DB_USER = "javauser";
	private static final String DB_PASSWORD = "javauser";

	@FXML
	private TextField txtAd;
	@FXML
	private TextField txtCinsiyet;
	@FXML
	private DatePicker dateDogumTarihi;
	@FXML
	private TextField txtTC;
	@FXML
	private TextField txtParola;
	@FXML
	private Button btn_hastaRandevu_Olustur;

	@FXML
	private void handleKaydetAction() {
		if (!validateInputs()) {
			return;
		}

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "INSERT INTO PatientCustomer (username, borndate, usergender, userssn, userpassword) "
					+ "VALUES (?, ?, ?, ?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				// Set parameters
				statement.setString(1, txtAd.getText().trim());
				statement.setString(2, dateDogumTarihi.getValue().format(DateTimeFormatter.ISO_DATE));
				statement.setString(3, txtCinsiyet.getText().trim());
				statement.setString(4, txtTC.getText().trim());
				statement.setString(5, txtParola.getText());
				int affectedRows = statement.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							showAlert(Alert.AlertType.INFORMATION, "Başarılı",
									"Hasta kaydı oluşturuldu!\nHasta ID: " + generatedKeys.getInt(1));
						}
					}
					clearForm();
				}
			}
		} catch (SQLIntegrityConstraintViolationException e) {
			showAlert(Alert.AlertType.ERROR, "Hata", "Bu TC numarası zaten kayıtlı!");
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private boolean validateInputs() {
		StringBuilder errors = new StringBuilder();

		if (txtAd.getText().trim().isEmpty()) {
			errors.append("- Ad alanı boş olamaz\n");
		}

		if (txtTC.getText().trim().length() != 11) {
			errors.append("- TC kimlik numarası 11 haneli olmalıdır\n");
		}

		if (txtParola.getText().length() < 6) {
			errors.append("- Parola en az 6 karakter olmalıdır\n");
		}

		if (dateDogumTarihi.getValue() == null) {
			errors.append("- Doğum tarihi seçilmelidir\n");
		}

		if (errors.length() > 0) {
			showAlert(Alert.AlertType.WARNING, "Geçersiz Giriş", errors.toString());
			return false;
		}

		return true;
	}

	private void clearForm() {
		txtAd.clear();
		txtCinsiyet.clear();
		dateDogumTarihi.setValue(null);
		txtTC.clear();
		txtParola.clear();
		txtAd.requestFocus();
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
