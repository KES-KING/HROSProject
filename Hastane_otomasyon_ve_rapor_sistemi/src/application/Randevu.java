package application;

import javafx.beans.property.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Randevu {
	private final IntegerProperty id;
	private final IntegerProperty patientId;
	private final StringProperty poliklinik;
	private final StringProperty doktor;
	private final StringProperty tarih;
	private final StringProperty saat;

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	public Randevu(int id, int patientId, String poliklinik, String doktor, String tarih, String saat) {
		this.id = new SimpleIntegerProperty(id);
		this.patientId = new SimpleIntegerProperty(patientId);
		this.poliklinik = new SimpleStringProperty(poliklinik);
		this.doktor = new SimpleStringProperty(doktor);
		this.tarih = new SimpleStringProperty(tarih);
		this.saat = new SimpleStringProperty(saat);
	}

	// Getter methods
	public int getId() {
		return id.get();
	}

	public int getPatientId() {
		return patientId.get();
	}

	public String getPoliklinik() {
		return poliklinik.get();
	}

	public String getDoktor() {
		return doktor.get();
	}

	public String getTarih() {
		return tarih.get();
	}

	public String getSaat() {
		return saat.get();
	}

	// Property methods
	public IntegerProperty idProperty() {
		return id;
	}

	public IntegerProperty patientIdProperty() {
		return patientId;
	}

	public StringProperty poliklinikProperty() {
		return poliklinik;
	}

	public StringProperty doktorProperty() {
		return doktor;
	}

	public StringProperty tarihProperty() {
		return tarih;
	}

	public StringProperty saatProperty() {
		return saat;
	}

	// Utility methods
	public LocalDate getTarihAsLocalDate() {
		try {
			return LocalDate.parse(tarih.get(), DATE_FORMATTER);
		} catch (Exception e) {
			return LocalDate.now();
		}
	}

	@Override
	public String toString() {
		return String.format("%s - %s (%s %s)", poliklinik.get(), doktor.get(), tarih.get(), saat.get());
	}
}