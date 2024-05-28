package montaditos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador para la funcionalidad del trabajador.
 * @version 1.0.
 */
public class ControladorTrabajador {

    @FXML
    private TableView<String[]> tableViewHorarios;

    @FXML
    private TableColumn<String[], String> colDiaSemana;

    @FXML
    private TableColumn<String[], String> colFranjaHoraria;

    @FXML
    private ImageView imageViewImagen;

    /**
     * Maneja el evento cuando se hace clic en el botón "Mostrar Horarios" para mostrar los horarios del trabajador.
     *
     * @param event El evento de clic en el botón "Mostrar Horarios".
     */
    @FXML
    private void handleMostrarHorarios(ActionEvent event) {
        // Detalles de la conexión a la base de datos
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";
        int idUsuario = SessionManager.getInstance().getUserId();

        // Lista para almacenar los horarios
        List<String[]> horarios = new ArrayList<>();

        // Conexión a la base de datos
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Consulta para obtener los horarios del usuario
            String query = "SELECT diaSemana, franjaHoraria FROM horario WHERE idUsuario = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, idUsuario);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String diaSemana = resultSet.getString("diaSemana");
                        String franjaHoraria = resultSet.getString("franjaHoraria");
                        horarios.add(new String[]{diaSemana, franjaHoraria});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Asignar los datos a las columnas de la tabla
        colDiaSemana.setCellValueFactory(cellData -> {
            String[] row = cellData.getValue();
            return new SimpleStringProperty(row[0]);
        });

        colFranjaHoraria.setCellValueFactory(cellData -> {
            String[] row = cellData.getValue();
            return new SimpleStringProperty(row[1]);
        });

        // Asignar los horarios a la tabla
        tableViewHorarios.setItems(FXCollections.observableArrayList(horarios));

        // Cambiar la visibilidad de la imagen y la tabla
        imageViewImagen.setVisible(false);
        tableViewHorarios.setVisible(true);
    }

    /**
     * Volver a la pantalla principal.
     * @param event El evento de acción.
     */
    @FXML
    private void handleVolver(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close(); // Aquí puedes agregar la lógica para volver a la pantalla principal
    }

    /**
     * Ir a la carta.
     * @param event El evento de acción.
     */
    @FXML
    private void goToCarta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/carta.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.setTitle("Carta");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar carta.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidos.");
        }
    }

    /**
     * Ir a pedidoscli.
     * @param event El evento de acción.
     */
    @FXML
    private void goToPedir(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/pedidoscli.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.setTitle("Realizar Pedidos");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar pedidoscli.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidos.");
        }
    }

    /**
     * Ir a pedidostra
     * @param event El evento de acción.
     */
    @FXML
    private void goToPedidos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/pedidostra.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.setTitle("Pedidos");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar pedidostra.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidos.");
        }
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados.
     *
     * @param alertType El tipo de alerta.
     * @param title     El título de la alerta.
     * @param message   El mensaje de la alerta.
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
