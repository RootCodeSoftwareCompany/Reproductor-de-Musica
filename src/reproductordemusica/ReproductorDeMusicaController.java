package reproductordemusica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import java.io.File;

public class ReproductorDeMusicaController {

    private MediaPlayer mediaPlayer;
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();
    private NodoCancion actual;
    private boolean enPausa = false; // Para controlar el estado de pausa

    @FXML
    private Label labelCancion; // Etiqueta para mostrar información de la canción
    @FXML
    private Slider sliderVolumen; // Slider para el volumen
    @FXML
    private Slider sliderProgreso; // Slider para el progreso de la canción

    @FXML
    public void initialize() {
        // Agregar canciones
        listaReproduccion.agregarCancion("Believer", "Imagine Dragons", "Canciones/Believer.mp3");
        listaReproduccion.agregarCancion("Demons", "Imagine Dragons", "Canciones/Demons.mp3");
        listaReproduccion.agregarCancion("Radioactive", "Imagine Dragons", "Canciones/Radioactive.mp3");

        // Configurar el slider de volumen
        sliderVolumen.setMin(0);
        sliderVolumen.setMax(100);
        sliderVolumen.setValue(50); // Volumen inicial en 50%
        sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100);
            }
        });

        // Configurar el slider de progreso
        sliderProgreso.setMin(0);
        sliderProgreso.setValue(0);
        sliderProgreso.setDisable(true); // Se habilita cuando hay una canción en reproducción
        sliderProgreso.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(sliderProgreso.getValue()));
            }
        });
    }

    @FXML
    public void Play(ActionEvent event) {
        if (actual == null) actual = listaReproduccion.obtenerCancion(0);
        if (actual != null) {
            if (mediaPlayer != null && enPausa) {
                mediaPlayer.play();
                enPausa = false;
                actualizarEtiqueta();
                System.out.println("Reproducción reanudada.");
            } else {
                if (mediaPlayer != null) mediaPlayer.stop();
                Media media = new Media(new File(actual.ruta).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnReady(() -> {
                    mediaPlayer.setVolume(sliderVolumen.getValue() / 100);
                    sliderProgreso.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                    sliderProgreso.setValue(0);
                    sliderProgreso.setDisable(false);
                    actualizarEtiqueta();
                });

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    sliderProgreso.setValue(newTime.toSeconds());
                });

                mediaPlayer.setOnEndOfMedia(() -> {
                    sliderProgreso.setValue(sliderProgreso.getMax()); // Ajustar el slider al final
                });

                mediaPlayer.play();
                enPausa = false;
            }
        }
    }

    @FXML
    public void Siguiente(ActionEvent event) {
        if (actual != null) {
            actual = actual.siguiente;
            Play(event);
        }
    }

    @FXML
    public void Anterior(ActionEvent event) {
        if (actual != null) {
            actual = actual.anterior;
            Play(event);
        }
    }

    @FXML
    public void Pausa(ActionEvent event) {
        if (mediaPlayer != null) {
            if (enPausa) {
                mediaPlayer.play();
                enPausa = false;
                actualizarEtiqueta();
                System.out.println("Reproducción reanudada.");
            } else {
                mediaPlayer.pause();
                enPausa = true;
                labelCancion.setText("Pausado: " + actual.nombre + " - " + actual.artista);
                System.out.println("Reproducción pausada.");
            }
        }
    }

    // Método para actualizar la etiqueta con la información de la canción
    private void actualizarEtiqueta() {
        if (labelCancion != null && actual != null && mediaPlayer != null) {
            Duration duracion = mediaPlayer.getMedia().getDuration();
            String duracionTexto = String.format("%02d:%02d",
                    (int) duracion.toMinutes(),
                    (int) duracion.toSeconds() % 60
            );
            labelCancion.setText("Reproduciendo: " + actual.nombre + " - " + actual.artista + " [" + duracionTexto + "]");
        }
    }

    // Clases para la lista de reproducción
    class NodoCancion {
        String nombre;
        String artista;
        String ruta;
        NodoCancion anterior, siguiente;

        public NodoCancion(String nombre, String artista, String ruta) {
            this.nombre = nombre;
            this.artista = artista;
            this.ruta = ruta;
            this.anterior = null;
            this.siguiente = null;
        }
    }

    class ListaDobleCircularReproduccion {
        private NodoCancion cabeza;

        public void agregarCancion(String nombre, String artista, String ruta) {
            NodoCancion nueva = new NodoCancion(nombre, artista, ruta);
            if (cabeza == null) {
                cabeza = nueva;
                cabeza.siguiente = cabeza;
                cabeza.anterior = cabeza;
            } else {
                NodoCancion cola = cabeza.anterior;
                cola.siguiente = nueva;
                nueva.anterior = cola;
                nueva.siguiente = cabeza;
                cabeza.anterior = nueva;
            }
        }

        public NodoCancion obtenerCancion(int index) {
            NodoCancion temp = cabeza;
            for (int i = 0; i < index; i++) {
                temp = temp.siguiente;
            }
            return temp;
        }
    }
}

