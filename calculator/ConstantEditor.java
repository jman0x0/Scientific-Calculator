package calculator;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.util.ArrayList;
import java.util.Arrays;

public class ConstantEditor extends EditorWindow {
    @FXML
    private TextField identifierField;

    @FXML
    private TextField conversionsField;

    @FXML
    private TextField valueField;

    @FXML
    public void initialize() {
        updateConstants();

        identifierField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final var model = selector.getSelectionModel();
                final int selection = model.getSelectedIndex();
                if (selection >= 0) {
                    final String oldIdentifier = model.getSelectedItem();
                    final String identifier = identifierField.getText();

                    if (!identifier.equals(oldIdentifier)) {
                        final Double value = Constants.JCONSTANTS.remove(oldIdentifier);;
                        final int shadowed = items.indexOf(identifier);
                        if (shadowed >= 0) {
                            items.remove(shadowed);
                        }
                        Constants.JCONSTANTS.put(identifier, value);
                        items.set(selection, identifier);
                    }
                }
            }
        }));
        conversionsField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final String target = identifierField.getText();
                final String conversions = conversionsField.getText();
                final String trimmed = conversions.substring(1, conversions.length()-1);

                final ArrayList<String> keys = new ArrayList<>(Arrays.asList(trimmed.split(",")));
                final ArrayList<String> stored = KeyConverter.converter.getAllConversions(target);
                stored.removeAll(keys);

                for (String old : stored) {
                    KeyConverter.converter.remove(old);
                }
                for (String proxy : keys) {
                    KeyConverter.converter.put(proxy.trim(), target);
                }
            }
        }));
        valueField.focusedProperty().addListener(((observableValue, v0, focused) -> {
            if (!focused) {
                final var model = selector.getSelectionModel();
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
        super("calculator_constants.fxml");
    }

    @Override
    public String getTitle() {
        return "Calculator - Constants";
    }

    @FXML
    private void onAddAction(ActionEvent action) {
        final CreateConstant creator = new CreateConstant();

        creator.display(Configuration.STAGE_STACK.peek());
        updateConstants();
    }

    @FXML
    private void onDeleteAction(ActionEvent action) {
        final var selectionModel = selector.getSelectionModel();
        final int selection = selectionModel.getSelectedIndex();

        if (selection >= 0) {
            Constants.JCONSTANTS.remove(selectionModel.getSelectedItem());
            items.remove(selection);
        }
    }

    private void updateConstants() {
        for (var value : Constants.JCONSTANTS.entrySet()) {
            if (!items.contains(value.getKey())) {
                items.add(value.getKey());
            }
        }
    }

    @Override
    protected void updateInformation(String identifier) {
        final Double value = Constants.JCONSTANTS.get(identifier);
        identifierField.setText(identifier);
        valueField.setText(String.valueOf(value));
        final var conversions = KeyConverter.converter.getAllConversions(identifier);
        conversionsField.setText(conversions.toString());
    }

}
