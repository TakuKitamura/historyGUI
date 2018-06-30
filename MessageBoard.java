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

import javafx.scene.input.KeyCode;

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

  private String getCommandInput(ProcessBuilder pb) {
    try {
      // Runtime runtime = Runtime.getRuntime();
      // runtime.exec(execCommand);
      Process p = pb.start();

      // p.waitFor();
      // System.out.println(pb.redirectInput());
      // System.out.println(123);

      try (BufferedReader br = new BufferedReader(
        new InputStreamReader(p.getInputStream(), "UTF-8"))) {
        // ping結果の出力

        StringBuilder sb = new StringBuilder();

        String line;

        while ((line = br.readLine()) != null) {
          sb.append(line + "\n");
        }

        String input = sb.toString();

        return input;

        // manual.setText(manOutput);
      }
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }

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
       // System.out.println(categoryName);
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

    TextArea manual = new TextArea();
    TextArea cli = new TextArea();

    command.setOnMouseClicked((mouseEvent) -> {
      // selectCommand = command.getSelectionModel().getSelectedItem().toString();
      String selectCommand = command.getSelectionModel().getSelectedItem().toString();
      String execCommand = "man -P cat " + selectCommand + " | col -b 2>&1";
      // System.out.println(execCommand);

      cli.setText("$ " + selectCommand);

      ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c" , execCommand);

      // pb = pb.redirectErrorStream(true);
      //
      // try {
      //   // Runtime runtime = Runtime.getRuntime();
      //   // runtime.exec(execCommand);
      //   Process p = pb.start();

        // p.waitFor();
        // System.out.println(pb.redirectInput());
        // System.out.println(123);

        // try (BufferedReader br = new BufferedReader(
        //   new InputStreamReader(p.getInputStream(), "UTF-8"))) {
        //   // ping結果の出力
        //
        //   StringBuilder sb = new StringBuilder();
        //
        //   String line;
        //
        //   while ((line = br.readLine()) != null) {
        //     sb.append(line + "\n");
        //   }

          // String manOutput = sb.toString();

      //     manual.setText(manOutput);
      //   }
      // } catch (IOException e) {
      //   e.printStackTrace();
      // }

      String manOutput = getCommandInput(pb);

      manual.setText(manOutput);
    });


    TextField searchBox = new TextField ();
    searchBox.setPromptText("コマンド検索");

    Label commandLbl = new Label("コマンド");
    VBox commandSelection = new VBox();
    commandSelection.setSpacing(10);
    commandSelection.getChildren().addAll(searchBox, command);

    // TextArea manual = new TextArea();
    manual.setText("試すコマンドを選択してください。");
    manual.setEditable(false);
    manual.setPrefSize(800, 450);
    VBox manualSelection = new VBox();
    manualSelection.setSpacing(10);
    manualSelection.getChildren().addAll(manual);

    cli.setPrefSize(800, 450);
    cli.setText("$ ");
    VBox cliSelection = new VBox();
    cliSelection.setSpacing(10);
    cliSelection.getChildren().addAll(cli);

    cli.setOnKeyPressed(
    (event) -> {
      if(event.getCode() == KeyCode.ENTER) {
        // System.out.println(command.getSelectionModel().getSelectedItem());

        String selectCommand = command.getSelectionModel().getSelectedItem();

        // for (String line : cli.getText().split("\\n")) {
          String[] lineAry = cli.getText().split("\\n");
          String lastCommand = lineAry[lineAry.length-1];
          System.out.println(lastCommand);
          String execCommand = lastCommand.replaceAll("^\\$ ", "");
          System.out.println(execCommand);
          ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c" , execCommand);
          String output = "\n" + getCommandInput(pb);
          cli.appendText(output);
          // break;
        // }

        if(selectCommand == null) {
          // cli.setEditable(false);
           // cli.setText("試すコマンドを選択してください。");
           cli.appendText("$ ");
           event.consume();
        } else {
          // cli.setEditable(true);
          cli.appendText("$ " + selectCommand);
          event.consume();
          // cli.positionCaret(4);
          // cli.consume();
        }
        // cli.setText("$" + selectCommand);
        // System.out.println(55555);
      }
    });

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
