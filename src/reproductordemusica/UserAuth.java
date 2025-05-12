package reproductordemusica;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class UserAuth {

    private static final String USERS_FILE = "users.txt";

    public UserAuth() {
        verificarArchivo();
    }

    private void verificarArchivo() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Archivo users.txt creado correctamente.");
                }
            } catch (IOException e) {
                System.out.println("Error al crear el archivo users.txt: " + e.getMessage());
            }
        }
    }

    public static boolean existeUsuario(String nombreUsuario) {
        File archivoUsuarios = new File(USERS_FILE);

        if (!archivoUsuarios.exists()) {
            return false;
        }

        try (Scanner scanner = new Scanner(archivoUsuarios)) {
            while (scanner.hasNextLine()) {
                String linea = scanner.nextLine();
                String[] datos = linea.split(":");
                if (datos.length >= 1 && datos[0].trim().equalsIgnoreCase(nombreUsuario.trim())) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo de usuarios", e);
        }

        return false;
    }

    public static String hashSHA256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar hash SHA-256", e);
        }
    }

    public boolean validarCredenciales(String usuario, String contrasena) {
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 2) {
                    String user = partes[0].trim();
                    String passHash = partes[1].trim(); // Hash SHA-256
                    if (user.equalsIgnoreCase(usuario.trim())) {
                        String hashedInputPassword = hashSHA256(contrasena);
                        return passHash.equals(hashedInputPassword);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo de usuarios: " + e.getMessage());
        }
        return false;
    }
    
    // Método para obtener la pista de contraseña de un usuario
    public String obtenerPistaContrasena(String usuario) {
        if (usuario == null || usuario.isEmpty()) {
            return null;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                // El formato del archivo es: usuario:hash:edad:preferencias:pista
                if (partes.length >= 5 && partes[0].trim().equalsIgnoreCase(usuario.trim())) {
                    return partes[4].trim(); // La pista está en la posición 4
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo de usuarios: " + e.getMessage());
        }
        
        return null; // No se encontró pista para el usuario
    }
}