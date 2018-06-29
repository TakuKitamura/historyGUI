import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Pos;
import java.lang.ProcessBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import javafx.scene.layout.HBox;
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
        "覚えるのが大変順",
        "覚えるのが簡単順",
        "新しい順",
        "高機能なコマンド順",
        "利用可能なコマンド順",
        "コマンド履歴"
      );

    ListView<String> category = new ListView<>(categories);
    category.setPrefSize(150, 1000);

    category.setOnMouseClicked((mouseEvent) -> {
       String categoryName = category.getSelectionModel().getSelectedItem().toString();
       System.out.println(categoryName);
    });

    Label caregoryLbl = new Label("カテゴリー");
    VBox categorySelection = new VBox();
    categorySelection.setSpacing(10);
    categorySelection.getChildren().addAll(category);

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

    command.setPrefSize(400, 1000);

    TextArea manual = new TextArea();
    command.setOnMouseClicked((mouseEvent) -> {
       String execCommand = "man -P cat " + command.getSelectionModel().getSelectedItem().toString() + " | col -b 2>&1";
       System.out.println(execCommand);

       ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c" , execCommand);
       pb = pb.redirectErrorStream(true);

       try {
          // Runtime runtime = Runtime.getRuntime();
          // runtime.exec(execCommand);
          Process p = pb.start();

          System.out.println(000);

          // p.waitFor();
          System.out.println(pb.redirectInput());
          System.out.println(123);

          try (BufferedReader br = new BufferedReader(
            new InputStreamReader(p.getInputStream(), "UTF-8"))) {
            // ping結果の出力

            StringBuilder sb = new StringBuilder();

            String line;

            System.out.println(456);

            while ((line = br.readLine()) != null) {
                sb.append(line + "\n");
            }

            System.out.println(789);

            String manOutput = sb.toString();

            manual.setText(manOutput);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
    });


    TextField searchBox = new TextField ();
    searchBox.setPromptText("コマンド検索");

    Label commandLbl = new Label("コマンド");
    VBox commandSelection = new VBox();
    commandSelection.setSpacing(10);
    commandSelection.getChildren().addAll(searchBox, command);

    // TextArea manual = new TextArea();
    manual.setText("a\nb");
    manual.setEditable(false);
    manual.setPrefSize(800, 450);
    VBox manualSelection = new VBox();
    manualSelection.setSpacing(10);
    manualSelection.getChildren().addAll(manual);

    TextArea cli = new TextArea();
    cli.setPrefSize(800, 450);
    VBox cliSelection = new VBox();
    cliSelection.setSpacing(10);
    cliSelection.getChildren().addAll(cli);

    // searchBoxSelection.getChildren().add(searchBox);
    VBox rightSelection = new VBox();
    rightSelection.setSpacing(10);
    rightSelection.getChildren().addAll(manual, cli);

    GridPane pane = new GridPane();

    pane.setHgap(10);

    pane.setVgap(5);

    pane.addColumn(0, categorySelection);

    pane.addColumn(1, commandSelection);

    pane.addColumn(2, rightSelection);

    // pane.addColumn(3, commandPathSelection);
    //
    // pane.addColumn(4, manual);
    //
    // pane.addColumn(5, cli);

    // pane.add(manualSelection, 0, 1);
    // pane.add(child, columnIndex, rowIndex);

    // pane.addRow(0, commandPathSelection);


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
