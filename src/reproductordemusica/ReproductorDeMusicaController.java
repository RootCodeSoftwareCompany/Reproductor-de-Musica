package reproductordemusica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class ReproductorDeMusicaController {

    private MediaPlayer mediaPlayer;
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();
    private NodoCancion actual;

    @FXML
    private Label labelCancion; // Etiqueta para mostrar la información de la canción

    @FXML
    public void initialize() {
        // Agregar canciones a la lista de reproducción
        listaReproduccion.agregarCancion("Believer", "Canciones/Believer.mp3");
        listaReproduccion.agregarCancion("Demons", "Canciones/Demons.mp3");
        listaReproduccion.agregarCancion("Radioactive", "Canciones/Radioactive.mp3");
    }

    @FXML
    public void Play(ActionEvent event) {
        if (actual == null) actual = listaReproduccion.obtenerCancion(0);
        if (actual != null) {
            Pausa(event);
            Media media = new Media(new File(actual.ruta).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            actualizarEtiqueta();
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
            mediaPlayer.stop();
            labelCancion.setText("Pausado: " + actual.nombre);
            System.out.println("Reproducción pausada.");
        }
    }

    @FXML
    public void SubirVolumen(ActionEvent event) {
        if (mediaPlayer != null) {
            double nuevoVolumen = Math.min(mediaPlayer.getVolume() + 0.1, 1.0);
            mediaPlayer.setVolume(nuevoVolumen);
            System.out.println("Volumen: " + (int) (nuevoVolumen * 100) + "%");
        }
    }

    @FXML
    public void BajarVolumen(ActionEvent event) {
        if (mediaPlayer != null) {
            double nuevoVolumen = Math.max(mediaPlayer.getVolume() - 0.1, 0.0);
            mediaPlayer.setVolume(nuevoVolumen);
            System.out.println("Volumen: " + (int) (nuevoVolumen * 100) + "%");
        }
    }

    // Método para actualizar la etiqueta con la información de la canción
    private void actualizarEtiqueta() {
        if (labelCancion != null && actual != null) {
            labelCancion.setText("Reproduciendo: " + actual.nombre);
        }
    }

    // Clases para la lista de reproducción
    class NodoCancion {
        String nombre;
        String ruta;
        NodoCancion anterior, siguiente;

        public NodoCancion(String nombre, String ruta) {
            this.nombre = nombre;
            this.ruta = ruta;
            this.anterior = null;
            this.siguiente = null;
        }
    }

    class ListaDobleCircularReproduccion {
        private NodoCancion cabeza;

        public void agregarCancion(String nombre, String ruta) {
            NodoCancion nueva = new NodoCancion(nombre, ruta);
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