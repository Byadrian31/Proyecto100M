package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador para la funcionalidad de registro de usuarios.
 * @version 1.0.
 */
public class ControladorRegistro {

    @FXML
    private TextField Nombre;

    @FXML
    private TextField Apellido;

    @FXML
    private TextField Telefono;

    @FXML
    private TextField Correo;

    @FXML
    private PasswordField Contrasena;

    @FXML
    private PasswordField Contrasena2;

    /**
     * Maneja el evento cuando se hace clic en el botón "Ok" para registrar un usuario.
     *
     * @param event El evento de clic en el botón "Ok".
     */
    @FXML
    void handleOk(ActionEvent event) {
        // Detalles de la conexión a la base de datos
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        // Conexión a la base de datos
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Obtener los datos del formulario de registro
            String nombre = Nombre.getText();
            String apellido = Apellido.getText();
            String telefono = Telefono.getText();
            String correo = Correo.getText();
            String contrasena = Contrasena.getText();
            String contrasena2 = Contrasena2.getText();

            // Verificar si hay campos vacíos
            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || contrasena2.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios.");
                return;
            }

            // Verificar si las contraseñas coinciden
            if (!contrasena.equals(contrasena2)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
                return;
            }

            // Verificar si el usuario ya existe en la base de datos
            String checkUserQuery = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUserQuery)) {
                checkStatement.setString(1, correo);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // El usuario ya existe
                        showAlert(Alert.AlertType.ERROR, "Error", "El usuario ya está en la base de datos.");
                        return;
                    }
                }
            }

            // Aplicar hash SHA-256 a la contraseña
            String hashedPassword = hashPassword(contrasena);

            // Insertar el nuevo usuario en la base de datos
            String insertQuery = "INSERT INTO usuarios (nombre, apellido, telefono, correo, contrasena, trabajador) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, nombre);
                insertStatement.setString(2, apellido);
                insertStatement.setString(3, telefono);
                insertStatement.setString(4, correo);
                insertStatement.setString(5, hashedPassword);
                insertStatement.setBoolean(6, false);
                insertStatement.executeUpdate();
                System.out.println("Usuario añadido correctamente.");
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario añadido correctamente.");
                closeOverlay(event);
            }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al intentar añadir el usuario.");
        }
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
     * Cierra la ventana emergente actual.
     *
     * @param event El evento de clic en el botón de cierre.
     */
    @FXML
    private void closeOverlay(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados.
     *
     * @param alertType El tipo de alerta.
     * @param title     El título de la alerta.
     * @param message   El mensaje de la alerta.
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
