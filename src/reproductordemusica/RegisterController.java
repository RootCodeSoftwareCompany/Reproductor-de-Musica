package reproductordemusica;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.util.Duration;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.animation.FadeTransition;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField passwordHintField; // Campo para la pista de contraseña
    @FXML private TextField ageField;
    @FXML private CheckBox chkRock, chkPop, chkJazz, chkClasica, chkReggaeton, chkElectronica, chkTrapLatino;
    @FXML private Label messageLabel;
    @FXML private Label lblRequisitos;

    @FXML
    private void initialize() {
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

        if (!esContrasenaSegura(contraseña)) {
            messageLabel.setText("La contraseña no cumple con los requisitos de seguridad.");
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