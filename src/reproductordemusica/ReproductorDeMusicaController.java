package reproductordemusica;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class ReproductorDeMusicaController {

    private MediaPlayer mediaPlayer;
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();
    private NodoCancion actual;

    @FXML
    public void initialize() {
        // Agregar canciones a la lista de reproducción
        listaReproduccion.agregarCancion("Believer","Canciones/Believer.mp3");
        listaReproduccion.agregarCancion("Demons","Canciones/Demons.mp3");
        listaReproduccion.agregarCancion("Radioactive","Canciones/Radioactive.mp3");
    }

    @FXML
    public void Play(ActionEvent event) throws Exception {
        if (actual == null) actual = listaReproduccion.obtenerCancion(0); 
        if (actual != null) {
            Pausa(event);  
            Media media = new Media(new File(actual.ruta).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            System.out.println("Reproduciendo: " + actual.nombre);
        }
    }

    @FXML
    public void Siguiente(ActionEvent event)throws Exception {
        if (actual != null) {
            actual = actual.siguiente;
            Play(event); 
        }
    }

    @FXML
    public void Anterior(ActionEvent event)throws Exception {
        if (actual != null) {
            actual = actual.anterior;
            Play(event);  
        }
    }

    @FXML
    public void Pausa(ActionEvent event)throws Exception {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            System.out.println("Reproducción pausada.");
        }
    }


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