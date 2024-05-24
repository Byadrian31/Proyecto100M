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

    // Método para cargar los pedidos desde la base de datos
    @FXML
    private void initialize() {
        // Configurar columnas
        colPedido.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[0])); // Índice 0 para el pedido
        colCliente.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[1])); // Índice 1 para el cliente
        colProducto.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[2])); // Índice 2 para el producto
        colCantidad.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[3])); // Índice 3 para la cantidad
        colEstado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue()[4])); // Índice 4 para el estado

        // Cargar datos de la base de datos
        cargarPedidosDesdeBaseDeDatos();
    }

    private void cargarPedidosDesdeBaseDeDatos() {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        ObservableList<String[]> pedidos = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT dp.idPedido , u.nombre , dp.idProducto , dp.cantidad , p.estado  " +
                    "FROM detallepedidos dp " +
                    "JOIN pedidos p ON dp.idPedido = p.idPedido " +
                    "JOIN usuarios u ON p.idUsuario = u.id " +
                    "WHERE p.estado = 'En proceso'";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        String cliente = resultSet.getString("nombre");
                        String producto = resultSet.getString("idProducto");
                        String cantidad = resultSet.getString("cantidad");
                        String estado = resultSet.getString("estado");
                        String pedido = resultSet.getString("idPedido");
                        pedidos.add(new String[]{pedido, cliente, producto, cantidad, estado});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Asignar los pedidos a la tabla
        tableViewPedidos.setItems(pedidos);
    }

    @FXML
    private void goToTrabajador(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/trabajador.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar trabajador.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de trabajador.");
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

    @FXML
    private void finalizarPedido(ActionEvent event) {
        // Obtener el pedido seleccionado en la tabla
        String[] pedidoSeleccionado = tableViewPedidos.getSelectionModel().getSelectedItem();

        if (pedidoSeleccionado == null) {
            showAlert(Alert.AlertType.WARNING, "Advertencia", "Por favor, seleccione un pedido para finalizar.");
            return;
        }

        // Obtener el ID del pedido seleccionado
        String idPedido = pedidoSeleccionado[0];

        // Actualizar el estado del pedido en la base de datos
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String updateQuery = "UPDATE pedidos SET estado = ? WHERE idPedido = ?";
            try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                // Establecer el nuevo estado del pedido como "Finalizado"
                statement.setString(1, "Finalizado");
                statement.setString(2, idPedido);

                // Ejecutar la actualización
                int rowsUpdated = statement.executeUpdate();

                // Verificar si se actualizó correctamente
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
