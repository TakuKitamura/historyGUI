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
import javafx.collections.transformation.FilteredList;

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



    ObservableList<String> commands = FXCollections.observableArrayList();
    try {
      String homePath = System.getProperty("user.home");
      commands = FXCollections.observableArrayList(
        Files.readAllLines(
          Paths.get(homePath + "/work/historyGUI/dict.txt"),
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

    FilteredList<String> filteredData = new FilteredList<>(commands, s -> true);


    TextField searchBox = new TextField ();

    searchBox.textProperty().addListener(obs-> {
          String searchText = searchBox.getText();
          if(searchText == null || searchText.length() == 0) {
            filteredData.setPredicate(s -> true);
          } else {
            filteredData.setPredicate(s -> s.contains(searchText));
          }
    });


    ListView<String> command = new ListView<>(filteredData);

    // searchBox.setOnKeyPressed(
    // (event) -> {
    //   if(event.getCode() == KeyCode.ENTER) {
    //
    //   }
    // });

    TextArea manual = new TextArea();
    TextArea cli = new TextArea();

    command.setOnMouseClicked((mouseEvent) -> {
      String selectCommand = command.getSelectionModel().getSelectedItem().toString();
      String execCommand = "man -P cat " + selectCommand + " | col -b 2>&1";

      cli.setText("$ " + selectCommand);

      ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c" , execCommand);

      String manOutput = getCommandInput(pb);

      manual.setText(manOutput);
    });

    searchBox.setPromptText("コマンド検索");

    Label commandLbl = new Label("コマンド");
    VBox commandSelection = new VBox();
    commandSelection.setSpacing(10);
    commandSelection.getChildren().addAll(searchBox, command);
    commandSelection.setPrefSize(800, 450);

    manual.setText("試すコマンドを選択してください。");
    manual.setEditable(false);
    manual.setPrefSize(800, 450);
    HBox manualSelection = new HBox();
    manualSelection.setSpacing(10);
    manualSelection.getChildren().addAll(manual);

    cli.setPrefSize(2000, 450);
    cli.setText("$ ");
    HBox cliSelection = new HBox();
    cliSelection.setSpacing(10);

    cli.setOnKeyPressed(
    (event) -> {
      if(event.getCode() == KeyCode.ENTER) {

        String selectCommand = command.getSelectionModel().getSelectedItem();

          String[] lineAry = cli.getText().split("\\n");
          String lastCommand = lineAry[lineAry.length-1];
          System.out.println(lastCommand);
          String execCommand = lastCommand.replaceAll("^\\$ ", "");
          System.out.println(execCommand);
          ProcessBuilder pb = new ProcessBuilder("/bin/sh", "-c" , execCommand);
          String output = "\n" + getCommandInput(pb);
          cli.appendText(output);

        if(selectCommand == null) {
           cli.appendText("$ ");
        } else {
          cli.appendText("$ " + selectCommand);
        }
         event.consume();
      }
    });

    VBox rightSelection = new VBox();
    rightSelection.setSpacing(10);

    GridPane pane = new GridPane();

    pane.setHgap(10);

    pane.setVgap(5);

    pane.addRow(0, commandSelection);

    pane.addRow(1, manual);

    pane.addRow(2, cli);


    pane.setStyle("-fx-padding: 10;" +
        "-fx-border-style: solid inside;" +
        "-fx-border-width: 2;" +
        "-fx-border-insets: 5;" +
        "-fx-border-radius: 5;" +
        "-fx-border-color: white;");

    Scene scene = new Scene(pane);

    stage.setScene(scene);
    stage.setTitle("History GUI");
    stage.setFullScreen(true);
    stage.show();
  }
}
