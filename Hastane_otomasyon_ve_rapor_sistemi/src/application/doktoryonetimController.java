package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import java.io.IOException;
import java.sql.*;

public class doktoryonetimController {

	@FXML
	private TableView<Patient> patientTable;
	@FXML
	private TableColumn<Patient, String> colUsername;
	@FXML
	private TableColumn<Patient, String> colBorndate;
	@FXML
	private TableColumn<Patient, String> colGender;
	@FXML
	private TableColumn<Patient, String> colSsn;

	@FXML
	private TextField searchField;
	@FXML
	private Button searchButton;

	private ObservableList<Patient> masterData = FXCollections.observableArrayList();

	@FXML
	private TableView<RandevuTable> randevuTable;
	@FXML
	private TableColumn<RandevuTable, String> colPatientName;
	@FXML
	private TableColumn<RandevuTable, String> colPatientSsn;
	@FXML
	private TableColumn<RandevuTable, String> colPoliklinik;
	@FXML
	private TableColumn<RandevuTable, String> colDoktor;
	@FXML
	private TableColumn<RandevuTable, String> colTarih;
	@FXML
	private TableColumn<RandevuTable, String> colSaat;

	@FXML
	private Button yenilebutton;

	private static final String DB_URL = "jdbc:mysql://localhost:3306/HrosSQL";
	private static final String DB_USER = "javauser";
	private static final String DB_PASSWORD = "javauser";

	@FXML
	public void initialize() {
		// Sütun bağlamaları
		colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
		colBorndate.setCellValueFactory(new PropertyValueFactory<>("borndate"));
		colGender.setCellValueFactory(new PropertyValueFactory<>("usergender"));
		colSsn.setCellValueFactory(new PropertyValueFactory<>("userssn"));
		// Sütun bağlamaları
		colPatientName.setCellValueFactory(new PropertyValueFactory<>("patientName"));
		colPatientSsn.setCellValueFactory(new PropertyValueFactory<>("patientSsn"));
		colPoliklinik.setCellValueFactory(new PropertyValueFactory<>("poliklinik"));
		colDoktor.setCellValueFactory(new PropertyValueFactory<>("doktor"));
		colTarih.setCellValueFactory(new PropertyValueFactory<>("tarih"));
		colSaat.setCellValueFactory(new PropertyValueFactory<>("saat"));

		// Verileri yükle
		loadPatientData();
		// Verileri yükle
		loadRandevuData();
		// Arama özelliğini ayarla
		 searchButton.setOnAction(event -> searchPatients());
	}

	private void loadRandevuData() {
		String query = "SELECT patientname, patientssn, poliklinik, polidoctor, radevudate, randevutime "
				+ "FROM RandevuPatient";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			ObservableList<RandevuTable> randevular = FXCollections.observableArrayList();

			while (rs.next()) {
				RandevuTable randevu = new RandevuTable(rs.getString("patientname"), rs.getString("patientssn"),
						rs.getString("poliklinik"), rs.getString("polidoctor"), rs.getString("radevudate"),
						rs.getString("randevutime"));
				randevular.add(randevu);
			}

			randevuTable.setItems(randevular);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Veritabanı Hatası", "Randevu verileri yüklenemedi: " + e.getMessage());
		}
	}

	private void loadPatientData() {
		String query = "SELECT username, borndate, usergender, userssn FROM PatientCustomer WHERE isatcive = true";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			ObservableList<Patient> patients = FXCollections.observableArrayList();

			while (rs.next()) {
				Patient patient = new Patient(rs.getString("username"), rs.getString("borndate"),
						rs.getString("usergender"), rs.getString("userssn"));
				patients.add(patient);
			}

			patientTable.setItems(patients);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert("Veritabanı Hatası", "Hasta verileri yüklenemedi: " + e.getMessage());
		}
	}
	private void searchPatients() {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
        	loadPatientData();
            return;
        }
        
        String query = "SELECT username, borndate, usergender, userssn FROM PatientCustomer " +
                      "WHERE isatcive = true AND username LIKE ?";
        executeParameterizedQuery(query, "%" + searchText + "%");
    }

	@FXML
	private void handleButtonAction(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("GirisPaneli.fxml"));
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

	 private void executeParameterizedQuery(String query, String param) {
	        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	             PreparedStatement stmt = conn.prepareStatement(query)) {
	            
	            stmt.setString(1, param);
	            ResultSet rs = stmt.executeQuery();
	            
	            fillTableWithResults(rs);
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	            showAlert("Veritabanı Hatası", "Arama sırasında hata oluştu: " + e.getMessage());
	        }
	    }

	    private void executePatientQuery(String query) {
	        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	             Statement stmt = conn.createStatement();
	             ResultSet rs = stmt.executeQuery(query)) {
	            
	            fillTableWithResults(rs);
	            
	        } catch (SQLException e) {
	            e.printStackTrace();
	            showAlert("Veritabanı Hatası", "Veriler yüklenemedi: " + e.getMessage());
	        }
	    }

	    private void fillTableWithResults(ResultSet rs) throws SQLException {
	        ObservableList<Patient> patients = FXCollections.observableArrayList();
	        
	        while (rs.next()) {
	            Patient patient = new Patient(
	                rs.getString("username"),
	                rs.getString("borndate"),
	                rs.getString("usergender"),
	                rs.getString("userssn")
	            );
	            patients.add(patient);
	        }
	        
	        patientTable.setItems(patients);
	    }

	    @FXML
	    private void handleRefresh() {
	    	loadPatientData();
	        searchField.clear();
	    }


	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
