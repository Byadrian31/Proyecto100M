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

public class ControladorEditarHorario {
    @FXML
    private TextField txtIdUsuario;

    @FXML
    private ComboBox<String> comboDiaSemana;

    @FXML
    private TextField txtFranjaHoraria;

    private Horario horario;

    private ControladorHorarios controladorHorarios; // Referencia al controlador principal

    public void setControladorHorarios(ControladorHorarios controladorHorarios) {
        this.controladorHorarios = controladorHorarios;
    }

    public void setHorario(Horario horario) {
        this.horario = horario;
        txtIdUsuario.setText(Integer.toString(horario.getIdUsuario()));
        comboDiaSemana.setValue(horario.getDiaSemana());
        txtFranjaHoraria.setText(horario.getFranjaHoraria());
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        int idUsuario = Integer.parseInt(txtIdUsuario.getText());
        String diaSemana = comboDiaSemana.getValue();
        String franjaHoraria = txtFranjaHoraria.getText();

        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            connection.setAutoCommit(false); // Iniciar transacción

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

            connection.commit(); // Confirmar transacción
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

    @FXML
    private void closeWindow(ActionEvent event) {
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
