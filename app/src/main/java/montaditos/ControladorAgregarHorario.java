package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Controlador para la interfaz de agregar horarios en la aplicación 100 Montaditos.
 * Permite al usuario agregar nuevos horarios para un usuario específico.
 * @version 1.0
 */
public class ControladorAgregarHorario {
    private ControladorHorarios controladorHorarios;
    private int usuarioId;

    @FXML
    private ComboBox<String> comboDia;

    @FXML
    private TextField txtHorario;

    /**
     * Establece el ID del usuario para el cual se agregarán los horarios.
     *
     * @param usuarioId El ID del usuario.
     */
    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    /**
     * Establece el controlador principal de horarios para actualizar la vista principal.
     *
     * @param controladorHorarios El controlador de horarios principal.
     */
    public void setControladorHorarios(ControladorHorarios controladorHorarios) {
        this.controladorHorarios = controladorHorarios;
    }

    /**
     * Inicializa el ComboBox de día con los días de la semana.
     */
    @FXML
    private void initialize() {
        comboDia.getItems().addAll("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo");
    }

    /**
     * Maneja el evento de guardar el nuevo horario.
     * Verifica la entrada y guarda el nuevo horario en la base de datos.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleGuardar(ActionEvent event) {
        String diaSeleccionado = comboDia.getValue();
        String franjaHorariaSeleccionada = txtHorario.getText();

        if (diaSeleccionado == null || franjaHorariaSeleccionada.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un día y una franja horaria.");
            return;
        }

        if (controladorHorarios != null && controladorHorarios.existeDiaEnHorarios(diaSeleccionado)) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Ya hay un horario registrado para este día.");
            return;
        }

        guardarHorarioEnBaseDeDatos(diaSeleccionado, franjaHorariaSeleccionada);

        Stage stage = (Stage) comboDia.getScene().getWindow();
        stage.close();
    }

    /**
     * Maneja el evento de cancelar la acción.
     * Cierra la ventana sin guardar cambios.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleCancelar(ActionEvent event) {
        Stage stage = (Stage) comboDia.getScene().getWindow();
        stage.close();
    }

    /**
     * Guarda el nuevo horario en la base de datos.
     *
     * @param dia          El día de la semana.
     * @param franjaHoraria La franja horaria.
     */
    private void guardarHorarioEnBaseDeDatos(String dia, String franjaHoraria) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "INSERT INTO horario (idUsuario, diaSemana, franjaHoraria) VALUES (?, ?, ?)";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, usuarioId);
                statement.setString(2, dia);
                statement.setString(3, franjaHoraria);
                statement.executeUpdate();

                controladorHorarios.refrescarHorarios();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al guardar el horario en la base de datos.");
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
}
