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
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.FieldKey;

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
            File[] archivos = carpetaSeleccionada.listFiles((dir, nombre) -> {
                String nombreLower = nombre.toLowerCase();
                return nombreLower.endsWith(".mp3") || nombreLower.endsWith(".wav") || nombreLower.endsWith(".wma");
            });

            if (archivos != null && archivos.length > 0) {
                listaCanciones.clear();

                for (File archivo : archivos) {
                    try {
                        AudioFile audioFile = AudioFileIO.read(archivo);
                        Tag tag = audioFile.getTag();
                        String nombre = tag != null && tag.getFirst(FieldKey.TITLE) != null && !tag.getFirst(FieldKey.TITLE).isEmpty() 
                                       ? tag.getFirst(FieldKey.TITLE) 
                                       : archivo.getName().replaceFirst("\\.(mp3|wav|wma)$", "");
                        String artista = tag != null ? tag.getFirst(FieldKey.ARTIST) : "Desconocido";
                        String genero = tag != null ? tag.getFirst(FieldKey.GENRE) : "Desconocido";
                        String album = tag != null ? tag.getFirst(FieldKey.ALBUM) : "Desconocido";
                        String anio = tag != null ? tag.getFirst(FieldKey.YEAR) : "Desconocido";

                        // Manejo de valores nulos o vacíos
                        if (artista == null || artista.isEmpty()) artista = "Desconocido";
                        if (genero == null || genero.isEmpty()) genero = "Desconocido";
                        if (album == null || album.isEmpty()) album = "Desconocido";
                        if (anio == null || anio.isEmpty()) anio = "Desconocido";

                        listaCanciones.add(new Cancion(nombre, archivo.getAbsolutePath(), artista, genero, album, anio));
                    } catch (Exception e) {
                        System.err.println("Error al leer metadatos de: " + archivo.getName() + " - " + e.getMessage());
                        listaCanciones.add(new Cancion(
                            archivo.getName().replaceFirst("\\.(mp3|wav|wma)$", ""),
                            archivo.getAbsolutePath(),
                            "Desconocido", "Desconocido", "Desconocido", "Desconocido"
                        ));
                    }
                }

                mostrarAlerta(AlertType.INFORMATION, "Canciones cargadas",
                        "Se han encontrado " + listaCanciones.size() + " canciones.");
            } else {
                mostrarAlerta(AlertType.WARNING, "Sin archivos de audio",
                        "No se encontraron archivos MP3, WAV o WMA en la carpeta seleccionada.");
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