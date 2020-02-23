package calculator;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public abstract class EditorWindow extends GridPane implements SubWindow {
    @FXML
    protected Button add;

    @FXML
    protected Button delete;

    @FXML
    protected GridPane infoPane;

    @FXML
    protected ObservableList<String> items;

    @FXML
    protected ListView<String> selector;

    public EditorWindow(String resource) {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(resource));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        final var selectionModel = selector.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            updateInformationBase(newValue);
        }));
    }

    @Override
    public Scene buildScene() {
        return new Scene(this);
    }

    @Override
    public double getWindowWidth() {
        return Configuration.SCREEN_WIDTH * 0.8;
    }

    @Override
    public double getWindowHeight() {
        return Configuration.SCREEN_HEIGHT * 0.8;
    }

    protected abstract void updateInformation(String selection);

    private void updateInformationBase(String selection) {
        infoPane.setVisible(selection != null);

        if (selection != null) {
            updateInformation(selection);
        }
    }
}
