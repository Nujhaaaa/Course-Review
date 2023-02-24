package edu.virginia.cs.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ReviewsApplication extends Application {
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ReviewsApplication.class.getResource("/edu.virginia.cs.gui/reviews-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 650);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}


