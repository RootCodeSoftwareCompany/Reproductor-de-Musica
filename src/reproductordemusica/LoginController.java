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
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.AnchorPane;

public class LoginController implements Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;
    
    @FXML
    private Button btnMostrarContrasena;
    
    @FXML
    private Hyperlink lnkOlvideContrasena;
    
    // Necesitamos crear un TextField para mostrar la contraseña
    private TextField txtContrasenaVisible;

    private Stage stage;
    
    private UserAuth userAuth;
    
    private boolean passwordVisible = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userAuth = new UserAuth();
        
        // Crear el TextField para mostrar la contraseña
        setupPasswordToggle();
    }
    
    private void setupPasswordToggle() {
        // Crear un TextField que reemplazará al PasswordField cuando se quiera mostrar la contraseña
        txtContrasenaVisible = new TextField();
        txtContrasenaVisible.setLayoutX(txtContrasena.getLayoutX());
        txtContrasenaVisible.setLayoutY(txtContrasena.getLayoutY());
        txtContrasenaVisible.setPrefWidth(txtContrasena.getPrefWidth());
        txtContrasenaVisible.setPrefHeight(txtContrasena.getPrefHeight());
        txtContrasenaVisible.setStyle(txtContrasena.getStyle());
        txtContrasenaVisible.setVisible(false);
        
        // Sincronizar ambos campos
        txtContrasena.textProperty().bindBidirectional(txtContrasenaVisible.textProperty());
        
        // Agregar el TextField al layout
        AnchorPane parent = (AnchorPane) txtContrasena.getParent();
        parent.getChildren().add(txtContrasenaVisible);
    }

    @FXML
    public void Ingresar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        // Obtener la contraseña del campo visible en este momento
        String contrasena = passwordVisible ? txtContrasenaVisible.getText().trim() : txtContrasena.getText().trim();

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
                mostrarAlerta("Error", "No se pudo cargar la ventana principal.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Acceso denegado", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
        
            txtUsuario.clear();
            clearPassword();
        }
    }    
    
    @FXML
    public void mostrarPistaContrasena(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        
        // Verificar que el usuario haya ingresado su nombre de usuario
        if (usuario.isEmpty()) {
            mostrarAlerta("Información necesaria", "Por favor, ingresa tu nombre de usuario para ver la pista.", Alert.AlertType.INFORMATION);
            txtUsuario.requestFocus();
            return;
        }
        
        // Obtener la pista de contraseña
        String pista = userAuth.obtenerPistaContrasena(usuario);
        
        if (pista != null && !pista.isEmpty()) {
            mostrarAlerta("Pista para recordar contraseña", "La pista para tu contraseña es: " + pista, Alert.AlertType.INFORMATION);
        } else {
            mostrarAlerta("Pista no disponible", "No se encontró ninguna pista para el usuario especificado o el usuario no existe.", Alert.AlertType.WARNING);
        }
    }
    
    @FXML
    public void togglePasswordVisibility(ActionEvent event) {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            // Mostrar contraseña
            txtContrasena.setVisible(false);
            txtContrasenaVisible.setVisible(true);
            btnMostrarContrasena.setText("🙈");
        } else {
            // Ocultar contraseña
            txtContrasenaVisible.setVisible(false);
            txtContrasena.setVisible(true);
            btnMostrarContrasena.setText("👁️");
        }
        
        // Mover el foco al campo visible
        if (passwordVisible) {
            txtContrasenaVisible.requestFocus();
        } else {
            txtContrasena.requestFocus();
        }
    }
    
    private void clearPassword() {
        txtContrasena.clear();
        txtContrasenaVisible.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
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
                mostrarAlerta("Error", "No se pudo cargar la ventana de registro.", Alert.AlertType.ERROR);
            }
        });
        pause.play();
    }
}