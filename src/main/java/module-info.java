module com.example.resumegen {
    requires javafx.fxml;
    requires org.apache.pdfbox;
    requires org.json;
    requires jbcrypt;
    requires org.controlsfx.controls;
    requires java.desktop;



    opens com.example.resumegen to javafx.fxml;
    exports com.example.resumegen;
}