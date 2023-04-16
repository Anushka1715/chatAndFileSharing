module com.example.messenger_server {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.messenger_server to javafx.fxml;
    exports com.example.messenger_server;
}