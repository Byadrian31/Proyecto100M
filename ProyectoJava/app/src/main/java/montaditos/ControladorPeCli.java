package montaditos;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

public class ControladorPeCli {

    @FXML
    private TableView<Producto> tableViewProductos;

    @FXML
    private TableColumn<Producto, String> colNombreProd;

    @FXML
    private TableColumn<Producto, String> colDescripcionProd;

    @FXML
    private TableColumn<Producto, Double> colPrecioProd;

    @FXML
    private TableView<Producto> tableViewPedido;

    @FXML
    private TableColumn<Producto, String> colNombrePed;

    @FXML
    private TableColumn<Producto, Double> colPrecioPed;

    @FXML
    private Button btnAnadir;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnEnviar;

    @FXML
    private TextField txtCantidad;

    @FXML
    private Label lblPrecio;

    @FXML
    private Label lblTotalPedido;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnEuromania;

    // Inicialización del controlador
    public void initialize() {
        colNombreProd.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcionProd.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecioProd.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        colNombrePed.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecioPed.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());

        loadData("SELECT idProducto, nombre, descripcion, precio FROM producto");
        actualizarEstadoEuromania(); // Verificar si se debe habilitar o deshabilitar el botón Euromania
    }

    private void actualizarEstadoEuromania() {
        DayOfWeek diaActual = LocalDate.now().getDayOfWeek();
        if (diaActual == DayOfWeek.WEDNESDAY || diaActual == DayOfWeek.SUNDAY) {
            btnEuromania.setDisable(false);
        } else {
            btnEuromania.setDisable(true);
        }
    }

    private void loadData(String query) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        ObservableList<Producto> productos = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            while (resultSet.next()) {
                productos.add(new Producto(
                        resultSet.getInt("idProducto"),
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("precio")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableViewProductos.setItems(productos);
    }

    @FXML
    private void handleAnadir(ActionEvent event) {
        Producto productoSeleccionado = tableViewProductos.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            String cantidadString = txtCantidad.getText();
            if (!cantidadString.isEmpty()) {
                int cantidad = Integer.parseInt(cantidadString);
                boolean productoExistente = false;

                // Buscar el producto en la lista y actualizar cantidad y precio
                for (Producto p : tableViewPedido.getItems()) {
                    if (p.getIdProducto() == productoSeleccionado.getIdProducto()) {
                        p.setCantidad(p.getCantidad() + cantidad);
                        p.setPrecio(p.getPrecio() + (productoSeleccionado.getPrecio() * cantidad));
                        productoExistente = true;
                        break;
                    }
                }

                // Si no existe, agregarlo al pedido
                if (!productoExistente) {
                    Producto pedido = new Producto(productoSeleccionado.getIdProducto(),
                            productoSeleccionado.getNombre(),
                            productoSeleccionado.getDescripcion(),
                            productoSeleccionado.getPrecio() * cantidad,
                            cantidad);
                    tableViewPedido.getItems().add(pedido);
                }

                tableViewPedido.refresh(); // Refrescar la tabla para mostrar cambios
                actualizarPrecioTotalPedido();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Debe ingresar la cantidad.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Debe seleccionar un producto.");
        }
    }

    @FXML
    private void handleEliminar(ActionEvent event) {
        Producto productoSeleccionado = tableViewPedido.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            String cantidadString = txtCantidad.getText();
            if (!cantidadString.isEmpty()) {
                int cantidad = Integer.parseInt(cantidadString);
                if (cantidad == 0 || productoSeleccionado.getCantidad() <= cantidad) {
                    tableViewPedido.getItems().remove(productoSeleccionado);
                } else {
                    double precioUnitario = productoSeleccionado.getPrecio() / productoSeleccionado.getCantidad();
                    double precioRestar = precioUnitario * cantidad;
                    productoSeleccionado.setCantidad(productoSeleccionado.getCantidad() - cantidad);
                    productoSeleccionado.setPrecio(productoSeleccionado.getPrecio() - precioRestar);
                }

                tableViewPedido.refresh(); // Refrescar la tabla para mostrar cambios
                actualizarPrecioTotalPedido();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Debe ingresar la cantidad.");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Debe seleccionar un producto para eliminar.");
        }
    }



    @FXML
    private void handleEnviar(ActionEvent event) {
        int userId = SessionManager.getInstance().getUserId();

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmación");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("¿Está seguro de enviar el pedido?");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            insertarPedido(userId);
        }
    }

    private void insertarPedido(int userId) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String insertPedidoQuery = "INSERT INTO pedidos (idUsuario, estado) VALUES (?, ?)";
            try (PreparedStatement insertPedidoStatement = connection.prepareStatement(insertPedidoQuery, Statement.RETURN_GENERATED_KEYS)) {
                insertPedidoStatement.setInt(1, userId);
                insertPedidoStatement.setString(2, "En proceso");
                insertPedidoStatement.executeUpdate();

                ResultSet generatedKeys = insertPedidoStatement.getGeneratedKeys();
                int pedidoId = 0;
                if (generatedKeys.next()) {
                    pedidoId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID del pedido.");
                }

                String insertDetalleQuery = "INSERT INTO detallepedidos (idPedido, idProducto, cantidad, precio) VALUES (?, ?, ?, ?)";
                try (PreparedStatement insertDetalleStatement = connection.prepareStatement(insertDetalleQuery)) {
                    for (Producto producto : tableViewPedido.getItems()) {
                        insertDetalleStatement.setInt(1, pedidoId);
                        insertDetalleStatement.setInt(2, producto.getIdProducto());
                        insertDetalleStatement.setInt(3, producto.getCantidad());
                        insertDetalleStatement.setDouble(4, producto.getPrecio() / producto.getCantidad());
                        insertDetalleStatement.executeUpdate();
                    }
                }

                showAlert(Alert.AlertType.CONFIRMATION, "Éxito", "Pedido enviado correctamente.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Ocurrió un error al enviar el pedido.");
        }
    }

    @FXML
    private void mostrarCarta(ActionEvent event) {
        loadData("SELECT idProducto, nombre, descripcion, precio FROM producto");
    }

    @FXML
    private void mostrarEuromania(ActionEvent event) {
        loadData("SELECT idProducto, nombre, descripcion, precio_eur AS precio FROM producto");
    }

    @FXML
    private void buscarProducto() {
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            loadData("SELECT idProducto, nombre, descripcion, precio FROM producto");
        } else {
            loadData("SELECT idProducto, nombre, descripcion, precio FROM producto WHERE descripcion LIKE '%" + searchText + "%' OR nombre LIKE '%" + searchText + "%'");
        }
    }

    private void actualizarPrecioTotalPedido() {
        double total = 0;
        for (Producto producto : tableViewPedido.getItems()) {
            total += producto.getPrecio();
        }
        lblTotalPedido.setText(String.format("Total: %.2f €", total));
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        int userId = SessionManager.getInstance().getUserId();

        if (esTrabajador(userId)) {
            // Si el usuario es un trabajador, redirigir a trabajador.fxml
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
        } else {
            // Si el usuario no es un trabajador, redirigir a carta.fxml
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
    }


    private boolean esTrabajador(int userId) {
        String url = "jdbc:mysql://localhost:3306/montaditos";
        String username = "root";
        String password = "";

        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            String query = "SELECT trabajador FROM usuarios WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean("trabajador");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false; // Si no se encuentra el usuario o hay un error, asumimos que no es trabajador
    }

}
