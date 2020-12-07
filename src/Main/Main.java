package Main;

import Controllers.workspaceController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Main extends Application {

    public static Stage mainStage;
    public Scene mainScene;

    @Override
    public void start(Stage primaryStage) {
        loadMain();
    }

    public void loadMain() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/workspace.fxml"));
            BorderPane pane = loader.load();

            mainScene = new Scene(pane);
            workspaceController controller = loader.getController();
            controller.setMain(mainStage, this);
            mainStage = new Stage();
            mainStage.setScene(mainScene);
            mainStage.show();
            mainStage.setTitle("Plan Editor");
            mainStage.setMinWidth(1270);
            mainStage.setMinHeight(750);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
