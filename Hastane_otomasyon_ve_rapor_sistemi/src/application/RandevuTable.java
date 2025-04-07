package application;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class RandevuTable {
	private final StringProperty patientName;
	private final StringProperty patientSsn;
	private final StringProperty poliklinik;
	private final StringProperty doktor;
	private final StringProperty tarih;
	private final StringProperty saat;

	public RandevuTable(String patientName, String patientSsn, String poliklinik, String doktor, String tarih,
			String saat) {
		this.patientName = new SimpleStringProperty(patientName);
		this.patientSsn = new SimpleStringProperty(patientSsn);
		this.poliklinik = new SimpleStringProperty(poliklinik);
		this.doktor = new SimpleStringProperty(doktor);
		this.tarih = new SimpleStringProperty(tarih);
		this.saat = new SimpleStringProperty(saat);
	}

	// Getter methods
	public String getPatientName() {
		return patientName.get();
	}

	public String getPatientSsn() {
		return patientSsn.get();
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
	public StringProperty patientNameProperty() {
		return patientName;
	}

	public StringProperty patientSsnProperty() {
		return patientSsn;
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
}