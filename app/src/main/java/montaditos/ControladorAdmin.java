package montaditos;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador para la interfaz de administrador de la aplicación 100 Montaditos.
 * Gestiona la visualización y manipulación de los datos de los usuarios.
 * @version 1.0.
 */
public class ControladorAdmin {

    @FXML
    private TableView<String[]> tableViewUsuarios;

    @FXML
    private TableColumn<String[], String> colId;

    @FXML
    private TableColumn<String[], String> colNombre;

    @FXML
    private TableColumn<String[], String> colCorreo;

    @FXML
    private TableColumn<String[], String> colTrabajador;

    @FXML
    private Button btnEliminarUsuario;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnTrabajador;

    @FXML
    private Button btnHorarios;

    /**
     * Inicializa la tabla de usuarios configurando las columnas y cargando los datos desde la base de datos.
     */
    @FXML
    private void initialize() {
        colId.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        colNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        colCorreo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        colTrabajador.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
        cargarUsuariosDesdeBaseDeDatos();
    }

    /**
     * Carga los usuarios desde la base de datos y los agrega a la tabla.
     */
    private void cargarUsuariosDesdeBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";
        ObservableList<String[]> usuarios = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT id, nombre, correo, trabajador FROM usuarios";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String id = resultSet.getString("id");
                        String nombre = resultSet.getString("nombre");
                        String correo = resultSet.getString("correo");
                        String trabajador = resultSet.getBoolean("trabajador") ? "Sí" : "No";
                        usuarios.add(new String[]{id, nombre, correo, trabajador});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableViewUsuarios.setItems(usuarios);
    }

    /**
     * Maneja el evento de eliminación de usuario.
     * Elimina el usuario seleccionado de la base de datos junto con sus pedidos y detalles de pedidos asociados.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleEliminarUsuario(ActionEvent event) {
        String[] usuarioSeleccionado = tableViewUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un usuario para eliminar.");
            return;
        }

        String idUsuario = usuarioSeleccionado[0];

        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String selectPedidosQuery = "SELECT idPedido FROM pedidos WHERE idUsuario = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectPedidosQuery)) {
                selectStatement.setString(1, idUsuario);
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String idPedido = resultSet.getString("idPedido");

                        String deleteDetallePedidosQuery = "DELETE FROM detallepedidos WHERE idPedido = ?";
                        try (PreparedStatement deleteDetalleStatement = connection.prepareStatement(deleteDetallePedidosQuery)) {
                            deleteDetalleStatement.setString(1, idPedido);
                            deleteDetalleStatement.executeUpdate();
                        }

                        String deletePedidosQuery = "DELETE FROM pedidos WHERE idPedido = ?";
                        try (PreparedStatement deletePedidoStatement = connection.prepareStatement(deletePedidosQuery)) {
                            deletePedidoStatement.setString(1, idPedido);
                            deletePedidoStatement.executeUpdate();
                        }
                    }
                }
            }

            String deleteUserQuery = "DELETE FROM usuarios WHERE id = ?";
            try (PreparedStatement deleteUserStatement = connection.prepareStatement(deleteUserQuery)) {
                deleteUserStatement.setString(1, idUsuario);
                int rowsDeleted = deleteUserStatement.executeUpdate();

                if (rowsDeleted > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "El usuario ha sido eliminado correctamente.");
                    cargarUsuariosDesdeBaseDeDatos();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el usuario.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al eliminar el usuario.");
        }
    }

    /**
     * Maneja el evento de cierre de sesión.
     * Cierra la ventana actual.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
        stage.close();
    }

    /**
     * Maneja el evento de actualización del estado de trabajador.
     * Cambia el estado de trabajador del usuario seleccionado.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleTrabajador(ActionEvent event) {
        String[] usuarioSeleccionado = tableViewUsuarios.getSelectionModel().getSelectedItem();

        if (usuarioSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un usuario.");
            return;
        }

        String idUsuario = usuarioSeleccionado[0];
        boolean esTrabajador = "Sí".equals(usuarioSeleccionado[3]);

        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String updateQuery = "UPDATE usuarios SET trabajador = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                statement.setBoolean(1, !esTrabajador);
                statement.setString(2, idUsuario);
                int rowsUpdated = statement.executeUpdate();

                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "El estado de trabajador ha sido actualizado.");
                    cargarUsuariosDesdeBaseDeDatos();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el estado de trabajador.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al actualizar el estado de trabajador.");
        }
    }

    /**
     * Maneja el evento de visualización de horarios.
     * Muestra la interfaz de horarios para el usuario seleccionado.
     *
     * @param event El evento de acción.
     */
    @FXML
    private void handleHorarios(ActionEvent event) {
        try {
            String[] usuarioSeleccionado = tableViewUsuarios.getSelectionModel().getSelectedItem();

            if (usuarioSeleccionado == null) {
                showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un usuario.");
                return;
            }

            int idUsuario = Integer.parseInt(usuarioSeleccionado[0]);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/horarios.fxml"));
            Scene scene = new Scene(loader.load());

            ControladorHorarios controladorHorarios = loader.getController();
            controladorHorarios.setUsuarioId(idUsuario);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setTitle("Horarios");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al cargar la escena de horarios.");
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
