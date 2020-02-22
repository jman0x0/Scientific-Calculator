package calculator;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.MenuButton;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
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

        primaryStage.getScene().addEventFilter(KeyEvent.KEY_PRESSED, (KeyEvent event) -> {
            if (!display.editor.isFocused()) {
                if (!event.isAltDown() && !event.isShiftDown() && !event.isShortcutDown()) {
                    display.editor.requestFocus();
                }
            }
        });

        keypad.listenForKey(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                final ButtonBase button = (ButtonBase)actionEvent.getSource();
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
                    case "2ND":
                        keypad.shiftButtons();
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
        primaryStage.getIcons().addAll(new Image(getClass().getResourceAsStream("icon64x64.png")),
                new Image(getClass().getResourceAsStream("icon48x48.png")));
        primaryStage.setTitle("Calculator");
        primaryStage.setScene(new Scene(root, Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT));
        final Main controller = loader.getController();
        controller.attachListeners(primaryStage);
        Configuration.STAGE_STACK.push(primaryStage);
        primaryStage.show();
        final MenuButton menu = (MenuButton)(primaryStage.getScene().lookup("#display").lookup("#settings"));
        Configuration.listenFor(menu.getItems());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
