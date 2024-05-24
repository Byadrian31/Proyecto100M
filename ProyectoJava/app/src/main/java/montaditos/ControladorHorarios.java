package montaditos;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import montaditos.Horario; // Asumiendo que ya tienes la clase Horario definida

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ControladorHorarios {
    @FXML
    private TableView<Horario> tableViewHorarios;

    @FXML
    private TableColumn<Horario, Integer> colId;

    @FXML
    private TableColumn<Horario, String> colDia;

    @FXML
    private TableColumn<Horario, String> colFranjaHoraria;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnVolver;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idUsuarioProperty().asObject());
        colDia.setCellValueFactory(cellData -> cellData.getValue().diaSemanaProperty());
        colFranjaHoraria.setCellValueFactory(cellData -> cellData.getValue().franjaHorariaProperty());

        cargarHorariosDesdeBaseDeDatos();
    }

    private void cargarHorariosDesdeBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        ObservableList<Horario> horarios = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT idUsuario, diaSemana, franjaHoraria FROM horario";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        int idUsuario = resultSet.getInt("idUsuario");
                        String diaSemana = resultSet.getString("diaSemana");
                        String franjaHoraria = resultSet.getString("franjaHoraria");
                        horarios.add(new Horario(idUsuario, diaSemana, franjaHoraria));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableViewHorarios.setItems(horarios);
    }

    @FXML
    private void handleEditar(ActionEvent event) {
        Horario horarioSeleccionado = tableViewHorarios.getSelectionModel().getSelectedItem();

        if (horarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un horario para editar.");
            return;
        }

        // Cargar el formulario de edición
        openEditForm(horarioSeleccionado);
    }

    private void openEditForm(Horario horario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/editarhorarios.fxml"));
            Parent root = loader.load();
            cargarHorariosDesdeBaseDeDatos();

            ControladorEditarHorario controller = loader.getController();
            controller.setHorario(horario);
            controller.setControladorHorarios(this);

            Stage stage = new Stage();
            stage.setTitle("Editar Horario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tableViewHorarios.getScene().getWindow());
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void refrescarHorarios() {
        cargarHorariosDesdeBaseDeDatos();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/admin.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar admin.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de admin.");
        }
    }
}
