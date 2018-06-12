import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;

import javafx.geometry.Rectangle2D;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxListCell;
import javafx.scene.layout.StackPane;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import java.nio.file.Paths;
import java.util.List;
import java.io.IOException;

public class MessageBoard extends Application {
  @Override
  public void start(Stage primaryStage) {

    ObservableList<String> items = FXCollections.observableArrayList();
    try {
      items = FXCollections.observableArrayList(Files.readAllLines(Paths.get("/Users/kitamurataku/.bash_history"), StandardCharsets.UTF_8));
    } catch (IOException e) {
      e.printStackTrace();
    }

    ListView<String> list = new ListView<>();
    list.setItems(items);
    list.setPrefWidth(100);
    list.setPrefHeight(70);
    Rectangle2D d = Screen.getPrimary().getVisualBounds();

    StackPane root = new StackPane();
    root.getChildren().add(list);
    primaryStage.setScene(new Scene(root, d.getWidth(), d.getHeight()));
    primaryStage.setTitle("History GUI");
    primaryStage.setFullScreen(true);
    primaryStage.show();
  }
}
