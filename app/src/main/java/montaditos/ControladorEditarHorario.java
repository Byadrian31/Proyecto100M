package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Controlador para la interfaz de edición de horarios en la aplicación 100 Montaditos.
 * Permite modificar los horarios existentes para un usuario específico.
 * @version 1.0.
 */
public class ControladorEditarHorario {

    @FXML
    private TextField txtIdUsuario;

    @FXML
    private ComboBox<String> comboDiaSemana;

    @FXML
    private TextField txtFranjaHoraria;

    private Horario horario;

    private ControladorHorarios controladorHorarios; // Referencia al controlador principal

    /**
     * Configura el controlador principal para refrescar la tabla de horarios después de la edición.
     *
     * @param controladorHorarios El controlador principal de horarios.
     */
    public void setControladorHorarios(ControladorHorarios controladorHorarios) {
        this.controladorHorarios = controladorHorarios;
    }

    /**
     * Configura los datos del horario a editar.
     *
     * @param horario El objeto Horario a editar.
     */
    public void setHorario(Horario horario) {
        this.horario = horario;
        txtIdUsuario.setText(Integer.toString(horario.getIdUsuario()));
        comboDiaSemana.setValue(horario.getDiaSemana());
        txtFranjaHoraria.setText(horario.getFranjaHoraria());
    }

    /**
     * Maneja la acción de guardar los cambios realizados en el horario.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleGuardar(ActionEvent event) {
        int idUsuario = Integer.parseInt(txtIdUsuario.getText());
        String diaSemana = comboDiaSemana.getValue();
        String franjaHoraria = txtFranjaHoraria.getText();

        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {

            // Eliminar el registro existente
            String deleteQuery = "DELETE FROM horario WHERE idUsuario = ? AND diaSemana = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteQuery)) {
                deleteStatement.setInt(1, idUsuario);
                deleteStatement.setString(2, horario.getDiaSemana());
                deleteStatement.executeUpdate();
            }

            // Insertar el nuevo registro
            String insertQuery = "INSERT INTO horario (idUsuario, diaSemana, franjaHoraria) VALUES (?, ?, ?)";
            try (PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {
                insertStatement.setInt(1, idUsuario);
                insertStatement.setString(2, diaSemana);
                insertStatement.setString(3, franjaHoraria);
                insertStatement.executeUpdate();
            }

            showAlert(Alert.AlertType.INFORMATION, "Guardado", "Horario actualizado correctamente.");
            if (controladorHorarios != null) {
                controladorHorarios.refrescarHorarios();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al actualizar el horario.");
        }

        closeWindow(event);
    }

    /**
     * Cierra la ventana actual.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
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
