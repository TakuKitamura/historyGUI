import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Screen;

import javafx.geometry.Rectangle2D;

public class MessageBoard extends Application {
  @Override
  public void start(Stage primaryStage) {
    Text message = new Text("Hello, world. This is JavaFX.");
    Rectangle2D d = Screen.getPrimary().getVisualBounds();
    Group group = new Group(message);
    group.setLayoutY(50);
    Scene scene = new Scene(group, d.getWidth(), d.getHeight());
    primaryStage.setScene(scene);
    primaryStage.setTitle("History GUI");
    primaryStage.setFullScreen(true);
    primaryStage.show();
  }
}
