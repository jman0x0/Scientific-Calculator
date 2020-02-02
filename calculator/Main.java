package calculator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.beans.value.ChangeListener;


public class Main extends Application {
    @FXML
    public Keypad keypad;

    @FXML
    public Display display;

    @FXML
    public void initialize() {

    }

    public void attachListeners(Stage primaryStage) {
        final ChangeListener<Number> dimensionChange = ((observableValue, oldHeight, newHeight) -> {
            final double width = primaryStage.getWidth();
            final double height = primaryStage.getHeight();
            display.scaleTo(width, height);
        });
        primaryStage.heightProperty().addListener(dimensionChange);
        primaryStage.widthProperty().addListener(dimensionChange);

        keypad.listenForKey(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                final Button button = (Button)actionEvent.getSource();
                final String command = button.getText();

                switch (command) {
                    case "1/x":
                        display.updateDisplay(1.0/display.getAnswer());
                        break;
                    case "x^2":
                        display.insertText("^2");
                        break;
                    case "√x":
                        display.insertText("√");
                        break;
                    default:
                        display.processCommand(command);
                        break;
                }
            }
        });
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        final FXMLLoader loader = new FXMLLoader(getClass().getResource("calculator_main.fxml"));
        final Parent root = loader.load();
        //Load and set the window's icon.
        primaryStage.getIcons().addAll(new Image(getClass().getResourceAsStream("../media/icon64x64.png")),
                                       new Image(getClass().getResourceAsStream("../media/icon48x48.png")));
        final Main controller = loader.getController();
        controller.attachListeners(primaryStage);

        primaryStage.setTitle("Calculator");
        primaryStage.setScene(new Scene(root, Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
