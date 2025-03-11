package mp3.dam.elpuig.pokeFX;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import mp3.dam.elpuig.pokeFX.control.MainWindow;

import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("fxml/MainWindow.fxml"));
        Parent root = loader.load();
        primaryStage.setTitle("PokéFX - Estadísticas Pokémon");
        Scene scene = new Scene(root);

        MainWindow mainWindow = loader.getController();
        mainWindow.setStage(primaryStage);
        mainWindow.setScene(scene);

        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 