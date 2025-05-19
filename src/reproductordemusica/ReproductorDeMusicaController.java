package reproductordemusica;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javafx.animation.FadeTransition;
import javafx.stage.Stage;

public class ReproductorDeMusicaController {

    private MediaPlayer mediaPlayer;
    private Clip clip;
    private final ListaDobleCircularReproduccion listaReproduccion = new ListaDobleCircularReproduccion();
    private NodoCancion actual;
    private boolean enPausa = false;
    private boolean modoAleatorio = false;
    private boolean isSelectingFromHistory = false;
    private final ObservableList<Cancion> cancionesList = FXCollections.observableArrayList();
    private final Map<String, ArrayList<Cancion>> listasDeReproduccion = new HashMap<>();
    private final ObservableList<String> historialReproduccion = FXCollections.observableArrayList();
    
    // Variables para el ecualizador
    private AudioEqualizer equalizer; // Para MediaPlayer (MP3/WAV)
    private Map<String, Float> javaSoundEQGains = new HashMap<>(); // Para JavaSound (WMA)
    private UserAuth.Usuario usuarioActual; // Almacena el usuario actual
    
    @FXML private MenuButton menuUsuario;
    @FXML private MenuItem menuItemCerrarSesion;
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
    @FXML private ListView<String> listaHistorial;
    @FXML private Slider sliderBajos;
    @FXML private Slider sliderMedios;
    @FXML private Slider sliderAgudos;

    @FXML
    public void initialize() {
        if (colNombre != null) {
            colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        }
        if (colArtista != null) {
            colArtista.setCellValueFactory(cellData -> cellData.getValue().artistaProperty());
        }
        if (colGenero != null) {
            colGenero.setCellValueFactory(cellData -> cellData.getValue().generoProperty());
        }
        if (colAlbum != null) {
            colAlbum.setCellValueFactory(cellData -> cellData.getValue().albumProperty());
        }
        if (colAnio != null) {
            colAnio.setCellValueFactory(cellData -> cellData.getValue().anioProperty());
        }

        if (tablaCanciones != null) {
            tablaCanciones.setItems(cancionesList);

            tablaCanciones.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !isSelectingFromHistory) {
                    actual = listaReproduccion.buscarPorNombre(newVal.getNombre());
                    reproducirCancionSeleccionada();
                }
            });
            
            tablaCanciones.setRowFactory(tv -> {
                TableRow<Cancion> row = new TableRow<>();
                row.setOnMouseEntered(e -> {
                    row.setScaleX(1.02);
                    row.setScaleY(1.02);
                });
                row.setOnMouseExited(e -> {
                    row.setScaleX(1.0);
                    row.setScaleY(1.0);
                });
                return row;
            });
        }

        if (btnCrearLista != null) {
            Timeline pulsoPlay = new Timeline(
                new KeyFrame(Duration.seconds(0), e -> btnCrearLista.setStyle("-fx-effect: dropshadow(gaussian, yellow, 10, 0.5, 0, 0);")),
                new KeyFrame(Duration.seconds(1), e -> btnCrearLista.setStyle("-fx-effect: none;"))
            );
            pulsoPlay.setCycleCount(Timeline.INDEFINITE);
            pulsoPlay.play();
        }

        if (sliderVolumen != null) {
            sliderVolumen.setMin(0);
            sliderVolumen.setMax(100);
            sliderVolumen.setValue(50);
            sliderVolumen.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(newVal.doubleValue() / 100);
                }
                if (clip != null && clip.isOpen()) {
                    try {
                        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                        float volume = newVal.floatValue() / 100;
                        float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
                        gainControl.setValue(dB);
                    } catch (Exception e) {
                        System.err.println("Error al ajustar volumen de Clip: " + e.getMessage());
                    }
                }
            });
        }

        if (sliderProgreso != null) {
            sliderProgreso.setMin(0);
            sliderProgreso.setValue(0);
            sliderProgreso.setDisable(true);
            sliderProgreso.setOnMouseReleased(event -> {
                if (mediaPlayer != null) {
                    mediaPlayer.seek(Duration.seconds(sliderProgreso.getValue()));
                }
                if (clip != null && clip.isOpen()) {
                    clip.setMicrosecondPosition((long) (sliderProgreso.getValue() * 1000000));
                    if (!enPausa) clip.start();
                }
            });
        }

        // Inicializar sliders del ecualizador
        initializeEqualizerSliders();

        if (botonAleatorio != null) {
            botonAleatorio.setOnAction(e -> {
                modoAleatorio = botonAleatorio.isSelected();
                cambiarColor();
            });
        }

        if (comboBoxListas != null) {
            comboBoxListas.setOnAction(e -> {
                String nombreSeleccionado = comboBoxListas.getValue();
                ArrayList<Cancion> cancionesSeleccionadas = listasDeReproduccion.get(nombreSeleccionado);
                if (cancionesSeleccionadas != null) {
                    setListaCanciones(cancionesSeleccionadas, false);
                }
                boolean esPrincipal = "Lista Principal".equals(nombreSeleccionado);
                if (btnAgregarCancion != null) btnAgregarCancion.setVisible(!esPrincipal);
                if (btnEliminarCancion != null) btnEliminarCancion.setVisible(!esPrincipal);
            });
        }

        if (btnEliminarCancion != null) btnEliminarCancion.setVisible(false);
        if (btnAgregarCancion != null) btnAgregarCancion.setVisible(false);
        cambiarColor();

        // Vincular ListView del historial
        if (listaHistorial != null) {
            listaHistorial.setItems(historialReproduccion);
            listaHistorial.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    String nombreCancion = newVal.split(" - ")[0];
                    for (Cancion cancion : cancionesList) {
                        if (cancion.getNombre().equals(nombreCancion)) {
                            isSelectingFromHistory = true;
                            actual = listaReproduccion.buscarPorNombre(cancion.getNombre());
                            tablaCanciones.getSelectionModel().select(cancion);
                            reproducirCancionSeleccionada();
                            isSelectingFromHistory = false;
                            break;
                        }
                    }
                }
            });
        }

        configurarEfectosVisuales();
        
        // Verificar si el botón de menú de usuario existe y configurar el handler de cerrar sesión
        if (menuItemCerrarSesion != null) {
            menuItemCerrarSesion.setOnAction(this::cerrarSesion);
        }
    }
    
    private void initializeEqualizerSliders() {
        if (sliderBajos != null) {
            sliderBajos.setMin(-12.0);
            sliderBajos.setMax(12.0);
            sliderBajos.setValue(0.0);
            sliderBajos.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null && equalizer != null) {
                    for (EqualizerBand band : equalizer.getBands()) {
                        if (band.getCenterFrequency() <= 250) {
                            band.setGain(newVal.doubleValue());
                        }
                    }
                }
                if (clip != null && clip.isOpen()) {
                    javaSoundEQGains.put("bajos", newVal.floatValue());
                    aplicarEcualizadorJavaSound();
                }
            });
        }
        
        if (sliderMedios != null) {
            sliderMedios.setMin(-12.0);
            sliderMedios.setMax(12.0);
            sliderMedios.setValue(0.0);
            sliderMedios.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null && equalizer != null) {
                    for (EqualizerBand band : equalizer.getBands()) {
                        if (band.getCenterFrequency() > 250 && band.getCenterFrequency() <= 4000) {
                            band.setGain(newVal.doubleValue());
                        }
                    }
                }
                if (clip != null && clip.isOpen()) {
                    javaSoundEQGains.put("medios", newVal.floatValue());
                    aplicarEcualizadorJavaSound();
                }
            });
        }
        
        if (sliderAgudos != null) {
            sliderAgudos.setMin(-12.0);
            sliderAgudos.setMax(12.0);
            sliderAgudos.setValue(0.0);
            sliderAgudos.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (mediaPlayer != null && equalizer != null) {
                    for (EqualizerBand band : equalizer.getBands()) {
                        if (band.getCenterFrequency() > 4000) {
                            band.setGain(newVal.doubleValue());
                        }
                    }
                }
                if (clip != null && clip.isOpen()) {
                    javaSoundEQGains.put("agudos", newVal.floatValue());
                    aplicarEcualizadorJavaSound();
                }
            });
        }
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
            if (comboBoxListas != null && !comboBoxListas.getItems().contains("Lista Principal")) {
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
                mostrarAlerta("Nombre inválido o ya existe.", Alert.AlertType.WARNING);
            }
        });
    }

    @FXML
    public void eliminarLista() {
        String listaSeleccionada = comboBoxListas.getValue();

        if ("Lista Principal".equals(listaSeleccionada)) {
            mostrarAlerta("La Lista Principal no se puede eliminar.", Alert.AlertType.WARNING);
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
            mostrarAlerta("Selecciona una lista personalizada para agregar canciones.", Alert.AlertType.WARNING);
            return;
        }

        List<String> opciones = new ArrayList<>();
        for (Cancion c : listasDeReproduccion.get("Lista Principal")) {
            opciones.add(c.getNombre());
        }
        
        if (opciones.isEmpty()) {
            mostrarAlerta("No hay canciones disponibles para agregar.", Alert.AlertType.WARNING);
            return;
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
            mostrarAlerta("Selecciona una canción de una lista personalizada para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        ArrayList<Cancion> lista = listasDeReproduccion.get(listaSeleccionada);
        if (lista.removeIf(c -> c.getNombre().equals(seleccion.getNombre()))) {
            setListaCanciones(lista, false);
        } else {
            mostrarAlerta("No se pudo eliminar la canción.", Alert.AlertType.WARNING);
        }
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void reproducirCancionSeleccionada() {
        if (actual == null) return;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        if (clip != null) {
            clip.stop();
            clip.close();
            clip = null;
        }

        String extension = getFileExtension(actual.ruta);
        Cancion cancion = actualToCancion(actual);
        if ("mp3".equalsIgnoreCase(extension) || "wav".equalsIgnoreCase(extension)) {
            reproducirConMediaPlayer(cancion);
        } else if ("wma".equalsIgnoreCase(extension)) {
            reproducirConJavaSound(cancion);
        } else {
            mostrarAlerta("Formato no soportado: " + extension, Alert.AlertType.WARNING);
        }
    }

    private void reproducirConMediaPlayer(Cancion cancion) {
        try {
            Media media = new Media(new File(actual.ruta).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            equalizer = mediaPlayer.getAudioEqualizer();
            equalizer.setEnabled(true);
            inicializarEcualizadorMediaPlayer();

            mediaPlayer.setOnReady(() -> {
                String entradaHistorial = actual.nombre + " - " +
                        (cancion.getArtista() != null && !cancion.getArtista().isEmpty()
                                ? cancion.getArtista() : "Desconocido");
                if (historialReproduccion.isEmpty() || !historialReproduccion.get(historialReproduccion.size() - 1).equals(entradaHistorial)) {
                    historialReproduccion.add(entradaHistorial);
                }

                mediaPlayer.setVolume(sliderVolumen.getValue() / 100);
                sliderProgreso.setMax(mediaPlayer.getMedia().getDuration().toSeconds());
                sliderProgreso.setDisable(false);
                actualizarEtiqueta();

                String artista = cancion.getArtista() != null ? cancion.getArtista() : "Desconocido";
                String genero = cancion.getGenero() != null ? cancion.getGenero() : "Desconocido";
                labelArtista.setText("👤 Artista: " + artista);
                labelGenero.setText("🎶 Género: " + genero);

                mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> {
                    if (!sliderProgreso.isValueChanging()) {
                        sliderProgreso.setValue(newTime.toSeconds());
                        actualizarTiempo(newTime, mediaPlayer.getMedia().getDuration());
                    }
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
            });

            mediaPlayer.setOnError(() -> {
                mostrarAlerta("Error al reproducir " + getFileExtension(actual.ruta).toUpperCase() + ": " + 
                             (mediaPlayer.getError() != null ? mediaPlayer.getError().getMessage() : "Error desconocido"), 
                             Alert.AlertType.ERROR);
                mediaPlayer.dispose();
                mediaPlayer = null;
            });
        } catch (Exception e) {
            mostrarAlerta("Error al reproducir " + getFileExtension(actual.ruta).toUpperCase() + ": " + e.getMessage(), 
                         Alert.AlertType.ERROR);
        }
    }

    private void inicializarEcualizadorMediaPlayer() {
        if (equalizer == null) return;
        ObservableList<EqualizerBand> bands = equalizer.getBands();
        // Ajustar ganancias iniciales según sliders
        for (EqualizerBand band : bands) {
            double freq = band.getCenterFrequency();
            if (freq <= 250) {
                band.setGain(sliderBajos.getValue());
            } else if (freq <= 4000) {
                band.setGain(sliderMedios.getValue());
            } else {
                band.setGain(sliderAgudos.getValue());
            }
        }
    }

    private void reproducirConJavaSound(Cancion cancion) {
        AudioInputStream audioInputStream = null;
        try {
            File audioFile = new File(actual.ruta);
            audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            AudioFormat originalFormat = audioInputStream.getFormat();
            System.out.println("Formato original de " + cancion.getNombre() + ": " + originalFormat);

            AudioFormat targetFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    originalFormat.getSampleRate(),
                    16,
                    originalFormat.getChannels(),
                    originalFormat.getChannels() * 2,
                    originalFormat.getSampleRate(),
                    false
            );

            if (!AudioSystem.isConversionSupported(targetFormat, originalFormat)) {
                mostrarAlerta("Formato de audio WMA no soportado por Tritonus: " + originalFormat, Alert.AlertType.ERROR);
                return;
            }

            AudioInputStream convertedStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            clip = AudioSystem.getClip();
            clip.open(convertedStream);

            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float volume = (float) (sliderVolumen.getValue() / 100.0);
            float dB = (float) (Math.log(volume == 0 ? 0.0001 : volume) / Math.log(10.0) * 20.0);
            gainControl.setValue(dB);

            long durationMicros = clip.getMicrosecondLength();
            if (durationMicros <= 0) {
                mostrarAlerta("No se pudo determinar la duración del archivo WMA: " + cancion.getNombre(), Alert.AlertType.ERROR);
                clip.close();
                clip = null;
                return;
            }

            String entradaHistorial = actual.nombre + " - " +
                    (cancion.getArtista() != null && !cancion.getArtista().isEmpty()
                            ? cancion.getArtista() : "Desconocido");
            if (historialReproduccion.isEmpty() || !historialReproduccion.get(historialReproduccion.size() - 1).equals(entradaHistorial)) {
                historialReproduccion.add(entradaHistorial);
            }

            actualizarEtiqueta();
            String artista = cancion.getArtista() != null ? cancion.getArtista() : "Desconocido";
            String genero = cancion.getGenero() != null ? cancion.getGenero() : "Desconocido";
            labelArtista.setText("👤 Artista: " + artista);
            labelGenero.setText("🎶 Género: " + genero);

            double durationSeconds = durationMicros / 1000000.0;
            sliderProgreso.setMax(durationSeconds);
            sliderProgreso.setDisable(false);

            // Aplicar ajustes del ecualizador para JavaSound
            javaSoundEQGains.put("bajos", (float) sliderBajos.getValue());
            javaSoundEQGains.put("medios", (float) sliderMedios.getValue());
            javaSoundEQGains.put("agudos", (float) sliderAgudos.getValue());
            aplicarEcualizadorJavaSound();

            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.1), ev -> {
                if (clip != null && clip.isRunning()) {
                    sliderProgreso.setValue(clip.getMicrosecondPosition() / 1000000.0);
                    actualizarTiempo(Duration.seconds(clip.getMicrosecondPosition() / 1000000.0), Duration.seconds(durationSeconds));
                }
            }));
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.play();

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP && clip != null && clip.getMicrosecondPosition() >= durationMicros) {
                    timeline.stop();
                    sliderProgreso.setValue(durationSeconds);
                    actual = modoAleatorio ? seleccionarAleatoria() : actual.siguiente;
                    if (actual != null) {
                        tablaCanciones.getSelectionModel().select(actualToCancion(actual));
                        reproducirCancionSeleccionada();
                    }
                }
            });

            clip.start();
            enPausa = false;
        } catch (UnsupportedAudioFileException e) {
            mostrarAlerta("Formato de audio WMA no soportado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            mostrarAlerta("Línea de audio no disponible para WMA: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error al reproducir WMA: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } finally {
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (Exception e) {
                    System.err.println("Error al cerrar AudioInputStream: " + e.getMessage());
                }
            }
        }
    }

    private void aplicarEcualizadorJavaSound() {
        if (clip == null || !clip.isOpen()) return;
        try {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // Combinar ganancias de bajos, medios y agudos (simplificado)
            float bajosGain = javaSoundEQGains.getOrDefault("bajos", 0.0f);
            float mediosGain = javaSoundEQGains.getOrDefault("medios", 0.0f);
            float agudosGain = javaSoundEQGains.getOrDefault("agudos", 0.0f);
            // Promediar las ganancias para simular un efecto (limitado por JavaSound)
            float combinedGain = (bajosGain + mediosGain + agudosGain) / 3.0f;
            gainControl.setValue(combinedGain);
        } catch (Exception e) {
            System.err.println("Error al aplicar ecualizador JavaSound: " + e.getMessage());
        }
    }

    private String getFileExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot != -1 && lastDot < path.length() - 1) {
            return path.substring(lastDot + 1).toLowerCase();
        }
        return "";
    }

    @FXML
    public void Play(ActionEvent event) {
        if (actual == null) {
            Cancion seleccion = tablaCanciones.getSelectionModel().getSelectedItem();
            if (seleccion != null) {
                actual = listaReproduccion.buscarPorNombre(seleccion.getNombre());
                reproducirCancionSeleccionada();
            }
        } else if (enPausa) {
            if (mediaPlayer != null) {
                mediaPlayer.play();
            }
            if (clip != null && clip.isOpen()) {
                clip.start();
            }
            enPausa = false;
            actualizarEtiqueta();
        } else if (modoAleatorio) {
            actual = seleccionarAleatoria();
            tablaCanciones.getSelectionModel().select(actualToCancion(actual));
            reproducirCancionSeleccionada();
        }
    }
    @FXML
    public void Pausa(ActionEvent event) {
        if (mediaPlayer != null && !enPausa) {
            mediaPlayer.pause();
            enPausa = true;
            if (labelCancion != null) {
                labelCancion.setText("⏸ Pausado: " + actual.nombre);
            }
        }
        if (clip != null && clip.isRunning()) {
            clip.stop();
            enPausa = true;
            if (labelCancion != null) {
                labelCancion.setText("⏸ Pausado: " + actual.nombre);
            }
        }
    }

    @FXML
    public void Siguiente(ActionEvent event) {
        if (actual != null) {
            actual = modoAleatorio ? seleccionarAleatoria() : actual.siguiente;
            if (tablaCanciones != null) {
                tablaCanciones.getSelectionModel().select(actualToCancion(actual));
                reproducirCancionSeleccionada();
            }
        }
    }

    @FXML
    public void Anterior(ActionEvent event) {
        if (actual != null) {
            actual = modoAleatorio ? seleccionarAleatoria() : actual.anterior;
            if (tablaCanciones != null) {
                tablaCanciones.getSelectionModel().select(actualToCancion(actual));
                reproducirCancionSeleccionada();
            }
        }
    }

    @FXML
    private void cambiarColor() {
        if (botonAleatorio != null) {
            botonAleatorio.setStyle(botonAleatorio.isSelected() ?
                    "-fx-background-color: #4CAF50; -fx-text-fill: white;" :
                    "-fx-background-color: #dddddd; -fx-text-fill: black;");
        }
    }

    private NodoCancion seleccionarAleatoria() {
        if (cancionesList.isEmpty()) return null;
        Random random = new Random();
        Cancion aleatoria = cancionesList.get(random.nextInt(cancionesList.size()));
        return listaReproduccion.buscarPorNombre(aleatoria.getNombre());
    }

    private void actualizarEtiqueta() {
        if (labelCancion != null && actual != null) {
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
        if (labelCancion != null) {
            DropShadow sombra = new DropShadow(10, Color.GRAY);
            labelCancion.setEffect(sombra);

            Timeline parpadeo = new Timeline(
                    new KeyFrame(Duration.seconds(0.5), e -> labelCancion.setTextFill(Color.YELLOW)),
                    new KeyFrame(Duration.seconds(1), e -> labelCancion.setTextFill(Color.WHITE))
            );
            parpadeo.setCycleCount(Timeline.INDEFINITE);
            parpadeo.play();
        }
    }

    public void setUsuarioActual(UserAuth.Usuario usuario) {
        this.usuarioActual = usuario;

        // Configurar el botón del menú con el nombre del usuario
        if (menuUsuario != null && usuario != null) {
            menuUsuario.setText(usuario.getNombre() + " ");
        }
    }

    // Método para cerrar sesión
    @FXML
    public void cerrarSesion(ActionEvent event) {
        try {
            // Detener la reproducción si hay algo sonando
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.dispose();
            }
            if (clip != null && clip.isOpen()) {
                clip.stop();
                clip.close();
            }

            // Cargar la pantalla de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // Obtener la ventana actual
            Stage stage;
            if (event != null && event.getSource() instanceof MenuItem) {
                MenuItem source = (MenuItem) event.getSource();
                MenuButton menuButton = (MenuButton) source.getParentPopup().getOwnerNode();
                stage = (Stage) menuButton.getScene().getWindow();
            } else if (menuUsuario != null) {
                stage = (Stage) menuUsuario.getScene().getWindow();
            } else {
                // Intentar obtener la ventana desde cualquier componente disponible
                stage = findStage();
                if (stage == null) {
                    mostrarAlerta("No se pudo obtener la ventana actual.", Alert.AlertType.ERROR);
                    return;
                }
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.show();

            // Efecto de transición
            FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al cerrar sesión: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    // Método auxiliar para encontrar el Stage desde cualquier componente
    private Stage findStage() {
        if (tablaCanciones != null) return (Stage) tablaCanciones.getScene().getWindow();
        if (comboBoxListas != null) return (Stage) comboBoxListas.getScene().getWindow();
        if (sliderVolumen != null) return (Stage) sliderVolumen.getScene().getWindow();
        if (botonAleatorio != null) return (Stage) botonAleatorio.getScene().getWindow();
        if (listaHistorial != null) return (Stage) listaHistorial.getScene().getWindow();
        return null;
    }

    // Inner classes
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
        for (Cancion cancion : cancionesList) {
            if (cancion.getNombre().equals(nodo.nombre) && cancion.getRuta().equals(nodo.ruta)) {
                return cancion;
            }
        }
        return new Cancion(nodo.nombre, nodo.ruta, null, null, null, null);
    }
    
    @FXML
    public void abrirOpcionesPerfil(ActionEvent event) {
        try {
            // Detener la reproducción si hay algo sonando
            if (mediaPlayer != null) {
                mediaPlayer.pause();
            }
            if (clip != null && clip.isOpen()) {
                clip.stop();
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("PerfilUsuario.fxml"));
            Parent root = loader.load();

            // Pasar el usuario actual y la lista de canciones al controlador
            PerfilUsuarioController controller = loader.getController();
            controller.setUsuarioActual(usuarioActual, new ArrayList<>(cancionesList));

            Stage stage = (Stage) menuUsuario.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Opciones de Perfil");
            stage.show();

            // Efecto de transición
            FadeTransition fade = new FadeTransition(Duration.seconds(1), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir opciones de perfil: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}