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
import java.time.format.DateTimeFormatter;

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
	private ObservableList<Patient> masterData = FXCollections.observableArrayList();

	@FXML
	private Button searchButton;

	@FXML
	private TextField searchField;

	// odd day ekleme sistemi başlangıç
	@FXML
	private TextField doctorname;
	@FXML
	private TextField doctortc;
	@FXML
	private DatePicker doctortarih;
	@FXML
	private ComboBox doctortime;
	@FXML
	Button btnRandevuEkle1;

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
	private TableView<OffDay> offDayTable;
	@FXML
	private TableColumn<OffDay, String> colDocName;
	@FXML
	private TableColumn<OffDay, String> colDocTc;
	@FXML
	private TableColumn<OffDay, String> colDate;
	@FXML
	private TableColumn<OffDay, String> colTime;
	@FXML
	private TableColumn<OffDay, String> colStatus;
	@FXML
	private TableColumn<OffDay, String> colComment;

	@FXML
	private Button yenilebutton;

	private int currentUserId;
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
		colDocName.setCellValueFactory(new PropertyValueFactory<>("docName"));
		colDocTc.setCellValueFactory(new PropertyValueFactory<>("docTc"));
		colDate.setCellValueFactory(new PropertyValueFactory<>("docDate"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("docTime"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("docStatus"));
		colComment.setCellValueFactory(new PropertyValueFactory<>("docComment"));

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
		setupTimeComboBoxes();
		loadOffDaysForUser(currentUserId);
	}

	public void setCurrentUserId(int userId) {
		this.currentUserId = userId;

		loadOffDaysForUser(currentUserId);
	}

	@FXML
	private void kaydetoffday() {
		if (!validateInputs())
			return;

		String sql = "INSERT INTO offday (patient_id, docname, doctc, docdate, doctime) " + "VALUES (?, ?, ?, ?, ?)";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, currentUserId);
			stmt.setString(2, doctorname.getText().trim());
			stmt.setString(3, doctortc.getText().trim());
			stmt.setString(4, doctortime.getValue().toString());
			stmt.setString(5, doctortarih.getValue().format(DateTimeFormatter.ISO_DATE));

			if (stmt.executeUpdate() > 0) {
				clearForm();
				loadOffDaysForUser(currentUserId);
			}
		} catch (SQLException e) {
		}
	}

	private void loadOffDaysForUser(int userId) {
		String query = "SELECT id, patient_id, docname, doctc, docdate, doctime, docstatus, doccomment "
				+ "FROM offday WHERE patient_id = ?";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();

			ObservableList<OffDay> offDays = FXCollections.observableArrayList();
			while (rs.next()) {
				offDays.add(new OffDay(rs.getInt("id"), rs.getInt("patient_id"), rs.getString("docname"),
						rs.getString("doctc"), rs.getString("docdate"), rs.getString("doctime"),
						rs.getString("docstatus"), rs.getString("doccomment")));
			}

			offDayTable.setItems(offDays);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void setupTimeComboBoxes() {
		ObservableList<String> times = FXCollections.observableArrayList();
		for (int hour = 0; hour < 24; hour++) {
			for (int minute = 0; minute < 60; minute += 15) {
				times.add(String.format("%02d:%02d", hour, minute));
			}
		}
		doctortime.setItems(times);
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

		String query = "SELECT username, borndate, usergender, userssn FROM PatientCustomer "
				+ "WHERE isatcive = true AND username LIKE ?";
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
			Patient patient = new Patient(rs.getString("username"), rs.getString("borndate"),
					rs.getString("usergender"), rs.getString("userssn"));
			patients.add(patient);
		}

		patientTable.setItems(patients);
	}

	@FXML
	private void handleRefresh() {
		loadPatientData();
		searchField.clear();
	}

	private boolean validateInputs() {
		StringBuilder errors = new StringBuilder();

		if (doctorname.getText().trim().isEmpty()) {
			errors.append("- Ad alanı boş olamaz\n");
		}

		if (doctortc.getText().trim().length() != 11) {
			errors.append("- TC kimlik numarası 11 haneli olmalıdır\n");
		}

		if (doctortarih.getValue() == null) {
			errors.append("- Randevu tarihi seçilmelidir\n");
		}
		return true;
	}

	private void clearForm() {
		doctorname.clear();
		doctortc.clear();
		doctortarih.setValue(null);
	}

	private void showAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
