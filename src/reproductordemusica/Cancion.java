package reproductordemusica;

import javafx.beans.property.*;

public class Cancion {
    private StringProperty nombre;
    private StringProperty ruta;
    private StringProperty artista;
    private StringProperty genero;
    private StringProperty album;
    private StringProperty anio;

    public Cancion(String nombre, String ruta, String artista, String genero, String album, String anio) {
        this.nombre = new SimpleStringProperty(nombre);
        this.ruta = new SimpleStringProperty(ruta);
        this.artista = new SimpleStringProperty(artista);
        this.genero = new SimpleStringProperty(genero);
        this.album = new SimpleStringProperty(album);
        this.anio = new SimpleStringProperty(anio);
    }

    // Métodos getter para las propiedades
    public String getNombre() {
        return nombre.get();
    }

    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    public String getRuta() {
        return ruta.get();
    }

    public void setRuta(String ruta) {
        this.ruta.set(ruta);
    }

    public String getArtista() {
        return artista.get();
    }

    public void setArtista(String artista) {
        this.artista.set(artista);
    }

    public String getGenero() {
        return genero.get();
    }

    public void setGenero(String genero) {
        this.genero.set(genero);
    }

    public String getAlbum() {
        return album.get();
    }

    public void setAlbum(String album) {
        this.album.set(album);
    }

    public String getAnio() {
        return anio.get();
    }

    public void setAnio(String anio) {
        this.anio.set(anio);
    }

    // Métodos property para las propiedades String
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

    @Override
    public String toString() {
        return String.format("%s - %s (%s) [%s, %s, %s]",
                nombre.get() != null ? nombre.get() : "Desconocido",
                artista.get() != null ? artista.get() : "Artista desconocido",
                anio.get() != null ? anio.get() : "Año desconocido",
                album.get() != null ? album.get() : "Álbum desconocido",
                genero.get() != null ? genero.get() : "Género desconocido");
    }
}
