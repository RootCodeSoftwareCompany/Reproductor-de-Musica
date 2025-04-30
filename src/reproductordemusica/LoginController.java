package reproductordemusica;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    private Stage stage;
    
    private UserAuth userAuth;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userAuth = new UserAuth();
    }

    @FXML
    public void Ingresar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String contrasena = txtContrasena.getText().trim();

        
        if (userAuth.validarCredenciales(usuario, contrasena)) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CarpetaMusica.fxml"));
                Parent root = loader.load();
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Reproductor de Música");
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la ventana principal.");
            }
        } else {
            mostrarAlerta("Acceso denegado", "Usuario o contraseña incorrectos.");
        
            txtUsuario.clear();
            txtContrasena.clear();
        }
    }    
        

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    @FXML
    private void openRegister(ActionEvent event) {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
        pause.setOnFinished(e -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Register.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setTitle("Registro de Usuario");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception ex) {
                ex.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la ventana de registro.");
            }
        });
        pause.play();
    }
}

