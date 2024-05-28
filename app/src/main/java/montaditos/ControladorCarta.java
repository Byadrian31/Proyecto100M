package montaditos;

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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

/**
 * Controlador para la interfaz de la carta de productos en la aplicación 100 Montaditos.
 * Permite filtrar, buscar y gestionar la vista de productos.
 * @version 1.0.
 */
public class ControladorCarta {

    @FXML
    private TableView<Producto> tableView;

    @FXML
    private TableColumn<Producto, String> colNombre;

    @FXML
    private TableColumn<Producto, String> colDescripcion;

    @FXML
    private TableColumn<Producto, Double> colPrecio;

    @FXML
    private TextField searchField;

    @FXML
    private Button CS;

    @FXML
    private Button Volver;

    /**
     * Inicializa la tabla de productos y configura la visibilidad de botones según el rol del usuario.
     */
    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        loadData("SELECT nombre, descripcion, precio FROM producto");
        int idUsuario = SessionManager.getInstance().getUserId();
        boolean esTrabajador = esTrabajador(idUsuario);

        if (esTrabajador) {
            Volver.setVisible(true);
            CS.setVisible(false);
        } else {
            Volver.setVisible(false);
            CS.setVisible(true);
        }
    }

    /**
     * Carga los datos de los productos desde la base de datos según la consulta especificada.
     *
     * @param query La consulta SQL para obtener los productos.
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
                        resultSet.getString("nombre"),
                        resultSet.getString("descripcion"),
                        resultSet.getDouble("precio")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(productos);
    }

    /**
     * Filtro para mostrar Montaditos.
     */
    @FXML
    private void filterMontaditos() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'montaditos'");
    }

    /**
     * Filtro para mostrar Bebida.
     */
    @FXML
    private void filterBebida() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'bebida'");
    }

    /**
     * Filtro para mostrar Postres.
     */
    @FXML
    private void filterPostres() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'postres'");
    }

    /**
     * Filtro para mostrar Ensaladas.
     */
    @FXML
    private void filterEnsalada() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'ensaladas'");
    }

    /**
     * Filtro para mostrar tablas.
     */
    @FXML
    private void filterTabla() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'tablas'");
    }

    /**
     * Filtro para mostrar Promociones(Euromania).
     */
    @FXML
    private void filterPromociones() {
        loadData("SELECT nombre, descripcion, precio_eur AS precio FROM producto");
    }

    /**
     * Método para buscar los productos por descripción y/o nombre.
     */
    @FXML
    private void searchByDescription() {
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            loadData("SELECT nombre, descripcion, precio FROM producto");
        } else {
            loadData("SELECT nombre, descripcion, precio FROM producto WHERE descripcion LIKE '%" + searchText + "%' OR nombre LIKE '%" + searchText + "%'");
        }
    }

    /**
     * Quitar todos los filtros.
     */
    @FXML
    private void clearFilters() {
        searchField.clear();
        loadData("SELECT nombre, descripcion, precio FROM producto");
    }

    /**
     * Cerrar Sesión.
     * @param event El evento de acción.
     */
    @FXML
    private void handleCS(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
    }

    /**
     * Volver a trabajador.fxml.
     * @param event El evento de acción.
     */
    @FXML
    private void handleVolver(ActionEvent event) {
        int userId = SessionManager.getInstance().getUserId();

        if (esTrabajador(userId)) {
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
    }

    /**
     * Ir a pedidoscli.fxml.
     * @param event El evento de acción.
     */
    @FXML
    private void goToPedidosCli(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/pedidoscli.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.setTitle("Realizar Pedidos");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar pedidoscli.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidos.");
        }
    }

    /**
     * Muestra una alerta en pantalla.
     *
     * @param alertType El tipo de alerta.
     * @param title     El título de la alerta.
     * @param message   El mensaje de la alerta.
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
     * Verifica si un usuario es trabajador.
     *
     * @param userId El ID del usuario.
     * @return true si el usuario es trabajador, false en caso contrario.
     */
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
