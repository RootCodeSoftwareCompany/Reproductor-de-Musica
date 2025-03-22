package reproducir;

public class Reproductor {
    // Instancia de la lista de reproducción
    private static Nodo listaReproduccion = new Nodo();

    // Método para añadir canciones a la lista
    public static void aniadir(String nombre, String ruta) {
        listaReproduccion.agregarCancion(nombre, ruta);
    }

    // Métodos para controlar la reproducción
    public static void play() {
        listaReproduccion.reproducir();
    }

    public static void pause() {
        listaReproduccion.pausar();
    }

    public static void siguiente() {
        listaReproduccion.siguiente();
    }

    public static void anterior() {
        listaReproduccion.anterior();
    }
    
    public static void reanudar(){
        listaReproduccion.reanudar();
    }
    
    public static boolean isPaused() {
        return listaReproduccion.isPaused(); // Verificar si la canción está en pausa
    }

    // Método para obtener la lista de reproducción
    public static Nodo getListaReproduccion() {
        return listaReproduccion;
    }
    
}