package reproducir;

import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

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

public class Nodo {
    private NodoCancion cabeza, actual;
    private MediaPlayer mediaPlayer;
    private boolean isPaused; // Agregar esta variable para rastrear el estado de pausa

    public Nodo() {
        this.cabeza = null;
        this.actual = null;
        this.mediaPlayer = null;
        this.isPaused = false; // Inicializar el estado de pausa
    }

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

    public void mostrarLista() {
        if (cabeza == null) return;
        NodoCancion temp = cabeza;
        do {
            System.out.println("🎵 " + temp.nombre);
            temp = temp.siguiente;
        } while (temp != cabeza);
    }

    public void reproducir() {
    if (actual == null) actual = cabeza; // Si no hay canción actual, empezar desde la cabeza
    if (actual != null) {
        if (mediaPlayer != null) {
            mediaPlayer.stop(); // Detener la canción actual si está reproduciendo
        }
        Media media = new Media(new File(actual.ruta).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
        isPaused = false; // Cambiar el estado a no pausado
        System.out.println("Reproduciendo: " + actual.nombre);
    }
}

    public void siguiente() {
        if (actual != null) {
            actual = actual.siguiente;
            reproducir(); // Reproducir la siguiente canción
        }
    }

    public void anterior() {
        if (actual != null) {
            actual = actual.anterior;
            reproducir(); // Reproducir la canción anterior
        }
    }

    public void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            isPaused = false; // Cambiar el estado a no pausado al detener
        }
    }

    public void pausar() {
        if (mediaPlayer != null) {
            mediaPlayer.pause(); // Pausar la canción actual
            isPaused = true; // Cambiar el estado a pausado
        }
    }

    public void reanudar() {
        if (mediaPlayer != null) {
            mediaPlayer.play(); // Reanudar la canción
            isPaused = false; // Cambiar el estado a no pausado
        }
    }
    
    public boolean isPaused() {
        return isPaused; // Método para verificar si está en pausa
    }
}