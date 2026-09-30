module org.ezone.pae.ejercicioconexionbd {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    exports org.ezone.pae.ejercicioconexionbd to javafx.graphics;

    opens org.ezone.pae.ejercicioconexionbd to javafx.fxml;
    opens org.ezone.pae.ejercicioconexionbd.controller to javafx.fxml;
    opens org.ezone.pae.ejercicioconexionbd.model to javafx.base;
}