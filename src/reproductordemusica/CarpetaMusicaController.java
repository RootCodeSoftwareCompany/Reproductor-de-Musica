package reproductordemusica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class CarpetaMusicaController implements Initializable {

    private Stage stage;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }

    @FXML
    public void seleccionarCarpeta(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar carpeta de música");

        File carpetaSeleccionada = directoryChooser.showDialog(stage);

        if (carpetaSeleccionada != null) {
            File[] archivos = carpetaSeleccionada.listFiles((dir, nombre) -> nombre.toLowerCase().endsWith(".mp3"));

            if (archivos != null && archivos.length > 0) {
                File archivoSalida = new File("canciones.txt");

                try (BufferedWriter writer = new BufferedWriter(new FileWriter(archivoSalida))) {
                    for (File archivo : archivos) {
                        String nombreCancion = archivo.getName().replace(".mp3", "").trim();
                        String rutaCancion = archivo.getAbsolutePath();
                        writer.write(nombreCancion + " | " + rutaCancion);
                        writer.newLine();
                    }

                    Alert alerta = new Alert(AlertType.INFORMATION);
                    alerta.setTitle("Operación exitosa");
                    alerta.setHeaderText(null);
                    alerta.setContentText("Las canciones se han guardado correctamente en: " + archivoSalida.getAbsolutePath());
                    alerta.showAndWait();

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ReproductorDeMusica.fxml"));
                    Parent root = loader.load();
                    stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Reproductor de Música");
                    stage.show();

                } catch (IOException e) {
                    Alert alerta = new Alert(AlertType.ERROR);
                    alerta.setTitle("Error al guardar el archivo");
                    alerta.setHeaderText(null);
                    alerta.setContentText("Ocurrió un error al guardar el archivo de texto: " + e.getMessage());
                    alerta.showAndWait();
                }
            } else {
                Alert alerta = new Alert(AlertType.WARNING);
                alerta.setTitle("Sin archivos MP3");
                alerta.setHeaderText(null);
                alerta.setContentText("No se encontraron archivos MP3 en la carpeta seleccionada.");
                alerta.showAndWait();
            }
        } else {
            System.out.println("No se seleccionó ninguna carpeta.");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
}



