package montaditos;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Controlador para la ventana de inicio.
 * @version 1.0.
 */
public class ControladorInicio {

    /**
     * Abre la ventana de registro.
     *
     * @param event Evento de acción
     */
    @FXML
    private void handleRegistro(ActionEvent event) {
        openPopup("registro.fxml", "Registro", event);
    }

    /**
     * Abre la ventana de acceso.
     *
     * @param event Evento de acción
     */
    @FXML
    private void handleAcceso(ActionEvent event) {
        openPopup("login.fxml", "Acceso", event);
    }

    /**
     * Abre una ventana emergente.
     *
     * @param fxmlFile Archivo FXML de la ventana
     * @param title    Título de la ventana
     * @param event    Evento de acción que desencadenó la apertura de la ventana
     */
    private void openPopup(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(((Button) event.getSource()).getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
