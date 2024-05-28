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
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Controlador para la funcionalidad de pedidos de clientes.
 * Este controlador gestiona la interacción del cliente con la interfaz
 * para realizar pedidos, agregar productos al carrito, eliminar productos,
 * enviar el pedido y navegar entre las opciones de menú.
 * @version 1.0.
 */
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

    @FXML
    private Button btnCarta;

    // Inicialización del controlador
    public void initialize() {
        colNombreProd.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcionProd.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecioProd.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());
        colNombrePed.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecioPed.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getPrecio()).asObject());

        loadData("SELECT idProducto, nombre, descripcion, precio FROM producto");
        tableViewProductos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) { // Detectar un clic simple
                Producto productoSeleccionado = tableViewProductos.getSelectionModel().getSelectedItem();
                if (productoSeleccionado != null) {
                    agregarProductoAlPedido(productoSeleccionado);
                }
            }
        });
        actualizarEstadoEuromania(); // Verificar si se debe habilitar o deshabilitar el botón Euromania
    }

    /**
     * Agrega un producto seleccionado al pedido del cliente.
     *
     * @param producto El producto seleccionado por el cliente.
     */
    private void agregarProductoAlPedido(Producto producto) {
        Producto productoEnPedido = buscarProductoEnPedido(producto);
        if (productoEnPedido != null) {
            productoEnPedido.setCantidad(productoEnPedido.getCantidad() + 1);
            productoEnPedido.setPrecio(productoEnPedido.getPrecio() + producto.getPrecio());
        } else {
            Producto nuevoProducto = new Producto(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    1
            );
            tableViewPedido.getItems().add(nuevoProducto);
        }
        tableViewPedido.refresh();
        actualizarPrecioTotalPedido();
    }

    /**
     * Maneja el evento de eliminar un producto del carrito de compra.
     *
     * @param event El evento de acción generado por el botón "Eliminar".
     */
    @FXML
    private void handleEliminar(ActionEvent event) {
        Producto productoSeleccionado = tableViewPedido.getSelectionModel().getSelectedItem();
        if (productoSeleccionado != null) {
            int cantidadEliminar = 1;

            if (productoSeleccionado.getCantidad() > cantidadEliminar) {
                double precioUnitario = productoSeleccionado.getPrecio() / productoSeleccionado.getCantidad();
                productoSeleccionado.setCantidad(productoSeleccionado.getCantidad() - cantidadEliminar);
                productoSeleccionado.setPrecio(productoSeleccionado.getPrecio() - precioUnitario * cantidadEliminar);
            } else {
                tableViewPedido.getItems().remove(productoSeleccionado);
            }

            tableViewPedido.refresh();
            actualizarPrecioTotalPedido();
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", "Debe seleccionar un producto para eliminar.");
        }
    }

    /**

    /**
     * Busca un producto en el pedido.
     *
     * @param producto El producto a buscar.
     * @return El producto si se encuentra en el pedido, null si no.
     */
    private Producto buscarProductoEnPedido(Producto producto) {
        for (Producto p : tableViewPedido.getItems()) {
            if (p.getIdProducto() == producto.getIdProducto()) {
                return p;
            }
        }
        return null;
    }

    /**
     * Actualiza el estado del botón Euromania dependiendo del día de la semana.
     */
    private void actualizarEstadoEuromania() {
        DayOfWeek diaActual = LocalDate.now().getDayOfWeek();
        if (diaActual == DayOfWeek.WEDNESDAY || diaActual == DayOfWeek.SUNDAY) {
            btnEuromania.setDisable(false);
            btnCarta.setDisable(true);
        } else {
            btnEuromania.setDisable(true);
            btnCarta.setDisable(false);
        }
    }

    /**
     * Carga los datos de los productos desde la base de datos y los muestra en la tabla.
     *
     * @param query La consulta SQL para cargar los datos de los productos.
     */
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

    /**
     * Maneja el evento de enviar el pedido.
     *
     * @param event El evento de acción generado por el botón "Enviar".
     */
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

    /**
     * Inserta el pedido en la base de datos.
     *
     * @param userId El ID del usuario que realiza el pedido.
     */
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

    /**
     * Muestra la carta de productos disponibles.
     *
     * @param event El evento de acción generado por el botón "Carta".
     */
    @FXML
    private void mostrarCarta(ActionEvent event) {
        loadData("SELECT idProducto, nombre, descripcion, precio FROM producto");
    }

    /**
     * Muestra los productos en promoción de Euromania.
     *
     * @param event El evento de acción generado por el botón "Euromania".
     */
    @FXML
    private void mostrarEuromania(ActionEvent event) {
        loadData("SELECT idProducto, nombre, descripcion, precio_eur AS precio FROM producto");
    }

    /**
     * Busca productos por nombre o descripción.
     */
    @FXML
    private void buscarProducto() {
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            loadData("SELECT idProducto, nombre, descripcion, precio FROM producto");
        } else {
            loadData("SELECT idProducto, nombre, descripcion, precio FROM producto WHERE descripcion LIKE '%" + searchText + "%' OR nombre LIKE '%" + searchText + "%'");
        }
    }

    /**
     * Actualiza el precio total del pedido en la etiqueta correspondiente.
     */
    private void actualizarPrecioTotalPedido() {
        double total = 0;
        for (Producto producto : tableViewPedido.getItems()) {
            total += producto.getPrecio();
        }
        lblTotalPedido.setText(String.format("Total: %.2f €", total));
    }

    /**
     * Muestra un mensaje de alerta.
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
     * Maneja el evento de volver atrás.
     *
     * @param event El evento de acción generado por el botón "Volver".
     */
    @FXML
    private void handleVolver(ActionEvent event) {
        int userId = SessionManager.getInstance().getUserId();

        if (esTrabajador(userId)) {
            // Si el usuario es un trabajador, redirigir a trabajador.fxml
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/trabajador.fxml"));
                Scene scene = new Scene(loader.load());
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
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
                stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
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

