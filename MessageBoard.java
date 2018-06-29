import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;

import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;

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
import java.util.stream.Collectors;
import java.util.function.UnaryOperator;
import java.lang.Runtime;


public class MessageBoard extends Application {
  @Override
  public void start(Stage stage) {

    ObservableList<String> categories =
      FXCollections.<String>observableArrayList(
        "利用率順",
        "覚えるの大変順",
        "覚えるの簡単順",
        "新しい順",
        "高機能なコマンド順",
        "利用可能なコマンド順",
        "コマンド履歴"
      );

    ListView<String> category = new ListView<>(categories);

    category.setOnMouseClicked((mouseEvent) -> {
       String categoryName = category.getSelectionModel().getSelectedItem().toString();
       System.out.println(categoryName);
    });

    Label caregoryLbl = new Label("カテゴリー");
    VBox categorySelection = new VBox();
    categorySelection.setSpacing(10);
    categorySelection.getChildren().addAll(caregoryLbl, category);

    ObservableList<String> commands = FXCollections.observableArrayList();
    try {
      String homePath = System.getProperty("user.home");
      commands = FXCollections.observableArrayList(
        Files.readAllLines(
          Paths.get(homePath + "/.bash_history"),
          StandardCharsets.UTF_8
        )
        .stream()
        .distinct()
        .collect(Collectors.toList())
      );
    } catch (IOException e) {
      e.printStackTrace();
    }

    UnaryOperator<String> unary = string -> string
      .replaceAll("#.*", "")
      .replaceAll("^ *| *$", "")
      .replaceAll(" {2,}", " ");
    commands.replaceAll(unary);

    ListView<String> command = new ListView<>(commands);

    command.setPrefSize(200, 1000);
    command.setOnMouseClicked((mouseEvent) -> {
       String execCommand = command.getSelectionModel().getSelectedItem().toString();
       System.out.println(execCommand);
       // try {
       //    Runtime runtime = Runtime.getRuntime();
       //    runtime.exec(execCommand);
       //  } catch (IOException e) {
       //    e.printStackTrace();
       //  }
    });


    Label commandLbl = new Label("コマンド");
    VBox commandSelection = new VBox();
    commandSelection.setSpacing(10);
    commandSelection.getChildren().addAll(commandLbl, command);

    GridPane pane = new GridPane();

    pane.setHgap(10);

    pane.setVgap(5);

    pane.addColumn(0, categorySelection);

    pane.addColumn(1, commandSelection);


    pane.setStyle("-fx-padding: 10;" +
        "-fx-border-style: solid inside;" +
        "-fx-border-width: 2;" +
        "-fx-border-insets: 5;" +
        "-fx-border-radius: 5;" +
        "-fx-border-color: blue;");

    Scene scene = new Scene(pane);

    stage.setScene(scene);
    stage.setTitle("History GUI");
    stage.setFullScreen(true);
    stage.show();
  }
}
