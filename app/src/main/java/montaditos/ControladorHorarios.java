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
import montaditos.Horario;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador para la gestión de horarios en la aplicación 100 Montaditos.
 * Permite visualizar, añadir, editar y borrar horarios de la base de datos.
 * @version 1.0.
 */
public class ControladorHorarios {

    @FXML
    private TableView<Horario> tableViewHorarios;

    @FXML
    private TableColumn<Horario, String> colDia;

    @FXML
    private TableColumn<Horario, String> colFranjaHoraria;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnVolver;

    @FXML
    private Button btnAnadir;

    private int usuarioId;

    /**
     * Establece el ID del usuario y carga los horarios correspondientes desde la base de datos.
     *
     * @param usuarioId El ID del usuario.
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
        cargarHorariosDesdeBaseDeDatos();
        initialize();
    }

    /**
     * Inicializa la configuración de la tabla de horarios.
     */
    @FXML
    private void initialize() {
        colDia.setCellValueFactory(cellData -> cellData.getValue().diaSemanaProperty());
        colFranjaHoraria.setCellValueFactory(cellData -> cellData.getValue().franjaHorariaProperty());
        actualizarInterfaz();
    }

    /**
     * Obtiene el número de horarios actuales en la tabla.
     *
     * @return El número de horarios.
     */
    public int getNumeroDeHorarios() {
        return tableViewHorarios.getItems().size();
    }

    /**
     * Actualiza el estado de la interfaz dependiendo del número de horarios existentes.
     */
    private void actualizarInterfaz() {
        if (getNumeroDeHorarios() >= 7) {
            btnAnadir.setDisable(true);
        } else {
            btnAnadir.setDisable(false);
        }
    }

    /**
     * Verifica si un día específico ya existe en la lista de horarios.
     *
     * @param dia El día a verificar.
     * @return true si el día ya existe, false en caso contrario.
     */
    public boolean existeDiaEnHorarios(String dia) {
        for (Horario horario : tableViewHorarios.getItems()) {
            if (horario.getDiaSemana().equalsIgnoreCase(dia)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Carga los horarios desde la base de datos para el usuario especificado.
     */
    private void cargarHorariosDesdeBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        ObservableList<Horario> horarios = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT diaSemana, franjaHoraria FROM horario WHERE idUsuario = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, usuarioId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String diaSemana = resultSet.getString("diaSemana");
                        String franjaHoraria = resultSet.getString("franjaHoraria");
                        horarios.add(new Horario(usuarioId, diaSemana, franjaHoraria));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableViewHorarios.setItems(horarios);
    }

    /**
     * Maneja la acción de editar un horario seleccionado.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleEditar(ActionEvent event) {
        Horario horarioSeleccionado = tableViewHorarios.getSelectionModel().getSelectedItem();

        if (horarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un horario para editar.");
            return;
        }

        openEditForm(horarioSeleccionado);
    }

    /**
     * Abre el formulario de edición para el horario seleccionado.
     *
     * @param horario El horario a editar.
     */
    private void openEditForm(Horario horario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/editarhorarios.fxml"));
            Parent root = loader.load();

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
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la ventana de edición de horario.");
        }
    }

    /**
     * Muestra una alerta en pantalla.
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

    /**
     * Refresca la lista de horarios desde la base de datos.
     */
    public void refrescarHorarios() {
        cargarHorariosDesdeBaseDeDatos();
    }

    /**
     * Maneja la acción de añadir un nuevo horario.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleAnadir(ActionEvent event) {
        if (btnAnadir.isDisabled()) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "No se pueden añadir más horarios.");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/agregarhorario.fxml"));
            Parent root = loader.load();

            ControladorAgregarHorario controller = loader.getController();
            controller.setUsuarioId(usuarioId);
            controller.setControladorHorarios(this);

            Stage stage = new Stage();
            stage.setTitle("Agregar Horario");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(btnAnadir.getScene().getWindow());
            stage.showAndWait();
            actualizarInterfaz();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar la ventana de agregar horario.");
        }
    }

    /**
     * Maneja la acción de borrar el horario seleccionado.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleBorrar(ActionEvent event) {
        Horario horarioSeleccionado = tableViewHorarios.getSelectionModel().getSelectedItem();

        if (horarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un horario para borrar.");
            return;
        }

        // Lógica para borrar el horario seleccionado de la base de datos
        borrarHorarioDeBaseDeDatos(horarioSeleccionado);

        // Remover el horario seleccionado de la tabla
        tableViewHorarios.getItems().remove(horarioSeleccionado);
        actualizarInterfaz();
    }

    /**
     * Borra un horario específico de la base de datos.
     *
     * @param horario El horario a borrar.
     */
    private void borrarHorarioDeBaseDeDatos(Horario horario) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "DELETE FROM horario WHERE idUsuario = ? AND diaSemana = ? AND franjaHoraria = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, horario.getIdUsuario());
                statement.setString(2, horario.getDiaSemana());
                statement.setString(3, horario.getFranjaHoraria());
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al borrar el horario de la base de datos.");
        }
    }

    /**
     * Maneja la acción de volver a la ventana anterior.
     *
     * @param event El evento de acción.
     */
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
