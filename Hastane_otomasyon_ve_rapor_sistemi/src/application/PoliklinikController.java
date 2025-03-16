package application;

import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;

//Controller sınıfının en üstüne bu importları ekleyin
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PoliklinikController extends Application {

	@FXML private TextField txtIsim; // FXML'deki fx:id ile aynı
    @FXML private TextField txtAciklama; // FXML'deki fx:id ile aynı
    @FXML private Button btnEkle;
    @FXML private Button btnSil;
    @FXML private TableView<Poliklinik> tableView;
    @FXML private TableColumn<Poliklinik, Integer> colId;
    @FXML private TableColumn<Poliklinik, String> colIsim;
    @FXML private TableColumn<Poliklinik, String> colAciklama;
    @FXML private ComboBox<Poliklinik> comboPoliklinikler;
    private ObservableList<Poliklinik> poliklinikData = FXCollections.observableArrayList();

    private ObservableList<Poliklinik> poliklinikList = FXCollections.observableArrayList();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("Poliklinik Yönetimi");
        primaryStage.setScene(new Scene(root, 600, 400));
        primaryStage.show();
        loadPoliklinikler();
    }

    @FXML
    public void initialize() {
        // Tabloyu başlat
    	 colId.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
         colIsim.setCellValueFactory(cellData -> cellData.getValue().isimProperty());
         colAciklama.setCellValueFactory(cellData -> cellData.getValue().aciklamaProperty());
         
        tableView.setItems(poliklinikList);
        loadPolikliniklerToComboBox();
        // Verileri yükle
        loadPoliklinikler();
        setupComboBox();
    }

    
    private void loadPoliklinikler() {
    	poliklinikData.clear();
        String sql = "SELECT id, isim, aciklama FROM poliklinikler";
        
        try (Connection conn = DatabaseHelper.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                poliklinikData.add(new Poliklinik(
                    rs.getInt("id"),
                    rs.getString("isim"),
                    rs.getString("aciklama")
                ));
            }
            
            // Tabloya verileri yükle
            tableView.setItems(poliklinikData);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void loadPolikliniklerToComboBox() {
        poliklinikData.clear();
        String sql = "SELECT * FROM poliklinikler";
        
      
            // Combobox'a verileri yükle
            comboPoliklinikler.setItems(poliklinikData);
            
            // Combobox'ta görüntülenecek değeri belirle
            comboPoliklinikler.setCellFactory(param -> new ListCell<Poliklinik>() {
                @Override
                protected void updateItem(Poliklinik item, boolean empty) {
                    super.updateItem(item, empty); // Bu satırı asla unutmayın!
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getIsim()); // Sadece ismi göster
                    }
                }
            });

            comboPoliklinikler.setButtonCell(new ListCell<Poliklinik>() {
                @Override
                protected void updateItem(Poliklinik item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getIsim());
                    }
                }
            });
    }
    @FXML
    private void handleYenile() {
        loadPoliklinikler();
        setupComboBox();
    }
    @FXML
    private void handleEkle() {
        String isim = txtIsim.getText().trim();
        String aciklama = txtAciklama.getText().trim();
        
        if (!isim.isEmpty() && !aciklama.isEmpty()) {
            String sql = "INSERT INTO poliklinikler (isim, aciklama) VALUES (?, ?)";
            
            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, isim);
                stmt.setString(2, aciklama);
                stmt.executeUpdate();
                
                // TextField'ları temizle
                txtIsim.clear();
                txtAciklama.clear();
                
                // Tabloyu yenile
                loadPoliklinikler();
                loadPolikliniklerToComboBox();
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        loadPoliklinikler();
        setupComboBox();
    }
    private void setupComboBox() {
        comboPoliklinikler.setItems(poliklinikData);
        comboPoliklinikler.setCellFactory(param -> new ListCell<Poliklinik>() {
            @Override
            protected void updateItem(Poliklinik item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getIsim());
            }
        });
    }

    @FXML
    private void handleSil() {
        Poliklinik secilen = comboPoliklinikler.getSelectionModel().getSelectedItem();
        
        if(secilen == null) {
            showAlert("Uyarı", "Lütfen silmek için bir poliklinik seçin!");
            return;
        }

        if(confirmDelete()) {
            String sql = "DELETE FROM poliklinikler WHERE id = ?";
            
            try (Connection conn = DatabaseHelper.getInstance().getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, secilen.getId());
                int affectedRows = stmt.executeUpdate();
                
                if(affectedRows > 0) {
                    poliklinikData.remove(secilen);
                    showAlert("Başarılı", "Poliklinik başarıyla silindi!");
                }
                
            } catch (SQLException e) {
                showAlert("Hata", "Silme işlemi başarısız: " + e.getMessage());
            }
        }
    }

    private boolean confirmDelete() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Silme Onayı");
        alert.setHeaderText(comboPoliklinikler.getValue() + " silinecek");
        alert.setContentText("Bu işlem geri alınamaz. Emin misiniz?");
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}