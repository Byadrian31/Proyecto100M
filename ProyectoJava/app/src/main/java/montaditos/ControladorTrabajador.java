package montaditos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorTrabajador {
    @FXML
    private TableView<String[]> tableViewHorarios;

    @FXML
    private TableColumn<String[], String> colDiaSemana;

    @FXML
    private TableColumn<String[], String> colFranjaHoraria;

    @FXML
    private ImageView imageViewImagen;

    // Método para cargar los horarios desde la base de datos
    @FXML
    private void handleMostrarHorarios(ActionEvent event) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";
        int idUsuario = SessionManager.getInstance().getUserId();

        List<String[]> horarios = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
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

        // Asignar los datos a las columnas
        colDiaSemana.setCellValueFactory(cellData -> {
            String[] row = cellData.getValue();
            return new SimpleStringProperty(row[0]);
        });

        colFranjaHoraria.setCellValueFactory(cellData -> {
            String[] row = cellData.getValue();
            return new SimpleStringProperty(row[1]);
        });

        // Una vez que tenemos los horarios, los asignamos a la tabla
        tableViewHorarios.setItems(FXCollections.observableArrayList(horarios));

        // Cambiar la visibilidad de la imagen y la tabla
        imageViewImagen.setVisible(false);
        tableViewHorarios.setVisible(true);
    }


    @FXML
    private void handleVolver(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close(); // Aquí puedes agregar la lógica para volver a la pantalla principal
    }

    @FXML
    private void goToCarta(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/carta.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar carta.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de carta.");
        }
    }

    @FXML
    private void goToPedir(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/pedidoscli.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar pedidoscli.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidoscli.");
        }
    }

    @FXML
    private void goToPedidos(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/pedidostra.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar pedidostra.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidostra.");
        }
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
