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
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controlador de la interfaz de usuario para gestionar pedidos.
 * @version 1.0.
 */
public class ControladorPedidos {

    @FXML
    private TableView<String[]> tableViewPedidos;

    @FXML
    private TableColumn<String[], String> colPedido;

    @FXML
    private TableColumn<String[], String> colCliente;

    @FXML
    private TableColumn<String[], String> colProducto;

    @FXML
    private TableColumn<String[], String> colCantidad;

    @FXML
    private TableColumn<String[], String> colEstado;

    /**
     * Inicializa el controlador después de que su contenido ha sido completamente procesado.
     */
    @FXML
    private void initialize() {
        // Configurar las columnas de la tabla
        colPedido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0]));
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1]));
        colProducto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2]));
        colCantidad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3]));
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4]));

        // Cargar los pedidos desde la base de datos
        cargarPedidosDesdeBaseDeDatos();
    }

    /**
     * Carga los pedidos desde la base de datos y los muestra en la tabla.
     */
    private void cargarPedidosDesdeBaseDeDatos() {
        // Detalles de la conexión a la base de datos
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        ObservableList<String[]> pedidos = FXCollections.observableArrayList();

        // Conexión a la base de datos
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Consulta para obtener los pedidos en proceso
            String query = "SELECT dp.idPedido , u.nombre , dp.idProducto , dp.cantidad , p.estado  " +
                    "FROM detallepedidos dp " +
                    "JOIN pedidos p ON dp.idPedido = p.idPedido " +
                    "JOIN usuarios u ON p.ids = u.id " +
                    "WHERE p.estado = 'En proceso'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Recorrer los resultados y añadirlos a la lista de pedidos
                    while (resultSet.next()) {
                        String pedido = resultSet.getString("idPedido");
                        String cliente = resultSet.getString("nombre");
                        String producto = resultSet.getString("idProducto");
                        String cantidad = resultSet.getString("cantidad");
                        String estado = resultSet.getString("estado");
                        pedidos.add(new String[]{pedido, cliente, producto, cantidad, estado});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Mostrar los pedidos en la tabla
        tableViewPedidos.setItems(pedidos);
    }

    /**
     * Cambia a la escena de trabajador.
     */
    @FXML
    private void goToTrabajador(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/trabajador.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.setTitle("Trabajador");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar trabajador.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de trabajador.");
        }
    }

    /**
     * Muestra una alerta con el tipo, título y mensaje especificados.
     */
    @FXML
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Finaliza el pedido seleccionado en la tabla y actualiza su estado en la base de datos.
     */
    @FXML
    private void finalizarPedido(ActionEvent event) {
        // Obtener el pedido seleccionado en la tabla
        String[] pedidoSeleccionado = tableViewPedidos.getSelectionModel().getSelectedItem();

        // Verificar si se ha seleccionado un pedido
        if (pedidoSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un pedido para finalizar.");
            return;
        }

        // Obtener el ID del pedido seleccionado
        String idPedido = pedidoSeleccionado[0];

        // Detalles de la conexión a la base de datos
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        // Conexión a la base de datos
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            // Consulta para actualizar el estado del pedido
            String updateQuery = "UPDATE pedidos SET estado = ? WHERE idPedido = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                // Establecer el nuevo estado del pedido como "Finalizado"
                statement.setString(1, "Finalizado");
                statement.setString(2, idPedido);

                // Ejecutar la actualización
                int rowsUpdated = statement.executeUpdate();

                // Verificar si se ha actualizado correctamente
                if (rowsUpdated > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Éxito", "El pedido se ha finalizado correctamente.");
                    // Actualizar la tabla de pedidos
                    cargarPedidosDesdeBaseDeDatos();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "No se pudo finalizar el pedido.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Error al finalizar el pedido.");
        }
    }
}
