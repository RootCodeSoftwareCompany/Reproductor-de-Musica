package reproductordemusica;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PerfilUsuarioController implements Initializable {

    @FXML private Label lblNombreUsuario;
    @FXML private TextField txtEdad;
    @FXML private CheckBox chkRock, chkPop, chkJazz, chkClasica, chkReggaeton, chkElectronica, chkTrapLatino;
    @FXML private PasswordField txtContrasenaActual, txtContrasenaNueva, txtConfirmarContrasena, txtContrasenaEliminar;
    @FXML private Label lblRequisitos;
    @FXML private Button btnActualizarDatos, btnCambiarContrasena, btnEliminarCuenta, btnVolver;
    
    private UserAuth.Usuario usuarioActual;
    private ArrayList<Cancion> listaCanciones; // Para mantener las canciones al volver
    private UserAuth userAuth;
    private static final String USERS_FILE = "users.txt";
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        userAuth = new UserAuth();
        
        // Configurar validación de contraseña
        txtContrasenaNueva.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                lblRequisitos.setVisible(true);
                if (esContrasenaSegura(newVal)) {
                    lblRequisitos.setStyle("-fx-text-fill: lightgreen; -fx-font-size: 12;");
                } else {
                    lblRequisitos.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 12;");
                }
            } else {
                lblRequisitos.setVisible(false);
            }
        });
    }
    
    // Método para recibir el usuario actual
    public void setUsuarioActual(UserAuth.Usuario usuario, ArrayList<Cancion> canciones) {
        this.usuarioActual = usuario;
        this.listaCanciones = canciones;
        
        if (usuario != null) {
            // Cargar los datos del usuario en los campos
            lblNombreUsuario.setText(usuario.getNombre());
            txtEdad.setText(String.valueOf(usuario.getEdad()));
            
            // Procesar preferencias musicales
            String preferenciasCadena = usuario.getPreferencias();
            if (preferenciasCadena != null && !preferenciasCadena.isEmpty() && !preferenciasCadena.equals("Ninguna")) {
                String[] preferencias = preferenciasCadena.split(",");
                
                // Limpiar todas las selecciones
                chkRock.setSelected(false);
                chkPop.setSelected(false);
                chkJazz.setSelected(false);
                chkClasica.setSelected(false);
                chkReggaeton.setSelected(false);
                chkElectronica.setSelected(false);
                chkTrapLatino.setSelected(false);
                
                // Seleccionar las preferencias guardadas
                for (String preferencia : preferencias) {
                    switch (preferencia.trim()) {
                        case "Rock":
                            chkRock.setSelected(true);
                            break;
                        case "Pop":
                            chkPop.setSelected(true);
                            break;
                        case "Jazz":
                            chkJazz.setSelected(true);
                            break;
                        case "Clásica":
                            chkClasica.setSelected(true);
                            break;
                        case "Reggaetón":
                            chkReggaeton.setSelected(true);
                            break;
                        case "Electrónica":
                            chkElectronica.setSelected(true);
                            break;
                        case "Trap Latino":
                            chkTrapLatino.setSelected(true);
                            break;
                    }
                }
            }
        }
    }
    
    @FXML
    public void actualizarDatos(ActionEvent event) {
        try {
            // Validar la edad
            int edad;
            try {
                edad = Integer.parseInt(txtEdad.getText().trim());
                if (edad <= 0) {
                    mostrarAlerta("Error", "La edad debe ser un número positivo.", Alert.AlertType.ERROR);
                    return;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "La edad debe ser un número válido.", Alert.AlertType.ERROR);
                return;
            }
            
            // Obtener preferencias musicales seleccionadas
            List<String> preferencias = new ArrayList<>();
            if (chkRock.isSelected()) preferencias.add("Rock");
            if (chkPop.isSelected()) preferencias.add("Pop");
            if (chkJazz.isSelected()) preferencias.add("Jazz");
            if (chkClasica.isSelected()) preferencias.add("Clásica");
            if (chkReggaeton.isSelected()) preferencias.add("Reggaetón");
            if (chkElectronica.isSelected()) preferencias.add("Electrónica");
            if (chkTrapLatino.isSelected()) preferencias.add("Trap Latino");
            
            // Formatear las preferencias
            String preferenciasCadena = String.join(",", preferencias);
            if (preferenciasCadena.isEmpty()) preferenciasCadena = "Ninguna";
            
            // Actualizar en archivo
            actualizarUsuarioEnArchivo(usuarioActual.getNombre(), null, edad, preferenciasCadena, null);
            
            // Crear un nuevo objeto Usuario con los datos actualizados
            usuarioActual = new UserAuth.Usuario(usuarioActual.getNombre(), edad, preferenciasCadena);
            
            mostrarAlerta("Éxito", "Datos de usuario actualizados correctamente.", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron actualizar los datos: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    public void cambiarContrasena(ActionEvent event) {
        String contrasenaActual = txtContrasenaActual.getText().trim();
        String contrasenaNueva = txtContrasenaNueva.getText().trim();
        String confirmarContrasena = txtConfirmarContrasena.getText().trim();
        
        // Validar que las contraseñas no estén vacías
        if (contrasenaActual.isEmpty() || contrasenaNueva.isEmpty() || confirmarContrasena.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos de contraseña son obligatorios.", Alert.AlertType.ERROR);
            return;
        }
        
        // Verificar que la contraseña actual sea correcta
        if (!userAuth.validarCredenciales(usuarioActual.getNombre(), contrasenaActual)) {
            mostrarAlerta("Error", "La contraseña actual es incorrecta.", Alert.AlertType.ERROR);
            return;
        }
        
        // Validar que las nuevas contraseñas coincidan
        if (!contrasenaNueva.equals(confirmarContrasena)) {
            mostrarAlerta("Error", "Las nuevas contraseñas no coinciden.", Alert.AlertType.ERROR);
            return;
        }
        
        // Validar que la nueva contraseña sea segura
        if (!esContrasenaSegura(contrasenaNueva)) {
            mostrarAlerta("Error", "La nueva contraseña no cumple con los requisitos de seguridad.", Alert.AlertType.ERROR);
            return;
        }
        
        try {
            // Generar hash de la nueva contraseña
            String nuevoHash = UserAuth.hashSHA256(contrasenaNueva);
            
            // Actualizar en archivo
            actualizarUsuarioEnArchivo(usuarioActual.getNombre(), nuevoHash, -1, null, null);
            
            mostrarAlerta("Éxito", "Contraseña actualizada correctamente.", Alert.AlertType.INFORMATION);
            limpiarCamposContrasena();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo actualizar la contraseña: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    @FXML
    public void eliminarCuenta(ActionEvent event) {
        String contrasena = txtContrasenaEliminar.getText().trim();
        
        if (contrasena.isEmpty()) {
            mostrarAlerta("Error", "Debes ingresar tu contraseña para confirmar la eliminación.", Alert.AlertType.ERROR);
            return;
        }
        
        if (!userAuth.validarCredenciales(usuarioActual.getNombre(), contrasena)) {
            mostrarAlerta("Error", "Contraseña incorrecta. No se puede eliminar la cuenta.", Alert.AlertType.ERROR);
            return;
        }
        
        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Estás seguro de que deseas eliminar tu cuenta?");
        confirmacion.setContentText("Esta acción no se puede deshacer. Perderás todos tus datos de usuario.");
        
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    if (UserAuth.eliminarUsuario(usuarioActual.getNombre())) {
                        mostrarAlerta("Cuenta eliminada", "Tu cuenta ha sido eliminada correctamente.", Alert.AlertType.INFORMATION);
                        
                        // Volver a la pantalla de login
                        irALogin(event);
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar la cuenta.", Alert.AlertType.ERROR);
                    }
                } catch (Exception e) {
                    mostrarAlerta("Error", "Error al eliminar la cuenta: " + e.getMessage(), Alert.AlertType.ERROR);
                    e.printStackTrace();
                }
            }
        });
    }
    
    @FXML
    public void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReproductorDeMusica.fxml"));
            Parent root = loader.load();

            // Obtener el controlador y pasarle el usuario actual
            ReproductorDeMusicaController controller = loader.getController();
            controller.setUsuarioActual(usuarioActual);
            
            // Pasar la lista de canciones si existe
            if (listaCanciones != null && !listaCanciones.isEmpty()) {
                controller.setListaCanciones(listaCanciones, true);
            }

            Stage stage = (Stage) btnVolver.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Reproductor de Música");
            stage.show();

            // Efecto de transición
            FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo volver al reproductor: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void irALogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((event.getSource() instanceof Button) ? 
                                   ((Button) event.getSource()).getScene().getWindow() : 
                                   btnVolver.getScene().getWindow());
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

            // Efecto de transición
            FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
            
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cargar la pantalla de login: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
    
    private void actualizarUsuarioEnArchivo(String nombreUsuario, String nuevoHash, int nuevaEdad, 
                                           String nuevasPreferencias, String nuevaPista) throws IOException {
        File archivoUsuarios = new File(USERS_FILE);
        if (!archivoUsuarios.exists()) {
            throw new IOException("El archivo de usuarios no existe.");
        }
        
        List<String> lineas = new ArrayList<>();
        boolean usuarioEncontrado = false;
        
        // Leer el archivo y almacenar todas las líneas
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                
                // Si es el usuario que buscamos, lo actualizamos
                if (partes.length >= 5 && partes[0].trim().equalsIgnoreCase(nombreUsuario.trim())) {
                    usuarioEncontrado = true;
                    
                    // Obtener los valores actuales
                    String hash = partes[1].trim();
                    String edad = partes[2].trim();
                    String preferencias = partes[3].trim();
                    String pista = partes[4].trim();
                    
                    // Actualizar solo los campos que se proporcionaron
                    if (nuevoHash != null) hash = nuevoHash;
                    if (nuevaEdad > 0) edad = String.valueOf(nuevaEdad);
                    if (nuevasPreferencias != null) preferencias = nuevasPreferencias;
                    if (nuevaPista != null) pista = nuevaPista;
                    
                    // Reconstruir la línea
                    linea = nombreUsuario + ":" + hash + ":" + edad + ":" + preferencias + ":" + pista;
                }
                
                lineas.add(linea);
            }
        }
        
        if (!usuarioEncontrado) {
            throw new IOException("Usuario no encontrado en el archivo.");
        }
        
        // Escribir todas las líneas de vuelta al archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (String linea : lineas) {
                writer.write(linea);
                writer.newLine();
            }
        }
    }
    
    private boolean esContrasenaSegura(String contrasena) {
        return contrasena.length() >= 8 &&
               contrasena.matches(".*[a-z].*") &&
               contrasena.matches(".*[0-9].*") &&
               contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }
    
    private void limpiarCamposContrasena() {
        txtContrasenaActual.clear();
        txtContrasenaNueva.clear();
        txtConfirmarContrasena.clear();
    }
    
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}