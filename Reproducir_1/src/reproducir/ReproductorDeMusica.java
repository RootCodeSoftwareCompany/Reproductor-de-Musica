/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package reproducir;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

class NodoCancion{
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
    private NodoCancion cabeza, actual;
    private MediaPlayer mediaPlayer;

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
        if (actual == null) actual = cabeza;
        if (actual != null) {
            detener();
            Media media = new Media(new File(actual.ruta).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
            System.out.println("Reproduciendo: " + actual.nombre);
        }
    }

    public void siguiente() {
        if (actual != null) {
            actual = actual.siguiente;
            reproducir();
        }
    }

    public void anterior() {
        if (actual != null) {
            actual = actual.anterior;
            reproducir();
        }
    }

    public void detener() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}

public class ReproductorDeMusica extends Application {
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();

    @Override
    public void start(Stage primaryStage) {
        listaReproduccion.agregarCancion("Believer", "Canciones/Believer.mp3");
        listaReproduccion.agregarCancion("Demons", "Canciones/Demons.mp3");
        listaReproduccion.agregarCancion("Radioactive", "Canciones/Radioactive.mp3");

        Button playBtn = new Button("▶ Play");
        Button stopBtn = new Button("■ Stop");
        Button prevBtn = new Button("⏮ Anterior");
        Button nextBtn = new Button("⏭ Siguiente");

        playBtn.setOnAction(e -> listaReproduccion.reproducir());
        stopBtn.setOnAction(e -> listaReproduccion.detener());
        prevBtn.setOnAction(e -> listaReproduccion.anterior());
        nextBtn.setOnAction(e -> listaReproduccion.siguiente());

        HBox controls = new HBox(10, playBtn, stopBtn, prevBtn, nextBtn);
        Scene scene = new Scene(controls, 400, 100);

        primaryStage.setTitle("Reproductor de Música");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}