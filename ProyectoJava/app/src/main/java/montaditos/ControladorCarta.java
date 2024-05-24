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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

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

    @FXML
    private void filterMontaditos() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'montaditos'");
    }

    @FXML
    private void filterBebida() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'bebida'");
    }

    @FXML
    private void filterPostres() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'postres'");
    }

    @FXML
    private void filterEnsalada() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'ensaladas'");
    }

    @FXML
    private void filterTabla() {
        loadData("SELECT nombre, descripcion, precio FROM producto WHERE tipo = 'tablas'");
    }

    @FXML
    private void filterPromociones() {
        loadData("SELECT nombre, descripcion, precio_eur AS precio FROM producto");
    }

    @FXML
    private void searchByDescription() {
        String searchText = searchField.getText();
        if (searchText.isEmpty()) {
            loadData("SELECT nombre, descripcion, precio FROM producto");
        } else {
            loadData("SELECT nombre, descripcion, precio FROM producto WHERE descripcion LIKE '%" + searchText + "%' OR nombre LIKE '%" + searchText + "%'");
        }
    }

    @FXML
    private void clearFilters() {
        searchField.clear();
        loadData("SELECT nombre, descripcion, precio FROM producto");
    }


    @FXML
    private void handleCS(ActionEvent event) {
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.close();
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
        }
    }


    @FXML
    private void goToPedidosCli(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/pedidoscli.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println("Error al cargar pedidoscli.fxml");
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo cargar la escena de pedidos.");
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
