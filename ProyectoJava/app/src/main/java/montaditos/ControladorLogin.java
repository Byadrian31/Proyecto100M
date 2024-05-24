package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import montaditos.SessionManager;

import java.io.IOException;
import java.sql.*;

public class ControladorLogin {
    @FXML
    private TextField Correo;

    @FXML
    private TextField Nombre;

    @FXML
    private PasswordField Password;


    @FXML
    void handleOk(ActionEvent event) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String dbPassword = "";

        try (Connection connection = DriverManager.getConnection(url, username, dbPassword)) {
            String nombre = Nombre.getText();
            String correo = Correo.getText();
            String contrasena = Password.getText();

            String query = "SELECT id, trabajador FROM usuarios WHERE nombre = ? AND correo = ? AND contrasena = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, nombre);
                statement.setString(2, correo);
                statement.setString(3, contrasena);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Inicio de sesión exitoso.");
                        int userId = resultSet.getInt("id");
                        SessionManager.getInstance().setUserId(userId); // Establecer el ID del usuario en la sesión
                        boolean isWorker = resultSet.getBoolean("trabajador");
                        if (nombre.equals("admin")  && correo.equals("admin")) {
                            openNewScene("/montaditos/admin.fxml", event);
                        }else {
                        if (isWorker) {
                            // Redirigir a trabajador.fxml
                            openNewScene("/montaditos/trabajador.fxml", event);
                        } else{
                            // Redirigir a carta.fxml
                            openNewScene("/montaditos/carta.fxml", event);
                        }
                        }
                    } else {
                        System.out.println("Credenciales incorrectas. Inténtalo de nuevo.");
                        showAlert(Alert.AlertType.ERROR, "Error", "Credenciales incorrectas");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al conectarse a la base de datos.");
            e.printStackTrace();
        }
    }


    // Método para abrir una nueva escena
    private void openNewScene(String fxmlFile, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar la nueva escena.");
            e.printStackTrace();
        }
    }

    @FXML
    private void closeOverlay(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
