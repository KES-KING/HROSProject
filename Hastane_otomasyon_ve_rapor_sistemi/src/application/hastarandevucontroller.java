package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import java.sql.*;

public class hastarandevucontroller {

	@FXML
	private ComboBox<String> cbPersoneller;
	@FXML
	private ComboBox<String> cbPoliklinikler;
	@FXML
	private ComboBox<String> timeComboBox;
	// Veritabanı bağlantı bilgileri
	private static final String DB_URL = "jdbc:mysql://localhost:3306/HrosSQL";
	private static final String DB_USER = "javauser";
	private static final String DB_PASSWORD = "javauser";

	@FXML
	public void initialize() {
		personelComboboxDoldur();
		poliklinikComboboxDoldur();

		ObservableList<String> times = FXCollections.observableArrayList();

		// Tüm saat ve dakikaları ekle (15 dakikalık aralıklarla)
		for (int hour = 0; hour < 24; hour++) {
			for (int minute = 0; minute < 60; minute += 15) {
				String time = String.format("%02d:%02d", hour, minute);
				times.add(time);
			}
		}

		timeComboBox.setItems(times);
		timeComboBox.setValue("00:00"); // Varsayılan değer
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
}
