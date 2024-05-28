package montaditos;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicación 100 Montaditos.
 * Configura y muestra la ventana principal de la aplicación.
 * @version 1.0.
 */
public class App extends Application {

    /**
     * Método de inicio de la aplicación.
     * Carga la interfaz gráfica desde un archivo FXML y establece el ícono de la ventana principal.
     *
     * @param primaryStage El escenario principal de la aplicación.
     */
    @Override
    public void start(Stage primaryStage) {
        // Establece el ícono de la aplicación en la ventana principal
        primaryStage.getIcons().add(new Image("file:src/main/resources/img/logo.jpg"));

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/montaditos/primary.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("100 Montaditos");
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Método principal que lanza la aplicación.
     *
     * @param args Los argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
