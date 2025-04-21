module com.example.resumegen {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.resumegen to javafx.fxml;
    exports com.example.resumegen;
}