package reproductordemusica;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserAuth {

    private static final String USERS_FILE = "users.txt";

    // Clase interna para representar un usuario
    public static class Usuario {
        private String nombre;
        private int edad;
        private String preferencias;

        public Usuario(String nombre, int edad, String preferencias) {
            this.nombre = nombre;
            this.edad = edad;
            this.preferencias = preferencias;
        }

        public String getNombre() {
            return nombre;
        }

        public int getEdad() {
            return edad;
        }

        public String getPreferencias() {
            return preferencias;
        }
    }

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
                    String passHash = partes[1].trim();
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

    public static List<Usuario> obtenerUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        File archivoUsuarios = new File(USERS_FILE);
        if (!archivoUsuarios.exists()) {
            return usuarios;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 4) {
                    String nombre = partes[0].trim();
                    int edad;
                    try {
                        edad = Integer.parseInt(partes[2].trim());
                    } catch (NumberFormatException e) {
                        edad = 0;
                    }
                    String preferencias = partes[3].trim();
                    usuarios.add(new Usuario(nombre, edad, preferencias));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    public static boolean eliminarUsuario(String nombreUsuario) {
        File archivoUsuarios = new File(USERS_FILE);
        if (!archivoUsuarios.exists()) {
            return false;
        }

        List<String> lineas = new ArrayList<>();
        boolean encontrado = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                if (partes.length >= 1 && partes[0].trim().equalsIgnoreCase(nombreUsuario.trim())) {
                    encontrado = true;
                    continue;
                }
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer usuarios para eliminación: " + e.getMessage());
            return false;
        }

        if (!encontrado) {
            return false;
        }

        try (FileWriter writer = new FileWriter(USERS_FILE)) {
            for (String linea : lineas) {
                writer.write(linea + "\n");
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error al escribir usuarios tras eliminación: " + e.getMessage());
            return false;
        }
    }

    public String obtenerPistaContrasena(String usuario) {
        if (usuario == null || usuario.isEmpty()) {
            return null;
        }
      
        try (BufferedReader reader = new BufferedReader(new FileReader(USERS_FILE))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split(":");
                // Formato esperado: usuario:hash:edad:preferencias:pista
                if (partes.length >= 5 && partes[0].trim().equalsIgnoreCase(usuario.trim())) {
                    return partes[4].trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Error leyendo el archivo de usuarios: " + e.getMessage());
        }
        
        return null; // No se encontró pista para el usuario
    }
}