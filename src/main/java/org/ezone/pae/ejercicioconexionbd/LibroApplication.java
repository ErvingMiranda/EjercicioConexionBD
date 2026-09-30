package org.ezone.pae.ejercicioconexionbd;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LibroApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LibroApplication.class.getResource("libro-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Registro de Libros");
        stage.setScene(scene);
        stage.show();
    }
}
