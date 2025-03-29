package application;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class doktorYonetimClass {
	public void cıkısekran(ActionEvent event) {
        try {
            // Yeni formu yükle
	        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("giriş paneli.fxml"));
	        Parent root = fxmlLoader.load();
	
	        // Yeni pencereyi aç
	        Stage stage = new Stage();
	        stage.setTitle("Giriş Ekranı");
	        stage.setScene(new Scene(root));
	        stage.show();
	        
	        ((Node) event.getSource()).getScene().getWindow().hide();
	
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}
