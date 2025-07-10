package com.univsoftdev.econova;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

public class EconovaFx extends Application {

    private static Scene scene;

    @Override
    public void start(@NotNull Stage stage) throws IOException {
        scene = new Scene(loadFXML("com/univsoftdev/econova/econova"), 640, 480);
        scene.getStylesheets().add(loadCSS("com/univsoftdev/econova/style"));
        stage.setScene(scene);
        stage.show();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static String loadCSS(String css) {
        return Objects.requireNonNull(EconovaFx.class.getClassLoader().getResource(css + ".css")).toExternalForm();
    }
    
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(EconovaFx.class.getClassLoader().getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}
