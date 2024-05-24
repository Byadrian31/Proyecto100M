package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

    @FXML
    void handleOk(ActionEvent event) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String nombre = Nombre.getText();
            String apellido = Apellido.getText();
            String telefono = Telefono.getText();
            String correo = Correo.getText();
            String contrasena = Contrasena.getText();
            String contrasena2 = Contrasena2.getText();

            // Verificar si los campos están vacíos
            if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || contrasena2.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios.");
                return;
            }

            // Verificar si las contraseñas coinciden
            if (!contrasena.equals(contrasena2)) {
                showAlert(Alert.AlertType.ERROR, "Error", "Las contraseñas no coinciden.");
                return;
            }

            // Comprobar si el usuario ya existe
            String checkUserQuery = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";
            try (PreparedStatement checkStatement = connection.prepareStatement(checkUserQuery)) {
                checkStatement.setString(1, correo);
                try (ResultSet resultSet = checkStatement.executeQuery()) {
                    if (resultSet.next() && resultSet.getInt(1) > 0) {
                        // Usuario ya existe
                        showAlert(Alert.AlertType.ERROR, "Error", "El usuario ya está en la base de datos.");
                        return;
                    }
                }
            }

            // Insertar el nuevo usuario
            String insertQuery = "INSERT INTO usuarios (nombre, apellido, telefono, correo, contrasena, trabajador) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setString(1, nombre);
                insertStatement.setString(2, apellido);
                insertStatement.setString(3, telefono);
                insertStatement.setString(4, correo);
                insertStatement.setString(5, contrasena);
                insertStatement.setBoolean(6, false);
                insertStatement.executeUpdate();
                System.out.println("Usuario añadido correctamente.");
                showAlert(Alert.AlertType.INFORMATION, "Éxito", "Usuario añadido correctamente.");
                closeOverlay(event);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al intentar añadir el usuario.");
        }
    }

    @FXML
    private void closeOverlay(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
