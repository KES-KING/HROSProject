package application;

import javafx.beans.property.*;
import java.time.LocalDate;

public class Personel {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty ad = new SimpleStringProperty();
    private final StringProperty soyad = new SimpleStringProperty();
    private final StringProperty cinsiyet = new SimpleStringProperty();
    private final ObjectProperty<LocalDate> dogumTarihi = new SimpleObjectProperty<>();
    private final StringProperty tcNo = new SimpleStringProperty();
    private final StringProperty telefon = new SimpleStringProperty();
    private final StringProperty eposta = new SimpleStringProperty();

    public Personel(int id, String ad, String soyad, String cinsiyet, 
                   LocalDate dogumTarihi, String tcNo, String telefon, String eposta) {
        this.id.set(id);
        this.ad.set(ad);
        this.soyad.set(soyad);
        this.cinsiyet.set(cinsiyet);
        this.dogumTarihi.set(dogumTarihi);
        this.tcNo.set(tcNo);
        this.telefon.set(telefon);
        this.eposta.set(eposta);
    }

    // Property Getter'ları
    public StringProperty adProperty() { return ad; }
    public StringProperty soyadProperty() { return soyad; }
    public StringProperty cinsiyetProperty() { return cinsiyet; }
    public IntegerProperty yasProperty() { 
        return new SimpleIntegerProperty(
            LocalDate.now().getYear() - dogumTarihi.get().getYear()
        ); 
    }
    public StringProperty tcNoProperty() { return tcNo; }
    public StringProperty telefonProperty() { return telefon; }
    public StringProperty epostaProperty() { return eposta; }
}