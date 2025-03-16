package application;

import javafx.beans.property.*;

public class Poliklinik {
    private final IntegerProperty id;
    private final StringProperty isim;
    private final StringProperty aciklama;

    public Poliklinik(int id, String isim, String aciklama) {
        this.id = new SimpleIntegerProperty(id);
        this.isim = new SimpleStringProperty(isim);
        this.aciklama = new SimpleStringProperty(aciklama);
    }

    // Getter ve Property metodları
    public int getId() { return id.get(); }
    public String getIsim() { return isim.get(); }
    public String getAciklama() { return aciklama.get(); }

    public IntegerProperty idProperty() { return id; }
    public StringProperty isimProperty() { return isim; }
    public StringProperty aciklamaProperty() { return aciklama; }
}