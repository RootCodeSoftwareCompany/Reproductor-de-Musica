package reproductordemusica;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.layout.AnchorPane;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController implements Initializable {

    @FXML
    private ComboBox<String> comboUsuario;

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
    
    // Lista de usuarios para el autocompletado
    private ObservableList<String> listaUsuarios = FXCollections.observableArrayList();
    private FilteredList<String> usuariosFiltrados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userAuth = new UserAuth();
        
        // Crear el TextField para mostrar la contraseña
        setupPasswordToggle();
        
        // Cargar la lista de usuarios desde el archivo
        cargarUsuarios();
        
        // Configurar el ComboBox con autocompletado
        configurarComboBoxUsuarios();
        
        // Añadir un listener para manejar la selección de un usuario
        comboUsuario.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                txtContrasena.requestFocus();
            }
        });
        
        // Añadir un manejador de teclas para permitir el salto a contraseña con Enter
        comboUsuario.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                txtContrasena.requestFocus();
            }
        });
    }
    
    private void cargarUsuarios() {
        // Limpiar la lista antes de cargar
        listaUsuarios.clear();
        
        // Obtener los usuarios desde el archivo
        List<UserAuth.Usuario> usuarios = UserAuth.obtenerUsuarios();
        
        // Agregar los nombres de usuario a la lista
        for (UserAuth.Usuario usuario : usuarios) {
            listaUsuarios.add(usuario.getNombre());
        }
    }
    
    private void configurarComboBoxUsuarios() {
        // Configurar el ComboBox con la lista de usuarios
        comboUsuario.setItems(listaUsuarios);
        
        // Crear un filtrado para autocompletar al escribir
        usuariosFiltrados = new FilteredList<>(listaUsuarios, p -> true);
        
        // Añadir listener al editor del ComboBox para filtrar mientras se escribe
        comboUsuario.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            final TextField editor = comboUsuario.getEditor();
            final String text = editor.getText();
            
            // Si el texto está vacío, mostrar todos los usuarios
            if (text == null || text.isEmpty()) {
                usuariosFiltrados.setPredicate(s -> true);
                comboUsuario.show();
                return;
            }
            
            // Filtrar usuarios que coincidan con lo escrito (ignorando mayúsculas/minúsculas)
            usuariosFiltrados.setPredicate(item -> item.toLowerCase().contains(text.toLowerCase()));
            
            // Si el ComboBox no está mostrando su desplegable, mostrarlo
            if (!comboUsuario.isShowing() && !usuariosFiltrados.isEmpty()) {
                comboUsuario.show();
            }
            
            // Si no hay coincidencias, cerrar el desplegable
            if (usuariosFiltrados.isEmpty()) {
                comboUsuario.hide();
            }
        });
        
        // Necesitamos que el ComboBox use la lista filtrada
        comboUsuario.setItems(usuariosFiltrados);
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
        String usuario = comboUsuario.getValue() != null ? comboUsuario.getValue().trim() : 
                         comboUsuario.getEditor().getText().trim();
        
        // Obtener la contraseña del campo visible en este momento
        String contrasena = passwordVisible ? txtContrasenaVisible.getText().trim() : txtContrasena.getText().trim();

        if (usuario.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese un usuario", Alert.AlertType.WARNING);
            comboUsuario.requestFocus();
            return;
        }
        
        if (contrasena.isEmpty()) {
            mostrarAlerta("Error", "Por favor ingrese una contraseña", Alert.AlertType.WARNING);
            if (passwordVisible) {
                txtContrasenaVisible.requestFocus();
            } else {
                txtContrasena.requestFocus();
            }
            return;
        }

        if (userAuth.validarCredenciales(usuario, contrasena)) {
            try {
                // Obtener el usuario completo
                UserAuth.Usuario usuarioActual = UserAuth.obtenerUsuarioPorNombre(usuario);
                
                // Primero pasamos a la pantalla de selección de carpeta
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CarpetaMusica.fxml"));
                Parent root = loader.load();
                
                // Pasar el usuario al controlador
                CarpetaMusicaController controller = loader.getController();
                controller.setUsuarioActual(usuarioActual);
                
                stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Reproductor de Música");
                stage.show();
                FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo cargar la ventana principal.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Acceso denegado", "Usuario o contraseña incorrectos.", Alert.AlertType.ERROR);
        
            clearFields();
        }
    }    
    
    @FXML
    public void mostrarPistaContrasena(ActionEvent event) {
        String usuario = comboUsuario.getValue() != null ? comboUsuario.getValue() : 
                         comboUsuario.getEditor().getText().trim();
        
        // Verificar que el usuario haya ingresado su nombre de usuario
        if (usuario.isEmpty()) {
            mostrarAlerta("Información necesaria", "Por favor, ingresa tu nombre de usuario para ver la pista.", Alert.AlertType.INFORMATION);
            comboUsuario.requestFocus();
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
    
    private void clearFields() {
        comboUsuario.getEditor().clear();
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