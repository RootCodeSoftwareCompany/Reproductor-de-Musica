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
import javax.swing.SwingUtilities;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        Reproductor.aniadir("audio", "/home/gos/Descargas/audio.mp3");

        Reproductor.aniadir("Demons", "/home/gos/Descargas/Novi.mp3");

        Reproductor.aniadir("Radioactive", "Canciones/Radioactive.mp3");
        
         SwingUtilities.invokeLater(() -> { //interfaz sencilla
            new interfaz().setVisible(true);
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
   
}