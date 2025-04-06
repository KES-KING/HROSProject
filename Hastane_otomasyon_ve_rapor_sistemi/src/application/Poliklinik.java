package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Poliklinik {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty poliklinikAdi;
    private final SimpleStringProperty doktorAdi;
    private final SimpleStringProperty doktorTC;
    private final SimpleStringProperty olusturma;

    public Poliklinik(int id, String poliklinikAdi, String doktorAdi, String doktorTC, String olusturma) {
        this.id = new SimpleIntegerProperty(id);
        this.poliklinikAdi = new SimpleStringProperty(poliklinikAdi);
        this.doktorAdi = new SimpleStringProperty(doktorAdi);
        this.doktorTC = new SimpleStringProperty(doktorTC);
        this.olusturma = new SimpleStringProperty(olusturma);
    }

    // Getter ve Property Metodları
    public int getId() { return id.get(); }
    public String getPoliklinikAdi() { return poliklinikAdi.get(); }
    public String getDoktorAdi() { return doktorAdi.get(); }
    public String getDoktorTC() { return doktorTC.get(); }
    public String getolusturma() { return olusturma.get(); }

    public SimpleStringProperty poliklinikAdiProperty() { return poliklinikAdi; }
    public SimpleStringProperty doktorAdiProperty() { return doktorAdi; }
    public SimpleStringProperty doktorTCProperty() { return doktorTC; }
    public SimpleStringProperty olusturmaProperty() { return olusturma; }
}