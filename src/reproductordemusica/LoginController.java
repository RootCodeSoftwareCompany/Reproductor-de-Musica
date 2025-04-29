package reproductordemusica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.*;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasena;
    @FXML private TextField txtNuevoUsuario;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;
    @FXML private TextField txtEdad;
    @FXML private CheckBox chkRock, chkPop, chkClasica, chkJazz, chkReggaeton;

    private final String USUARIOS_FILE = "usuarios.txt";

    @FXML
    private void handleLogin(ActionEvent event) {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Completa todos los campos.");
            return;
        }

        if (validarCredenciales(usuario, contrasena)) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "¡Bienvenido " + usuario + "!");
            cargarVentana("CarpetaMusica.fxml", event);
        } else {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void handleRegistro(ActionEvent event) {
        String nuevoUsuario = txtNuevoUsuario.getText();
        String nuevaContrasena = txtNuevaContrasena.getText();
        String confirmarContrasena = txtConfirmarContrasena.getText();
        String edadTexto = txtEdad.getText();

        if (nuevoUsuario.isEmpty() || nuevaContrasena.isEmpty() || confirmarContrasena.isEmpty() || edadTexto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Completa todos los campos de registro.");
            return;
        }

        if (!nuevaContrasena.equals(confirmarContrasena)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadTexto);
            if (edad < 10 || edad > 100) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Edad inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Edad inválida.");
            return;
        }

        if (usuarioExiste(nuevoUsuario)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "El nombre de usuario ya existe.");
            return;
        }

        Set<String> preferencias = new HashSet<>();
        if (chkRock.isSelected()) preferencias.add("rock");
        if (chkPop.isSelected()) preferencias.add("pop");
        if (chkClasica.isSelected()) preferencias.add("clasica");
        if (chkJazz.isSelected()) preferencias.add("jazz");
        if (chkReggaeton.isSelected()) preferencias.add("reggaeton");

        if (preferencias.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Selecciona al menos una preferencia musical.");
            return;
        }

        guardarUsuario(nuevoUsuario, nuevaContrasena, edad, preferencias);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Registro exitoso", "Usuario registrado correctamente.");

        limpiarCamposRegistro();
    }

    private void guardarUsuario(String usuario, String contrasena, int edad, Set<String> preferencias) {
        try (FileWriter fw = new FileWriter(USUARIOS_FILE, true)) {
            String hash = hash(contrasena);
            fw.write(usuario + ":" + hash + ":" + edad + ":" + String.join(",", preferencias) + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean usuarioExiste(String usuario) {
        File file = new File(USUARIOS_FILE);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length > 0 && partes[0].equals(usuario)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean validarCredenciales(String usuario, String contrasena) {
        File file = new File(USUARIOS_FILE);
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            String hashIntento = hash(contrasena);
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 2 && partes[0].equals(usuario) && partes[1].equals(hashIntento)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private String hash(String contrasena) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(contrasena.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarVentana(String fxml, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Reproductor de Música");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar la ventana principal.");
        }
    }

    private void limpiarCamposRegistro() {
        txtNuevoUsuario.clear();
        txtNuevaContrasena.clear();
        txtConfirmarContrasena.clear();
        txtEdad.clear();
        chkRock.setSelected(false);
        chkPop.setSelected(false);
        chkClasica.setSelected(false);
        chkJazz.setSelected(false);
        chkReggaeton.setSelected(false);
    }
}

