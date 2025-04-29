package reproductordemusica;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.util.*;

public class ReproductorDeMusicaController {

    private MediaPlayer mediaPlayer;
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();
    private NodoCancion actual;
    private boolean enPausa = false;
    private boolean modoAleatorio = false;

    private final ObservableList<Cancion> cancionesList = FXCollections.observableArrayList();
    private final Map<String, ArrayList<Cancion>> listasDeReproduccion = new HashMap<>();
    private final ObservableList<String> historialReproduccion = FXCollections.observableArrayList();

    @FXML private TableView<Cancion> tablaCanciones;
    @FXML private ComboBox<String> comboBoxListas;
    @FXML private TableColumn<Cancion, String> colNombre;
    @FXML private TableColumn<Cancion, String> colArtista;
    @FXML private TableColumn<Cancion, String> colGenero;
    @FXML private TableColumn<Cancion, String> colAlbum;
    @FXML private TableColumn<Cancion, String> colAnio;
    @FXML private Label labelCancion;
    @FXML private Slider sliderVolumen;
    @FXML private Slider sliderProgreso;
    @FXML private Label tiempoTranscurrido;
    @FXML private Label tiempoRestante;
    @FXML private ToggleButton botonAleatorio;
    @FXML private Label labelArtista;
    @FXML private Label labelGenero;
    @FXML private Button btnCrearLista;
    @FXML private Button btnAgregarCancion;
    @FXML private Button btnEliminarCancion;
    @FXML private Button btnEliminarLista;
 

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colArtista.setCellValueFactory(cellData -> cellData.getValue().artistaProperty());
        colGenero.setCellValueFactory(cellData -> cellData.getValue().generoProperty());
        colAlbum.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        colAnio.setCellValueFactory(cellData -> cellData.getValue().anioProperty());

        tablaCanciones.setItems(cancionesList);

        tablaCanciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actual = listaReproduccion.buscarPorNombre(newVal.getNombre());
                reproducirCancionSeleccionada();
            }
        });

        sliderVolumen.setMin(0);
        sliderVolumen.setMax(100);
        sliderVolumen.setValue(50);
        sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(newVal.doubleValue() / 100);
            }
        });

        sliderProgreso.setMin(0);
        sliderProgreso.setValue(0);
        sliderProgreso.setDisable(true);
        sliderProgreso.setOnMouseReleased(event -> {
            if (mediaPlayer != null) {
                mediaPlayer.seek(Duration.seconds(sliderProgreso.getValue()));
            }
        });

        botonAleatorio.setOnAction(e -> {
            modoAleatorio = botonAleatorio.isSelected();
            cambiarColor();
        });

        comboBoxListas.setOnAction(e -> {
            String nombreSeleccionado = comboBoxListas.getValue();
            ArrayList<Cancion> cancionesSeleccionadas = listasDeReproduccion.get(nombreSeleccionado);
            if (cancionesSeleccionadas != null) {
                setListaCanciones(cancionesSeleccionadas, false);
            }
            boolean esPrincipal = "Lista Principal".equals(nombreSeleccionado);
            btnAgregarCancion.setVisible(!esPrincipal);
            btnEliminarCancion.setVisible(!esPrincipal);
        });
        
        btnEliminarCancion.setVisible(false);
        btnAgregarCancion.setVisible(false);
        cambiarColor();
        configurarEfectosVisuales();
    }

    public void setListaCanciones(ArrayList<Cancion> canciones, boolean esPrincipal) {
        listaReproduccion.vaciar();
        cancionesList.clear();

        for (Cancion cancion : canciones) {
            listaReproduccion.agregarCancion(cancion.getNombre(), cancion.getRuta());
            cancionesList.add(cancion);
        }

        if (esPrincipal) {
            listasDeReproduccion.put("Lista Principal", canciones);
            if (!comboBoxListas.getItems().contains("Lista Principal")) {
                comboBoxListas.getItems().add("Lista Principal");
                comboBoxListas.getSelectionModel().selectFirst();
            }
        }
    }

    @FXML
    public void crearNuevaLista() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Crear Lista de Reproducción");
        dialog.setHeaderText("Nueva Lista de Reproducción");
        dialog.setContentText("Nombre de la lista:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(nombreLista -> {
            if (!nombreLista.trim().isEmpty() && !listasDeReproduccion.containsKey(nombreLista)) {
                listasDeReproduccion.put(nombreLista, new ArrayList<>());
                comboBoxListas.getItems().add(nombreLista);
                comboBoxListas.getSelectionModel().select(nombreLista);
            } else {
                mostrarAlerta("Nombre inválido o ya existe.");
            }
        });
    }
    @FXML
    public void eliminarLista() {
        String listaSeleccionada = comboBoxListas.getValue();

        if ("Lista Principal".equals(listaSeleccionada)) {
            mostrarAlerta("La Lista Principal no se puede eliminar.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Seguro que deseas eliminar la lista \"" + listaSeleccionada + "\"?");
        Optional<ButtonType> resultado = confirmacion.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            listasDeReproduccion.remove(listaSeleccionada);
            comboBoxListas.getItems().remove(listaSeleccionada);
            comboBoxListas.getSelectionModel().select("Lista Principal");
            setListaCanciones(listasDeReproduccion.get("Lista Principal"), false);

            btnAgregarCancion.setVisible(false);
            btnEliminarCancion.setVisible(false);
          
        }
    }
    @FXML
    public void agregarCancionesALista() {
        String listaSeleccionada = comboBoxListas.getValue();
        if (listaSeleccionada == null || "Lista Principal".equals(listaSeleccionada)) {
            mostrarAlerta("Selecciona una lista personalizada para agregar canciones.");
            return;
        }

        List<String> opciones = new ArrayList<>();
        for (Cancion c : listasDeReproduccion.get("Lista Principal")) {
            opciones.add(c.getNombre());
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(opciones.get(0), opciones);
        dialog.setTitle("Agregar Canción");
        dialog.setHeaderText("Selecciona una canción para agregar");
        dialog.setContentText("Canción:");

        Optional<String> resultado = dialog.showAndWait();
        resultado.ifPresent(nombreSeleccionado -> {
            Cancion cancionSeleccionada = null;
            for (Cancion c : listasDeReproduccion.get("Lista Principal")) {
                if (c.getNombre().equals(nombreSeleccionado)) {
                    cancionSeleccionada = c;
                    break;
                }
            }

            if (cancionSeleccionada != null) {
                ArrayList<Cancion> lista = listasDeReproduccion.get(listaSeleccionada);
                lista.add(cancionSeleccionada);
                if (comboBoxListas.getValue().equals(listaSeleccionada)) {
                    setListaCanciones(lista, false);
                }
            }
        });
    }
    @FXML
    public void eliminarCancionDeLista() {
        Cancion seleccion = tablaCanciones.getSelectionModel().getSelectedItem();
        String listaSeleccionada = comboBoxListas.getValue();

        if (seleccion == null || listaSeleccionada == null || "Lista Principal".equals(listaSeleccionada)) {
            mostrarAlerta("Selecciona una canción de una lista personalizada para eliminar.");
            return;
        }

        ArrayList<Cancion> lista = listasDeReproduccion.get(listaSeleccionada);
        if (lista.removeIf(c -> c.getNombre().equals(seleccion.getNombre()))) {
            setListaCanciones(lista, false);
        } else {
            mostrarAlerta("No se pudo eliminar la canción.");
        }
    }

   
    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    private void reproducirCancionSeleccionada() {
        if (actual == null) return;
        if (mediaPlayer != null) mediaPlayer.stop();

        Media media = new Media(new File(actual.ruta).toURI().toString());
        mediaPlayer = new MediaPlayer(media);

        mediaPlayer.setOnReady(() -> {
            mediaPlayer.setVolume(sliderVolumen.getValue() / 100);
            sliderProgreso.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
            sliderProgreso.setDisable(false);
            actualizarEtiqueta();

            String artista = (String) media.getMetadata().get("artist");
            String genero = (String) media.getMetadata().get("genre");

            labelArtista.setText("👤 Artista: " + (artista != null ? artista : "Desconocido"));
            labelGenero.setText("🎶 Género: " + (genero != null ? genero : "Desconocido"));
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
            sliderProgreso.setValue(newTime.toSeconds());
            actualizarTiempo(newTime, mediaPlayer.getMedia().getDuration());
        });

        mediaPlayer.setOnEndOfMedia(() -> {
            sliderProgreso.setValue(sliderProgreso.getMax());
            actual = modoAleatorio ? seleccionarAleatoria() : actual.siguiente;
            if (actual != null) {
                tablaCanciones.getSelectionModel().select(actualToCancion(actual));
                reproducirCancionSeleccionada();
              
            }
       
        });

        mediaPlayer.play();
        enPausa = false;
       
    }

    @FXML public void Play(ActionEvent event) {
        if (actual == null) {
            Cancion seleccion = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                actual = listaReproduccion.buscarPorNombre(seleccion.getNombre());
                reproducirCancionSeleccionada();
            }
        } else if (mediaPlayer != null && enPausa) {
            mediaPlayer.play();
            enPausa = false;
            actualizarEtiqueta();
        } else if (modoAleatorio) {
            actual = seleccionarAleatoria();
            tablaCanciones.getSelectionModel().select(actualToCancion(actual));
            reproducirCancionSeleccionada();
        }
    }

    @FXML public void Pausa(ActionEvent event) {
        if (mediaPlayer != null) {
            if (enPausa) {
                mediaPlayer.play();
                enPausa = false;
                actualizarEtiqueta();
            } else {
                mediaPlayer.pause();
                enPausa = true;
                labelCancion.setText("⏸ Pausado: " + actual.nombre);
            }
        }
    }

    @FXML public void Siguiente(ActionEvent event) {
        if (actual != null) {
            actual = modoAleatorio ? seleccionarAleatoria() : actual.siguiente;
            tablaCanciones.getSelectionModel().select(actualToCancion(actual));
            reproducirCancionSeleccionada();
        }
    }

    @FXML public void Anterior(ActionEvent event) {
        if (actual != null) {
            actual = modoAleatorio ? seleccionarAleatoria() : actual.anterior;
            tablaCanciones.getSelectionModel().select(actualToCancion(actual));
            reproducirCancionSeleccionada();
        }
    }

    @FXML private void cambiarColor() {
        botonAleatorio.setStyle(botonAleatorio.isSelected() ?
                "-fx-background-color: #4CAF50; -fx-text-fill: white;" :
                "-fx-background-color: #dddddd; -fx-text-fill: black;");
    }

    private NodoCancion seleccionarAleatoria() {
        if (cancionesList.isEmpty()) return null;
        Random random = new Random();
        Cancion aleatoria = cancionesList.get(random.nextInt(cancionesList.size()));
        return listaReproduccion.buscarPorNombre(aleatoria.getNombre());
    }

    private void actualizarEtiqueta() {
        if (labelCancion != null && actual != null && mediaPlayer != null) {
            labelCancion.setText("🎵 Reproduciendo: " + actual.nombre);
        }
    }

    private void actualizarTiempo(Duration actual, Duration total) {
        if (tiempoTranscurrido == null || tiempoRestante == null) return;
        int segs = (int) actual.toSeconds();
        int totalSegs = (int) total.toSeconds();
        int restantes = totalSegs - segs;

        tiempoTranscurrido.setText(String.format("%02d:%02d", segs / 60, segs % 60));
        tiempoRestante.setText(String.format("-%02d:%02d", restantes / 60, restantes % 60));
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
        String nombre, ruta;
        NodoCancion anterior, siguiente;
        public NodoCancion(String nombre, String ruta) {
            this.nombre = nombre;
            this.ruta = ruta;
        }
    }

  class ListaDobleCircularReproduccion {
    private NodoCancion cabeza;

    public void vaciar() {
        cabeza = null;
    }

    public void agregarCancion(String nombre, String ruta) {
        NodoCancion nueva = new NodoCancion(nombre, ruta);
        if (cabeza == null) {
            cabeza = nueva;
            cabeza.siguiente = cabeza;
            cabeza.anterior = cabeza;
        } else {
            NodoCancion ultimo = cabeza.anterior;
            ultimo.siguiente = nueva;
            nueva.anterior = ultimo;
            nueva.siguiente = cabeza;
            cabeza.anterior = nueva;
        }
    }

    public NodoCancion buscarPorNombre(String nombre) {
        if (cabeza == null) return null;
        NodoCancion temp = cabeza;
        do {
            if (temp.nombre.equals(nombre)) return temp;
            temp = temp.siguiente;
        } while (temp != cabeza);
        return null;
    }
}

    private Cancion actualToCancion(NodoCancion nodo) {
        if (nodo == null) return null;
        return new Cancion(nodo.nombre, nodo.ruta, null, null, null, null);
    }
}
