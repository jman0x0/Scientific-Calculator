package calculator;

import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Scene;

public interface SubWindow {
    default void display(Stage parent) {
        final Stage stage = buildStage(parent);
        final Scene scene = buildScene();
        Configuration.STAGE_STACK.push(stage);
        stage.setTitle(getTitle());
        stage.setScene(scene);
        stage.showAndWait();
        Configuration.STAGE_STACK.pop();
    }

    private Stage buildStage(Stage parent) {
        final Stage stage = new Stage();
        final double width = getWindowWidth();
        final double height = getWindowHeight();

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(parent);
        stage.setWidth(width);
        stage.setHeight(height);
        stage.getIcons().addAll(parent.getIcons());

        return stage;
    }

    Scene buildScene();

    String getTitle();

    double getWindowWidth();

    double getWindowHeight();
}
