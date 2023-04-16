module com.example.messenger_client {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.messenger_client to javafx.fxml;
    exports com.example.messenger_client;
}