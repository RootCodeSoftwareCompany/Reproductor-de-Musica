import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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

class ListaDobleReproduccion {
    private NodoCancion cabeza, cola, actual;
    private AdvancedPlayer player;
    private Thread playThread;
    
    public void agregarCancion(String nombre, String ruta) {
        NodoCancion nueva = new NodoCancion(nombre, ruta);
        if (cabeza == null) {
            cabeza = cola = nueva;
        } else {
            cola.siguiente = nueva;
            nueva.anterior = cola;
            cola = nueva;
        }
    }
    
    public void mostrarLista() {
        NodoCancion temp = cabeza;
        while (temp != null) {
            System.out.println("🎵 " + temp.nombre);
            temp = temp.siguiente;
        }
    }
    
    public void reproducir() {
        if (actual == null) actual = cabeza;
        if (actual != null) {
            try {
                FileInputStream fileInputStream = new FileInputStream(actual.ruta);
                player = new AdvancedPlayer(fileInputStream);
                playThread = new Thread(() -> {
                    try {
                        player.play();
                    } catch (JavaLayerException e) {
                        e.printStackTrace();
                    }
                });
                playThread.start();
                System.out.println("Reproduciendo: " + actual.nombre);
            } catch (FileNotFoundException | JavaLayerException e) {
                e.printStackTrace();
            }
        }
    }
    
    public void siguiente() {
        if (actual != null && actual.siguiente != null) {
            detener();
            actual = actual.siguiente;
            reproducir();
        }
    }
    
    public void anterior() {
        if (actual != null && actual.anterior != null) {
            detener();
            actual = actual.anterior;
            reproducir();
        }
    }
    
    public void detener() {
        if (player != null) {
            player.close();
            playThread.interrupt();
        }
    }
}

public class ReproductorMusica extends JFrame {
    private final ListaDobleReproduccion listaReproduccion;
    
    public ReproductorMusica() {
        listaReproduccion = new ListaDobleReproduccion();
        listaReproduccion.agregarCancion("Believer", "Canciones/Believer.mp3");
        listaReproduccion.agregarCancion("Demons", "Canciones/Demons.mp3");
        listaReproduccion.agregarCancion("Radioactive", "Canciones/Radioactive.mp3");
        
        setTitle("Reproductor de Música");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());
        
        JButton playBtn = new JButton("▶ Play");
        JButton stopBtn = new JButton("■ Stop");
        JButton prevBtn = new JButton("⏮ Anterior");
        JButton nextBtn = new JButton("⏭ Siguiente");
        
        
        playBtn.addActionListener(e -> listaReproduccion.reproducir());
        stopBtn.addActionListener(e -> listaReproduccion.detener());
        prevBtn.addActionListener(e -> listaReproduccion.anterior());
        nextBtn.addActionListener(e -> listaReproduccion.siguiente());
        
        add(playBtn);
        add(stopBtn);
        add(prevBtn);
        add(nextBtn);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReproductorMusica().setVisible(true));
    }
}

