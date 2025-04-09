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
import java.util.Arrays;
import java.util.List;

public class PersonelController {

	@FXML
	private ImageView imgPersonel;
	private String resimDosyaYolu;
	private static final String RESIM_KLASOR = "personel_resimleri";

	// JavaFX UI Elements
	@FXML
	private TextField txtAd;
	@FXML
	private TextField txtSoyad;
	@FXML
	private TextField txtCinsiyet;
	@FXML
	private DatePicker dateDogumTarihi;
	@FXML
	private TextField txtEposta;
	@FXML
	private TextField txtTelefon;
	@FXML
	private TextField txtTC;
	@FXML
	private PasswordField txtParola;
	@FXML
	private Button btnKaydet;
	// personel sgk

	@FXML
	private TextField personelPaneli_sgk_girisTarihi;
	@FXML
	private TextField personelPaneli_sgk_CisikTarihi;

	// polinik ekleme
	@FXML
	private TextField PoliklinikIslemler_poliklinikAdi;
	@FXML
	private Button btn_PoliklinikIslemler_ekle;
	@FXML
	private Button btn_PoliklinikIslemler_sil;
	@FXML
	private Button listeyenile;
	@FXML
	private Button btn_personelPaneli_sil;
	@FXML
	private Button btn_personelPaneli_guncelle;

	// Personel Listesi Bileşenleri
	@FXML
	private TableView<Personel> personelTableView;
	@FXML
	private TableColumn<Personel, String> colAd;
	@FXML
	private TableColumn<Personel, String> colSoyad;
	@FXML
	private TableColumn<Personel, String> colCinsiyet;
	@FXML
	private TableColumn<Personel, Integer> colYas;
	@FXML
	private TableColumn<Personel, String> colTcNo;
	@FXML
	private TableColumn<Personel, String> colTelefon;
	@FXML
	private TableColumn<Personel, String> colEposta;
	// poliklinikler

	@FXML
	private TableView<Poliklinik> poliklinikTableView;
	@FXML
	private TableColumn<Poliklinik, String> colPoliklinikAdi;
	@FXML
	private TableColumn<Poliklinik, String> colDoktorAdi;
	@FXML
	private TableColumn<Poliklinik, String> colDoktorTC;
	@FXML
	private TableColumn<Poliklinik, String> cololusturma;

	private ObservableList<Poliklinik> poliklinikListesi = FXCollections.observableArrayList();

	@FXML
	private TextField txtDoktorAdi;
	@FXML
	private TextField txtDoktorSSN;
	@FXML
	private TextArea onaydetayid;
	@FXML
	private ComboBox offtimecombobox;

	// arama kısmı
	@FXML
	private TextField txtAramaTC;
	@FXML
	private Button btnAra;
	@FXML
	private Button offdayguncelle;
	@FXML
	private FilteredList<Personel> filteredPersonelList;
	
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

	// Database Configuration
	private static final String DB_URL = "jdbc:mysql://localhost:3306/HrosSQL";
	private static final String DB_USER = "javauser";
	private static final String DB_PASSWORD = "javauser";

	private int currentUserId;

	private ObservableList<Personel> personelListesi = FXCollections.observableArrayList();
	private List<String> isimListesi = Arrays.asList("Onaylandı", "Reddedildi");
	@FXML
	private void initialize() {
		File klasor = new File(RESIM_KLASOR);
		if (!klasor.exists()) {
			klasor.mkdir();
		}
		// Tablo başlatma
		colAd.setCellValueFactory(cellData -> cellData.getValue().adProperty());
		colSoyad.setCellValueFactory(cellData -> cellData.getValue().soyadProperty());
		colCinsiyet.setCellValueFactory(cellData -> cellData.getValue().cinsiyetProperty());
		colYas.setCellValueFactory(cellData -> cellData.getValue().yasProperty().asObject());
		colTcNo.setCellValueFactory(cellData -> cellData.getValue().tcNoProperty());
		colTelefon.setCellValueFactory(cellData -> cellData.getValue().telefonProperty());
		colEposta.setCellValueFactory(cellData -> cellData.getValue().epostaProperty());

		colPoliklinikAdi.setCellValueFactory(new PropertyValueFactory<>("poliklinikAdi"));
		colDoktorAdi.setCellValueFactory(new PropertyValueFactory<>("doktorAdi"));
		colDoktorTC.setCellValueFactory(new PropertyValueFactory<>("doktorTC"));
		cololusturma.setCellValueFactory(new PropertyValueFactory<>("olusturma"));
		
		colDocName.setCellValueFactory(new PropertyValueFactory<>("docName"));
		colDocTc.setCellValueFactory(new PropertyValueFactory<>("docTc"));
		colDate.setCellValueFactory(new PropertyValueFactory<>("docDate"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("docTime"));
		colStatus.setCellValueFactory(new PropertyValueFactory<>("docStatus"));
		colComment.setCellValueFactory(new PropertyValueFactory<>("docComment"));

		// Verileri yükle
		verileriYukle();
		verileriYenile();
		LoadOffData();
		OffDayGüncelle();
		
		offtimecombobox.getItems().addAll(isimListesi);
		
	}

	public void setCurrentUserId(int userId) {
		this.currentUserId = userId;
	}
	@FXML
    private void OffDayGüncelle() {
        OffDay secilioff = offDayTable.getSelectionModel().getSelectedItem();
        if (secilioff == null) {
            return;
        }

        String onayaciklama = onaydetayid.getText();
        String onaydetay = offtimecombobox.getValue().toString();
        if (onayaciklama == null || onaydetay == null) {
            return;
        }

        String sql = "UPDATE offday SET docstatus = ?, doccomment = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, onaydetay);
            stmt.setString(2, onayaciklama);
            stmt.setInt(3, secilioff.getId());
            
            if (stmt.executeUpdate() > 0) {
            	LoadOffData();
            }
        } catch (SQLException e) {
        }
    }
	private void LoadOffData() {
		String query = "SELECT id, patient_id, docname, doctc, docdate, doctime, docstatus, doccomment "
				+ "FROM offday";

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {


			ObservableList<OffDay> offDays = FXCollections.observableArrayList();
			while (rs.next()) {
				offDays.add(new OffDay(rs.getInt("id"), rs.getInt("patient_id"), rs.getString("docname"),
						rs.getString("doctc"), rs.getString("docdate"), rs.getString("doctime"),
						rs.getString("docstatus"), rs.getString("doccomment")));
			}

			offDayTable.setItems(offDays);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
		}
	}


	@FXML
	private void handleResimEkleAction() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Personel Resmi Seçin");

		// Dosya türü filtreleri
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter(
				"Resim Dosyaları (*.jpg, *.png, *.jpeg)", "*.jpg", "*.png", "*.jpeg");
		fileChooser.getExtensionFilters().add(extFilter);

		// Dosya seçme penceresini göster
		Stage stage = (Stage) imgPersonel.getScene().getWindow();
		File secilenDosya = fileChooser.showOpenDialog(stage);

		if (secilenDosya != null) {
			try {
				// Resmi ImageView'da göster
				Image image = new Image(secilenDosya.toURI().toString());
				imgPersonel.setImage(image);

				// Resmi kaydetmek için yeni dosya yolu oluştur
				String yeniDosyaAdi = System.currentTimeMillis() + "_" + secilenDosya.getName();
				File hedefDosya = new File(RESIM_KLASOR + File.separator + yeniDosyaAdi);

				// Dosyayı kopyala
				Files.copy(secilenDosya.toPath(), hedefDosya.toPath(), StandardCopyOption.REPLACE_EXISTING);

				resimDosyaYolu = hedefDosya.getPath();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@FXML
	private void handleAraActionDB() {
		String arananTC = txtAramaTC.getText().trim();

		if (arananTC.isEmpty()) {
			verileriYenile(); // Tüm listeyi yenile
			return;
		}

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "SELECT * FROM Personeldoc WHERE userssn LIKE ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, "%" + arananTC + "%");

				ResultSet rs = stmt.executeQuery();
				personelListesi.clear();

				while (rs.next()) {
					Personel personel = new Personel(rs.getInt("id"), rs.getString("username"),
							rs.getString("usersurname"), rs.getString("usergender"),
							LocalDate.parse(rs.getString("borndate")), rs.getString("userssn"),
							rs.getString("userphone"), rs.getString("usermail"));
					personelListesi.add(personel);
				}

				if (personelListesi.isEmpty()) {
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void verileriYukle() {
		poliklinikListesi.clear();

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Poliklinik")) {

			while (rs.next()) {
				Poliklinik poliklinik = new Poliklinik(rs.getInt("id"), rs.getString("poliname"),
						rs.getString("polidoctorname"), rs.getString("polidoctorssn"), rs.getString("createdate")

				);

				poliklinikListesi.add(poliklinik);
			}

			poliklinikTableView.setItems(poliklinikListesi);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
		}
	}

	@FXML
	private void verileriYenile() {
		personelListesi.clear();

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Personeldoc")) {

			while (rs.next()) {
				Personel personel = new Personel(rs.getInt("id"), rs.getString("username"), rs.getString("usersurname"),
						rs.getString("usergender"), LocalDate.parse(rs.getString("borndate")), rs.getString("userssn"),
						rs.getString("userphone"), rs.getString("usermail"));
				personelListesi.add(personel);
			}

			personelTableView.setItems(personelListesi);

		} catch (SQLException e) {
			e.printStackTrace();
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
		}
	}

	@FXML
	private void personelsil() {

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "DELETE FROM Personeldoc WHERE userssn = ?";

			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				// Set parameters
				statement.setString(1, txtTC.getText());

				int affectedRows = statement.executeUpdate();
				showAlert(Alert.AlertType.INFORMATION, "Başarılı", "personel silindi");
				if (affectedRows > 0) {
					try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							verileriYukle();
						}
					}
					clearForm();
				}
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleGuncelleAction() {
		// Seçili personeli ve TC no'yu al
		String tcNo = txtTC.getText().trim();

		if (tcNo.isEmpty()) {
			return;
		}

		// Güncellenecek personelin TC'sini belirle
		String hedefTc = tcNo;

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			// Önce personelin var olup olmadığını kontrol et
			String checkSql = "SELECT id FROM Personeldoc WHERE userssn = ?";
			try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
				checkStmt.setString(1, hedefTc);
				ResultSet rs = checkStmt.executeQuery();

				if (!rs.next()) {
					return;
				}

				int personelId = rs.getInt("id");

				// Güncelleme sorgusu
				String updateSql = "UPDATE Personeldoc SET " + "username = ?, " + "usersurname = ?, " + "borndate = ?, "
						+ "usergender = ?, " + "userphone = ?, " + "usermail = ?, " + "userpassword = ?, "
						+ "sgkcikis = ? " + "WHERE id = ?";

				try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
					// Formdaki değerleri al
					String ad = txtAd.getText().trim();
					String soyad = txtSoyad.getText().trim();
					String dogumTarihi = dateDogumTarihi.getValue().toString();
					String cinsiyet = txtCinsiyet.getText().trim();
					String telefon = txtTelefon.getText().trim();
					String eposta = txtEposta.getText().trim();
					String parola = txtParola.getText().trim();
					String sskcikis = personelPaneli_sgk_CisikTarihi.getText().trim();

					// Parametreleri set et
					updateStmt.setString(1, ad);
					updateStmt.setString(2, soyad);
					updateStmt.setString(3, dogumTarihi);
					updateStmt.setString(4, cinsiyet);
					updateStmt.setString(5, telefon);
					updateStmt.setString(6, eposta);
					updateStmt.setString(7, parola);
					updateStmt.setString(8, sskcikis);
					updateStmt.setInt(9, personelId);

					int affectedRows = updateStmt.executeUpdate();

					if (affectedRows > 0) {
						verileriYenile(); // Tabloyu güncelle
						clearForm();// Formu sıfırla
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void polisil() {

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "DELETE FROM Poliklinik WHERE poliname = ?";

			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				// Set parameters
				statement.setString(1, PoliklinikIslemler_poliklinikAdi.getText().trim());

				int affectedRows = statement.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Poliklinik silindi");
							verileriYukle();
						}
					}
					clearForm();
				}
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleDoktorEkle() {
		Poliklinik seciliPoliklinik = poliklinikTableView.getSelectionModel().getSelectedItem();
		String doktorAdi = txtDoktorAdi.getText().trim();
		String doktorSSN = txtDoktorSSN.getText().trim();

		if (seciliPoliklinik == null) {
			return;
		}

		if (doktorAdi.isEmpty() || doktorSSN.isEmpty()) {
			return;
		}

		try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "UPDATE Poliklinik SET polidoctorname = ?, polidoctorssn = ? WHERE poliname = ?";

			try (PreparedStatement stmt = conn.prepareStatement(sql)) {
				stmt.setString(1, doktorAdi);
				stmt.setString(2, doktorSSN);
				stmt.setString(3, seciliPoliklinik.getPoliklinikAdi());

				int affectedRows = stmt.executeUpdate();

				if (affectedRows > 0) {
					verileriYukle(); // Tabloyu yenile
					txtDoktorAdi.clear();
					txtDoktorSSN.clear();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@FXML
	private void polikaydet() {

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "INSERT INTO Poliklinik (poliname) " + "VALUES (?)";

			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				// Set parameters
				statement.setString(1, PoliklinikIslemler_poliklinikAdi.getText().trim());

				int affectedRows = statement.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							showAlert(Alert.AlertType.INFORMATION, "Başarılı", "Poliklinik Oluşturuldu");
							verileriYukle();
						}
					}
					clearForm();
				}
			}
		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Veritabanı Hatası",
					"Hata kodu: " + e.getErrorCode() + "\nMesaj: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@FXML
	private void handleKaydetAction() {
		if (!validateInputs()) {
			return;
		}

		try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
			String sql = "INSERT INTO Personeldoc (username, usersurname, borndate, usergender, userssn, userphone, usermail, userpassword, userpicture) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

			try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
				// Set parameters
				statement.setString(1, txtAd.getText().trim());
				statement.setString(2, txtSoyad.getText().trim());
				statement.setString(3, dateDogumTarihi.getValue().format(DateTimeFormatter.ISO_DATE));
				statement.setString(4, txtCinsiyet.getText().trim());
				statement.setString(5, txtTC.getText().trim());
				statement.setString(6, txtTelefon.getText().trim());
				statement.setString(7, txtEposta.getText().trim());
				statement.setString(8, txtParola.getText());
				statement.setString(9, resimDosyaYolu);

				int affectedRows = statement.executeUpdate();

				if (affectedRows > 0) {
					try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
						if (generatedKeys.next()) {
							showAlert(Alert.AlertType.INFORMATION, "Başarılı",
									"Personel kaydı oluşturuldu!\nPersonel ID: " + generatedKeys.getInt(1));
							verileriYenile();
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

		if (txtSoyad.getText().trim().isEmpty()) {
			errors.append("- Soyad alanı boş olamaz\n");
		}

		if (txtTC.getText().trim().length() != 11) {
			errors.append("- TC kimlik numarası 11 haneli olmalıdır\n");
		}

		if (txtTelefon.getText().trim().length() != 10) {
			errors.append("- Telefon numarası 10 haneli olmalıdır\n");
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
		txtSoyad.clear();
		txtCinsiyet.clear();
		dateDogumTarihi.setValue(null);
		txtEposta.clear();
		txtTelefon.clear();
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