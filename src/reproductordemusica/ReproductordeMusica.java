package reproductordemusica;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
public class ReproductordeMusica extends Application {
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Reproductor de Musica");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void cambiarEscena(String fxml) throws Exception {
        FXMLLoader loader = new FXMLLoader(ReproductordeMusica.class.getResource(fxml));
        Parent root = loader.load();
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    } 
}
