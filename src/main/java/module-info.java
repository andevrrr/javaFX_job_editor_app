module com.example.firstweek {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.firstweek to javafx.fxml;
    exports com.example.firstweek;
}