package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador para la funcionalidad de inicio de sesión.
 * Este controlador gestiona la lógica detrás de la autenticación de usuarios
 * en la aplicación de Montaditos.
 * @version 1.0.
 */
public class ControladorLogin {

    @FXML
    private TextField Correo;

    @FXML
    private TextField Nombre;

    @FXML
    private PasswordField Password;

    /**
     * Método que se ejecuta cuando se presiona el botón "Ok" para iniciar sesión.
     * Verifica las credenciales ingresadas por el usuario en la base de datos y
     * redirige a la pantalla correspondiente según el rol del usuario.
     *
     * @param event Evento de acción generado por el botón "Ok".
     */
    @FXML
    void handleOk(ActionEvent event) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, username, dbPassword)) {
            String nombre = Nombre.getText();
            String correo = Correo.getText();
            String contrasena = Password.getText();

            String query = "SELECT id, trabajador, contrasena FROM usuarios WHERE nombre = ? AND correo = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nombre);
                statement.setString(2, correo);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        String storedHashedPassword = resultSet.getString("contrasena");
                        if (verifyPassword(contrasena, storedHashedPassword)) {
                            System.out.println("Inicio de sesión exitoso.");
                            int userId = resultSet.getInt("id");
                            SessionManager.getInstance().setUserId(userId); // Establecer el ID del usuario en la sesión
                            boolean isWorker = resultSet.getBoolean("trabajador");
                            if (nombre.equals("admin") && correo.equals("admin")) {
                                openNewScene("/montaditos/admin.fxml", event);
                            } else {
                                if (isWorker) {
                                    // Redirigir a trabajador.fxml
                                    openNewScene("/montaditos/trabajador.fxml", event);
                                } else {
                                    // Redirigir a carta.fxml
                                    openNewScene("/montaditos/carta.fxml", event);
                                }
                            }
                        } else {
                            System.out.println("Credenciales incorrectas. Inténtalo de nuevo.");
                            showAlert(Alert.AlertType.ERROR, "Error", "Credenciales incorrectas");
                        }
                    } else {
                        System.out.println("Credenciales incorrectas. Inténtalo de nuevo.");
                        showAlert(Alert.AlertType.ERROR, "Error", "Credenciales incorrectas");
                    }
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectarse a la base de datos.");
            e.printStackTrace();
        }
    }

    /**
     * Método para verificar si la contraseña ingresada coincide con la almacenada (hash).
     *
     * @param plainPassword La contraseña en texto plano ingresada por el usuario.
     * @param hashedPassword La contraseña hasheada almacenada en la base de datos.
     * @return true si las contraseñas coinciden, false de lo contrario.
     * @throws NoSuchAlgorithmException Si el algoritmo SHA-256 no está disponible.
     */
    private boolean verifyPassword(String plainPassword, String hashedPassword) throws NoSuchAlgorithmException {
        String hashedPlainPassword = hashPassword(plainPassword);
        return hashedPlainPassword.equals(hashedPassword);
    }

    /**
     * Aplica hash SHA-256 a la contraseña proporcionada.
     *
     * @param password La contraseña en texto plano.
     * @return La contraseña hasheada en formato hexadecimal.
     * @throws NoSuchAlgorithmException Si el algoritmo SHA-256 no está disponible.
     */
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedHash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(encodedHash);
    }

    /**
     * Convierte un arreglo de bytes en una cadena hexadecimal.
     *
     * @param hash El arreglo de bytes.
     * @return La cadena hexadecimal.
     */
    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Método para abrir una nueva escena.
     * Carga un archivo FXML y muestra su contenido en una nueva ventana.
     *
     * @param fxmlFile Ruta del archivo FXML que se va a cargar.
     * @param event    Evento de acción que desencadena la apertura de la nueva escena.
     */
    private void openNewScene(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar la nueva escena.");
            e.printStackTrace();
        }
    }

    /**
     * Método para cerrar la ventana de superposición.
     * Cierra la ventana de superposición actual cuando se presiona el botón correspondiente.
     *
     * @param event Evento de acción generado por el botón para cerrar la ventana de superposición.
     */
    @FXML
    private void closeOverlay(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Método para mostrar una alerta al usuario.
     * Muestra una ventana de alerta con el tipo, título y mensaje especificados.
     *
     * @param alertType Tipo de alerta a mostrar (por ejemplo, INFORMATION, WARNING, ERROR).
     * @param title     Título de la ventana de alerta.
     * @param message   Mensaje a mostrar en la ventana de alerta.
     */
    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
