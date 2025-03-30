package reproductordemusica;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import java.io.File;

public class ReproductorDeMusicaController {

    private MediaPlayer mediaPlayer;
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();
    private NodoCancion actual;
    private boolean enPausa = false;

    @FXML
    private Label labelCancion;
    @FXML
    private Slider sliderVolumen;
    @FXML
    private Slider sliderProgreso;
    @FXML
    private Label tiempoTranscurrido;
    @FXML
    private Label tiempoRestante;

    @FXML
    public void initialize() {
        // Agregar canciones
        listaReproduccion.agregarCancion("Believer", "Imagine Dragons", "Canciones/Believer.mp3");
        listaReproduccion.agregarCancion("Demons", "Imagine Dragons", "Canciones/Demons.mp3");
        listaReproduccion.agregarCancion("Radioactive", "Imagine Dragons", "Canciones/Radioactive.mp3");

        // Configurar el slider de volumen
        sliderVolumen.setMin(0);
        sliderVolumen.setMax(100);
        sliderVolumen.setValue(50);
        sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100);
            }
        });

        // Configurar el slider de progreso
        sliderProgreso.setMin(0);
        sliderProgreso.setValue(0);
        sliderProgreso.setDisable(true);
        sliderProgreso.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(sliderProgreso.getValue()));
            }
        });

        // Configurar efectos visuales iniciales
        configurarEfectosVisuales();
    }

    @FXML
    public void Play(ActionEvent event) {
        if (actual == null) actual = listaReproduccion.obtenerCancion(0);
        if (actual != null) {
            if (mediaPlayer != null && enPausa) {
                mediaPlayer.play();
                enPausa = false;
                actualizarEtiqueta();
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
                    actualizarTiempo(newTime, mediaPlayer.getMedia().getDuration());
                });

                mediaPlayer.setOnEndOfMedia(() -> sliderProgreso.setValue(sliderProgreso.getMax()));

                mediaPlayer.play();
                enPausa = false;
            }
        }
    }

    @FXML
    public void Pausa(ActionEvent event) {
        if (mediaPlayer != null) {
            if (enPausa) {
                mediaPlayer.play();
                enPausa = false;
                actualizarEtiqueta();
            } else {
                mediaPlayer.pause();
                enPausa = true;
                labelCancion.setText("Pausado: " + actual.nombre + " - " + actual.artista);
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

    private void actualizarEtiqueta() {
        if (labelCancion != null && actual != null && mediaPlayer != null) {
            labelCancion.setText("🎵 Reproduciendo: " + actual.nombre + " - " + actual.artista);
        }
    }

    private void actualizarTiempo(Duration tiempoActual, Duration duracionTotal) {
        if (tiempoTranscurrido != null && tiempoRestante != null) {
            int segundosTranscurridos = (int) tiempoActual.toSeconds();
            int segundosRestantes = (int) duracionTotal.toSeconds() - segundosTranscurridos;

            tiempoTranscurrido.setText(String.format("%02d:%02d", segundosTranscurridos / 60, segundosTranscurridos % 60));
            tiempoRestante.setText(String.format("-%02d:%02d", segundosRestantes / 60, segundosRestantes % 60));
        }
    }

    private void configurarEfectosVisuales() {
        DropShadow sombra = new DropShadow(10, Color.GRAY);
        labelCancion.setEffect(sombra);

        Timeline parpadeo = new Timeline(
            new KeyFrame(Duration.seconds(0.5), e -> labelCancion.setTextFill(Color.YELLOW)),
            new KeyFrame(Duration.seconds(1), e -> labelCancion.setTextFill(Color.WHITE))
        );
        parpadeo.setCycleCount(Timeline.INDEFINITE);
        parpadeo.play();
    }

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

