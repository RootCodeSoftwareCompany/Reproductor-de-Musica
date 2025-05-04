package reproductordemusica;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;




import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private TextField ageField;
    @FXML private CheckBox chkRock, chkPop, chkJazz, chkClasica, chkReggaeton, chkElectronica, chkTrapLatino;
    @FXML private Label messageLabel;
    @FXML private Label lblRequisitos;

    @FXML
    private void handleRegister(ActionEvent event) {
        String usuario = usernameField.getText().trim();
        String contraseña = passwordField.getText().trim();
        String confirmarContraseña = confirmPasswordField.getText();
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

        String hash = UserAuth.hashSHA256(contraseña);

        List<String> preferencias = new ArrayList<>();
        if (chkRock.isSelected()) preferencias.add("Rock");
        if (chkPop.isSelected()) preferencias.add("Pop");
        if (chkJazz.isSelected()) preferencias.add("Jazz");
        if (chkClasica.isSelected()) preferencias.add("Clásica");
        if (chkReggaeton.isSelected()) preferencias.add("Reggaetón");
        if (chkElectronica.isSelected()) preferencias.add("Electrónica");
        if (chkTrapLatino.isSelected()) preferencias.add("Trap Latino");

        String linea = usuario + ":" + hash + ":" + edad + ":" + String.join(",", preferencias);

        try (FileWriter writer = new FileWriter("users.txt", true)) {
            writer.write(linea + "\n");
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText("Usuario registrado correctamente.");

            clearFields(); // Limpia los campos

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
                    messageLabel.setText("Error al cargar la pantalla de login.");
                }
            });
            pause.play();

        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error al guardar el usuario.");
        }
    }
    
    @FXML
    private void initialize() {
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

    
    private boolean esContrasenaSegura(String contrasena) {
        if (contrasena.length() < 8) return false;
        boolean tieneLetra = contrasena.matches(".*[a-z].*");
        boolean tieneNumero = contrasena.matches(".*[0-9].*");
        boolean tieneSimbolo = contrasena.matches(".*[!@#$%^&*(),.?\":{}|<>].*");
        return tieneLetra && tieneNumero && tieneSimbolo;
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
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
        } catch (IOException e) {
            e.printStackTrace();
            messageLabel.setText("Error al volver al login.");
        }
    }
}

