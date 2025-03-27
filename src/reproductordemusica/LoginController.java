package reproductordemusica;


import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LoginController implements Initializable {
    
    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }  

    @FXML
    public void Ingresar(ActionEvent event) throws Exception {
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReproductorDeMusica.fxml"));
        Parent root = loader.load();

        // Obtener la ventana actual y cambiar la escena
        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Reproductor de Música");
        stage.show();
    }
}

