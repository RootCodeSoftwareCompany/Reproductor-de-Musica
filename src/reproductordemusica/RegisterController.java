package reproductordemusica;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.scene.Node;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField passwordHintField; // Nuevo campo para la pista de contraseña
    @FXML private TextField ageField;
    @FXML private CheckBox chkRock, chkPop, chkJazz, chkClasica, chkReggaeton, chkElectronica, chkTrapLatino;
    @FXML private Label messageLabel;
    @FXML private Label lblRequisitos;
    @FXML private Label lblInstruccionDobleClic;
    @FXML private TableView<UserAuth.Usuario> tablaUsuarios;
    @FXML private TableColumn<UserAuth.Usuario, String> colUsuario;
    @FXML private TableColumn<UserAuth.Usuario, Integer> colEdad;
    @FXML private TableColumn<UserAuth.Usuario, String> colPreferencias;

    private ObservableList<UserAuth.Usuario> usuariosList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // TableView
        if (tablaUsuarios != null) {
            colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            colEdad.setCellValueFactory(new PropertyValueFactory<>("edad"));
            colPreferencias.setCellValueFactory(new PropertyValueFactory<>("preferencias"));
            tablaUsuarios.setItems(usuariosList);

            tablaUsuarios.setOnMouseClicked((MouseEvent event) -> {
                if (event.getClickCount() == 2) {
                    UserAuth.Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
                    if (usuarioSeleccionado != null) {
                        iniciarSesionConUsuario(usuarioSeleccionado, event);
                    }
                }
            });

            cargarUsuarios();
        }

        // Requisitos visuales de contraseña
        if (passwordField != null && lblRequisitos != null) {
            passwordField.setOnMouseClicked(e -> lblRequisitos.setVisible(true));
            passwordField.textProperty().addListener((obs, oldText, newText) -> {
                if (newText.isEmpty()) {
                    lblRequisitos.setVisible(false);
                } else {
                    lblRequisitos.setVisible(true);
                    if (esContrasenaSegura(newText)) {
                        lblRequisitos.setStyle("-fx-text-fill: green; -fx-font-size: 11;");
                    } else {
                        lblRequisitos.setStyle("-fx-text-fill: red; -fx-font-size: 11;");
                    }
                }
            });
        }
    }

    private void cargarUsuarios() {
        usuariosList.clear();
        try {
            List<UserAuth.Usuario> usuarios = UserAuth.obtenerUsuarios();
            usuariosList.addAll(usuarios);
        } catch (Exception e) {
            if (messageLabel != null) {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("Error al cargar la lista de usuarios.");
            }
        }
    }

    private void iniciarSesionConUsuario(UserAuth.Usuario usuario, MouseEvent event) {
        try {
            if (UserAuth.existeUsuario(usuario.getNombre())) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("CarpetaMusica.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText("El usuario no existe.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText("Error al cargar la pantalla.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String usuario = usernameField.getText().trim();
        String contraseña = passwordField.getText().trim();
        String confirmarContraseña = confirmPasswordField.getText().trim();
        String pistaContraseña = passwordHintField != null ? passwordHintField.getText().trim() : "";
        String edadText = ageField.getText().trim();

        if (usuario.isEmpty() || contraseña.isEmpty() || confirmarContraseña.isEmpty() || edadText.isEmpty()) {
            messageLabel.setText("Todos los campos son obligatorios.");
            return;
        }

        int edad;
        try {
            edad = Integer.parseInt(edadText);
            if (edad <= 0) {
                messageLabel.setText("Edad inválida.");
                return;
            }
        } catch (NumberFormatException e) {
            messageLabel.setText("Edad debe ser un número.");
            return;
        }

        if (!contraseña.equals(confirmarContraseña)) {
            messageLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        if (UserAuth.existeUsuario(usuario)) {
            messageLabel.setText("El usuario ya existe.");
            return;
        }

        if (pistaContraseña.isEmpty() && !mostrarConfirmacionPistaPerdida()) {
            if (passwordHintField != null) passwordHintField.requestFocus();
            return;
        }

        String hash = UserAuth.hashSHA256(contraseña);

        List<String> preferencias = new ArrayList<>();
        if (chkRock != null && chkRock.isSelected()) preferencias.add("Rock");
        if (chkPop != null && chkPop.isSelected()) preferencias.add("Pop");
        if (chkJazz != null && chkJazz.isSelected()) preferencias.add("Jazz");
        if (chkClasica != null && chkClasica.isSelected()) preferencias.add("Clásica");
        if (chkReggaeton != null && chkReggaeton.isSelected()) preferencias.add("Reggaetón");
        if (chkElectronica != null && chkElectronica.isSelected()) preferencias.add("Electrónica");
        if (chkTrapLatino != null && chkTrapLatino.isSelected()) preferencias.add("Trap Latino");

        String preferenciasStr = String.join(",", preferencias);
        String linea = usuario + ":" + hash + ":" + edad + ":" + preferenciasStr + ":" + pistaContraseña;

        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write(linea + "\n");
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Usuario registrado correctamente.");

            usuariosList.add(new UserAuth.Usuario(usuario, edad, preferenciasStr.isEmpty() ? "Ninguna" : preferenciasStr));
            clearFields();

            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException ex) {
                    ex.printStackTrace();
                    messageLabel.setText("Error al cargar el login.");
                }
            });
            pause.play();
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error al guardar el usuario.");
        }
    }

    private boolean mostrarConfirmacionPistaPerdida() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Pista no proporcionada");
        alert.setHeaderText("No proporcionaste una pista para recordar tu contraseña.");
        alert.setContentText("¿Deseas continuar sin pista?");
        ButtonType btnSi = new ButtonType("Sí, continuar");
        ButtonType btnNo = new ButtonType("No, agregaré una pista");
        alert.getButtonTypes().setAll(btnSi, btnNo);
        return alert.showAndWait().orElse(btnNo) == btnSi;
    }


    @FXML
    private void handleEliminarUsuario(ActionEvent event) {
        if (tablaUsuarios == null) return;
        UserAuth.Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            messageLabel.setText("Selecciona un usuario para eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setContentText("¿Deseas eliminar a \"" + usuarioSeleccionado.getNombre() + "\"?");
        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    if (UserAuth.eliminarUsuario(usuarioSeleccionado.getNombre())) {
                        usuariosList.remove(usuarioSeleccionado);
                        messageLabel.setStyle("-fx-text-fill: green;");
                        messageLabel.setText("Usuario eliminado.");
                    } else {
                        messageLabel.setText("Error al eliminar.");
                    }
                } catch (Exception e) {
                    messageLabel.setText("Error al eliminar: " + e.getMessage());
                }
            }
        });
    }

    private boolean esContrasenaSegura(String contrasena) {
        return contrasena.length() >= 8 &&
                contrasena.matches(".*[a-z].*") &&
                contrasena.matches(".*[0-9].*") &&
                contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();

        if (passwordHintField != null) passwordHintField.clear();

        ageField.clear();
        chkRock.setSelected(false);
        chkPop.setSelected(false);
        chkJazz.setSelected(false);
        chkClasica.setSelected(false);
        chkReggaeton.setSelected(false);
        chkElectronica.setSelected(false);
        chkTrapLatino.setSelected(false);
    }

    @FXML
    private void handleBackToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
            FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error al volver al login.");
        }
    }
}
