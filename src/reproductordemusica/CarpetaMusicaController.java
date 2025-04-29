package reproductordemusica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import reproductordemusica.Cancion;
import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

public class CarpetaMusicaController implements Initializable {

    private Stage stage;
    private ArrayList<Cancion> listaCanciones = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Inicialización si es necesaria
    }

    @FXML
    public void seleccionarCarpeta(ActionEvent event) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar carpeta de música");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int resultado = fileChooser.showOpenDialog(null);

        if (resultado == JFileChooser.APPROVE_OPTION) {
            File carpetaSeleccionada = fileChooser.getSelectedFile();
            File[] archivos = carpetaSeleccionada.listFiles((dir, nombre) -> nombre.toLowerCase().endsWith(".mp3"));

            if (archivos != null && archivos.length > 0) {
                listaCanciones.clear();

                for (File archivo : archivos) {
                    try {
                        Mp3File mp3file = new Mp3File(archivo);
                        String nombre = archivo.getName().replace(".mp3", "").trim();
                        String ruta = archivo.getAbsolutePath();
                        String artista = "";
                        String genero = "";
                        String album = "";
                        String anio = "";

                        if (mp3file.hasId3v2Tag()) {
                            ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                            artista = id3v2Tag.getArtist();
                            genero = id3v2Tag.getGenreDescription();
                            album = id3v2Tag.getAlbum();
                            anio = id3v2Tag.getYear();
                        } else if (mp3file.hasId3v1Tag()) {
                            ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                            artista = id3v1Tag.getArtist();
                            genero = id3v1Tag.getGenreDescription();
                            album = id3v1Tag.getAlbum();
                            anio = id3v1Tag.getYear();
                        }

                        listaCanciones.add(new Cancion(nombre, ruta, artista, genero, album, anio));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                mostrarAlerta(AlertType.INFORMATION, "Canciones cargadas",
                        "Se han encontrado " + listaCanciones.size() + " canciones.");
            } else {
                mostrarAlerta(AlertType.WARNING, "Sin archivos MP3",
                        "No se encontraron archivos MP3 en la carpeta seleccionada.");
            }
        } else {
            System.out.println("No se seleccionó ninguna carpeta.");
        }
    }

    @FXML
    public void Siguiente(ActionEvent event) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReproductorDeMusica.fxml"));
        Parent root = loader.load();

        // Pasar la lista al siguiente controlador
        ReproductorDeMusicaController controller = loader.getController();
        controller.setListaCanciones(listaCanciones, true);

        stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle("Reproductor de Música");
        stage.show();
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
