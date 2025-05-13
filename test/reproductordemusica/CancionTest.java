package reproductordemusica;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class CancionTest {

    private Cancion cancion;

    @Before
    public void setUp() {
        cancion = new Cancion(
            "Imagine",
            "/musica/imagine.mp3",
            "John Lennon",
            "Rock",
            "Imagine",
            "1971"
        );
    }

    @Test
    public void testGetters() {
        assertEquals("Imagine", cancion.getNombre());
        assertEquals("/musica/imagine.mp3", cancion.getRuta());
        assertEquals("John Lennon", cancion.getArtista());
        assertEquals("Rock", cancion.getGenero());
        assertEquals("Imagine", cancion.getAlbum());
        assertEquals("1971", cancion.getAnio());
    }

    @Test
    public void testSetters() {
        cancion.setArtista("The Beatles");
        cancion.setGenero("Pop");
        cancion.setAlbum("Abbey Road");
        cancion.setAnio("1969");

        assertEquals("The Beatles", cancion.getArtista());
        assertEquals("Pop", cancion.getGenero());
        assertEquals("Abbey Road", cancion.getAlbum());
        assertEquals("1969", cancion.getAnio());
    }

    @Test
    public void testProperties() {
        assertEquals("Imagine", cancion.nombreProperty().get());
        assertEquals("/musica/imagine.mp3", cancion.rutaProperty().get());
    }

    @Test
    public void testCamposVacios() {
        Cancion vacia = new Cancion("", "", "", "", "", "");

        assertEquals("", vacia.getNombre());
        assertEquals("", vacia.getRuta());
        assertEquals("", vacia.getArtista());
        assertEquals("", vacia.getGenero());
        assertEquals("", vacia.getAlbum());
        assertEquals("", vacia.getAnio());
    }

    @Test
    public void testCamposNulos() {
        Cancion nula = new Cancion(null, null, null, null, null, null);

        assertNull(nula.getNombre());
        assertNull(nula.getRuta());
        assertNull(nula.getArtista());
        assertNull(nula.getGenero());
        assertNull(nula.getAlbum());
        assertNull(nula.getAnio());
    }
}