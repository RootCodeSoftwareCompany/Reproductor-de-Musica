# 🎵 Reproductor de Música en Java

Este es un **reproductor de música** en Java que permite reproducir archivos MP3 utilizando la biblioteca **JLayer**.

## 📌 Características

- Reproducción de archivos MP3.
- Controles básicos (reproducir, detener).
- Fácil de usar y expandir.

## 🛠️ Requisitos

- **Java 8 o superior**
- **BlueJ, Eclipse o IntelliJ IDEA** (cualquier IDE compatible con Java)
- **Biblioteca JLayer** (jl1.0.1.jar)

## 🚀 Instalación

1. **Descargar la biblioteca JLayer**
   - Obtener `jl1.0.1.jar` desde [JavaZoom](http://www.javazoom.net/javalayer/javalayer.html).
2. **Agregar la biblioteca al proyecto**
   - En BlueJ: Agregar `jl1.0.1.jar` al CLASSPATH desde "Preferencias" > "Bibliotecas".
   - En otros IDEs: Incluir el JAR en las dependencias del proyecto.
3. **Clonar este repositorio**
   ```sh
   git clone https://github.com/usuario/reproductor-musica-java.git
   cd reproductor-musica-java
   ```
4. **Ejecutar el programa**
   ```sh
   javac ReproductorMP3.java
   java ReproductorMP3
   ```

## 📜 Código de ejemplo

```java
import javazoom.jl.player.advanced.AdvancedPlayer;
import java.io.FileInputStream;

public class ReproductorMP3 {
    public static void main(String[] args) {
        try {
            FileInputStream archivo = new FileInputStream("cancion.mp3");
            AdvancedPlayer player = new AdvancedPlayer(archivo);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## 📄 Licencia

Este proyecto está bajo la **Licencia MIT**. Puedes usarlo y modificarlo libremente.

---
