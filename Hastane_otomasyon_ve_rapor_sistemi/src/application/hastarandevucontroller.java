package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import javafx.collections.FXCollections;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

public class hastarandevucontroller {

	// UI Components
    @FXML private ComboBox<String> cbPersoneller, cbPoliklinikler, timeComboBox, timeComboBox2;
    @FXML private TextField RandevuAl_isimSoyisim, RandevuAl_KimlikNumarasi;
    @FXML private DatePicker RandevuAl_TarihSecimi, Randevularım_tarihSecimi;
    @FXML private TableView<Randevu> tableView_Randevularım;
    @FXML private TableColumn<Randevu, String> poliklinikColumn, doktorColumn, tarihColumn, saatColumn;

    // Veritabanı ve oturum yönetimi
    private static final String DB_URL = "jdbc:mysql://localhost:3306/HrosSQL";
    private static final String DB_USER = "javauser";
    private static final String DB_PASSWORD = "javauser";
    private int currentUserId;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        verileriYenile(); // Kullanıcı ID'si ayarlandığında tabloyu yenile
    }

    @FXML
    public void initialize() {
        // Combobox'ları ve tabloyu başlat
        personelComboboxDoldur();
        poliklinikComboboxDoldur();
        setupTimeComboBoxes();
        setupTableView();
    }

    private void setupTimeComboBoxes() {
        ObservableList<String> times = FXCollections.observableArrayList();
        for (int hour = 0; hour < 24; hour++) {
            for (int minute = 0; minute < 60; minute += 15) {
                times.add(String.format("%02d:%02d", hour, minute));
            }
        }
        timeComboBox.setItems(times);
        timeComboBox2.setItems(times);
    }

    private void setupTableView() {
        poliklinikColumn.setCellValueFactory(new PropertyValueFactory<>("poliklinik"));
        doktorColumn.setCellValueFactory(new PropertyValueFactory<>("doktor"));
        tarihColumn.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        saatColumn.setCellValueFactory(new PropertyValueFactory<>("saat"));
    }

    @FXML
    private void handleguncelle() {
        Randevu seciliRandevu = tableView_Randevularım.getSelectionModel().getSelectedItem();
        if (seciliRandevu == null) {
            return;
        }

        LocalDate yeniTarih = Randevularım_tarihSecimi.getValue();
        String yeniSaat = timeComboBox2.getValue();
        if (yeniTarih == null || yeniSaat == null) {
            return;
        }

        String sql = "UPDATE RandevuPatient SET radevudate = ?, randevutime = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, yeniTarih.format(DateTimeFormatter.ISO_DATE));
            stmt.setString(2, yeniSaat.trim());
            stmt.setInt(3, seciliRandevu.getId());
            
            if (stmt.executeUpdate() > 0) {
                verileriYenile();
            }
        } catch (SQLException e) {
        }
    }

    @FXML
    private void handleKaydetAction() {
        if (!validateInputs()) return;

        String sql = "INSERT INTO RandevuPatient (patient_id, patientname, poliklinik, polidoctor, patientssn, radevudate, randevutime) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, currentUserId);
            stmt.setString(2, RandevuAl_isimSoyisim.getText().trim());
            stmt.setString(3, cbPoliklinikler.getValue());
            stmt.setString(4, cbPersoneller.getValue());
            stmt.setString(5, RandevuAl_KimlikNumarasi.getText().trim());
            stmt.setString(6, RandevuAl_TarihSecimi.getValue().format(DateTimeFormatter.ISO_DATE));
            stmt.setString(7, timeComboBox.getValue());
            
            if (stmt.executeUpdate() > 0) {
                verileriYenile();
                clearForm();
            }
        } catch (SQLException e) {
        }
    }

    @FXML
    private void verileriYenile() {
        String sql = "SELECT id, poliklinik, polidoctor, radevudate, randevutime FROM RandevuPatient "
                   + "WHERE patient_id = ?";
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            
            ObservableList<Randevu> randevular = FXCollections.observableArrayList();
            while (rs.next()) {
                randevular.add(new Randevu(
                    rs.getInt("id"),
                    currentUserId,
                    rs.getString("poliklinik"),
                    rs.getString("polidoctor"),
                    rs.getString("radevudate"),
                    rs.getString("randevutime")
                ));
            }
            tableView_Randevularım.setItems(randevular);
            
        } catch (SQLException e) {
        }
    }

	private boolean validateInputs() {
		StringBuilder errors = new StringBuilder();

		if (RandevuAl_isimSoyisim.getText().trim().isEmpty()) {
			errors.append("- Ad alanı boş olamaz\n");
		}

		if (RandevuAl_KimlikNumarasi.getText().trim().length() != 11) {
			errors.append("- TC kimlik numarası 11 haneli olmalıdır\n");
		}

		if (RandevuAl_TarihSecimi.getValue() == null) {
			errors.append("- Randevu tarihi seçilmelidir\n");
		}

		if (errors.length() > 0) {
			showAlert(Alert.AlertType.WARNING, "Geçersiz Giriş", errors.toString());
			return false;
		}

		return true;
	}

	private void clearForm() {
		RandevuAl_isimSoyisim.clear();
		RandevuAl_KimlikNumarasi.clear();
		RandevuAl_TarihSecimi.setValue(null);
		RandevuAl_isimSoyisim.requestFocus();
	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void personelComboboxDoldur() {
		ObservableList<String> personelListesi = FXCollections.observableArrayList();

		String sql = "SELECT CONCAT(username, ' ', usersurname) AS tam_ad FROM Personeldoc";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				personelListesi.add(rs.getString("tam_ad"));
			}
			cbPersoneller.setItems(personelListesi);

		} catch (SQLException e) {
			e.printStackTrace();
			// Hata mesajı göster
		}
	}

	private void poliklinikComboboxDoldur() {
		ObservableList<String> poliklinikListesi = FXCollections.observableArrayList();

		String sql = "SELECT poliname FROM Poliklinik";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {

			while (rs.next()) {
				poliklinikListesi.add(rs.getString("poliname"));
			}
			cbPoliklinikler.setItems(poliklinikListesi);

		} catch (SQLException e) {
			e.printStackTrace();
			// Hata mesajı göster
		}
	}

	@FXML
	private void handleButtonAction(ActionEvent event) {
		try {
			// Yeni formun FXML dosyasını yükleyin.
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GirisPaneli.fxml"));
			Parent newFormParent = loader.load();

			// Mevcut stage'i elde edin.
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

			// Yeni bir sahne oluşturun ve stage'e atayın.
			Scene scene = new Scene(newFormParent);
			stage.setScene(scene);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
