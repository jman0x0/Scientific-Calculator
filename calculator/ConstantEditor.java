package calculator;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class ConstantEditor extends GridPane implements SubWindow {
    @FXML
    private Button add;

    @FXML
    private Button delete;

    @FXML
    private ObservableList<String> constantList;

    @FXML
    private ListView<String> constantSelector;

    @FXML
    private GridPane infoPane;

    @FXML
    private TextField identifierField;

    @FXML
    private TextField conversionsField;

    @FXML
    private TextField valueField;

    @FXML
    public void initialize() {
        updateConstants();

        final var selectionModel = constantSelector.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(((observableValue, oldValue, newValue) -> {
            updateInformation(newValue);
        }));
        constantSelector.focusedProperty().addListener(((observableValue, aBoolean, t1) -> {
            if (!t1) {
                System.out.println("Constants");
            }
        }));
        identifierField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final var model = constantSelector.getSelectionModel();
                final int selection = model.getSelectedIndex();
                if (selection >= 0) {
                    final String oldIdentifier = model.getSelectedItem();
                    final String identifier = identifierField.getText();

                    if (!identifier.equals(oldIdentifier)) {
                        final Double value = Constants.JCONSTANTS.remove(oldIdentifier);;
                        final int shadowed = constantList.indexOf(identifier);
                        if (shadowed >= 0) {
                            constantList.remove(shadowed);
                        }
                        Constants.JCONSTANTS.put(identifier, value);
                        constantList.set(selection, identifier);
                    }
                }
            }
        }));
        valueField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final var model = constantSelector.getSelectionModel();
                final int selection = model.getSelectedIndex();
                if (selection >= 0) {
                    final String identifier = identifierField.getText();
                    final Double newValue;
                    try {
                        newValue = Double.parseDouble(valueField.getText());
                    } catch (Exception exception) {
                        valueField.setText(Constants.JCONSTANTS.get(identifier).toString());
                        return;
                    }
                    Constants.JCONSTANTS.put(identifier, newValue);
                }
            }
        }));
    }

    public ConstantEditor() {
        final String pathway = "calculator_constants.fxml";
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(pathway));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public Scene buildScene() {
        return new Scene(this);
    }

    @Override
    public String getTitle() {
        return "Calculator - Constants";
    }

    @Override
    public double getWindowWidth() {
        return Configuration.ASPECT_X * 40;
    }

    @Override
    public double getWindowHeight() {
        return Configuration.ASPECT_Y * 40;
    }

    @FXML
    private void onAddAction(ActionEvent action) {
        final CreateConstant creator = new CreateConstant();

        creator.display(Configuration.STAGE_STACK.peek());
        updateConstants();
    }

    @FXML
    private void onDeleteAction(ActionEvent action) {
        final var selectionModel = constantSelector.getSelectionModel();
        final int selection = selectionModel.getSelectedIndex();

        if (selection >= 0) {
            Constants.JCONSTANTS.remove(selectionModel.getSelectedItem());
            constantList.remove(selection);
        }
    }

    private void updateConstants() {
        for (var value : Constants.JCONSTANTS.entrySet()) {
            if (!constantList.contains(value.getKey())) {
                constantList.add(value.getKey());
            }
        }
    }

    private void updateInformation(String identifier) {
        if (identifier == null) {
            infoPane.setVisible(false);
            return;
        }

        if (!infoPane.isVisible()) {
            infoPane.setVisible(true);
        }

        final Double value = Constants.JCONSTANTS.get(identifier);
        identifierField.setText(identifier);
        valueField.setText(String.valueOf(value));
    }

}
