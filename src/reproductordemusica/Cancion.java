package reproductordemusica;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cancion {
    private final StringProperty nombre;
    private final StringProperty ruta;
    private final StringProperty artista;
    private final StringProperty genero;
    private final StringProperty album;
    private final StringProperty anio;

    public Cancion(String nombre, String ruta, String artista, String genero, String album, String anio) {
        this.nombre = new SimpleStringProperty(nombre);
        this.ruta = new SimpleStringProperty(ruta);
        this.artista = new SimpleStringProperty(artista);
        this.genero = new SimpleStringProperty(genero);
        this.album = new SimpleStringProperty(album);
        this.anio = new SimpleStringProperty(anio);
    }

    public String getNombre() {
        return nombre.get();
    }


    public String getRuta() {
        return ruta.get();
    }


    public String getArtista() {
        return artista.get();
    }


    public String getGenero() {
        return genero.get();
    }


    public String getAlbum() {
        return album.get();
    }


    public String getAnio() {
        return anio.get();

    }

    public void setArtista(String artista) {
        this.artista.set(artista);
    }

    public void setGenero(String genero) {
        this.genero.set(genero);
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public void setAnio(String anio) {
        this.anio.set(anio);
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    public StringProperty rutaProperty() {
        return ruta;
    }

    public StringProperty artistaProperty() {
        return artista;
    }

    public StringProperty generoProperty() {
        return genero;
    }

    public StringProperty albumProperty() {
        return album;
    }

    public StringProperty anioProperty() {
        return anio;
    }
}
