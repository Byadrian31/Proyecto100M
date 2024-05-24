module montaditos {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens montaditos to javafx.fxml;
    exports montaditos;
}
