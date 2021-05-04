package Main;

import Controllers.loginController;
import Controllers.workspaceController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static Stage loginStage;
    public Scene loginScene;

    public static Stage mainStage;
    public Scene scene;

    @Override
    public void start(Stage primaryStage) {
        loadLoginWindow();
    }

    public void loadLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/loginForm.fxml"));
            AnchorPane pane = loader.load();

            loginScene = new Scene(pane);
            loginScene.setFill(Color.TRANSPARENT);
            loginController controller = loader.getController();
            controller.setMain(loginStage, this);
            loginStage = new Stage();
            loginStage.setScene(loginScene);
            loginStage.initStyle(StageStyle.UNDECORATED);
            loginStage.initStyle(StageStyle.TRANSPARENT);
            loginStage.setTitle("Plan Editor");
            loginStage.show();
//            mainStage.setMinWidth(1270);
//            mainStage.setMinHeight(750);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/Views/workspace.fxml"));
            BorderPane pane = loader.load();

            scene = new Scene(pane);
            scene.setFill(Color.TRANSPARENT);
            workspaceController controller = loader.getController();
            controller.setMain(mainStage, this);
            mainStage = new Stage();
            mainStage.setScene(scene);
            mainStage.setTitle("Plan Editor");
            mainStage.show();
            mainStage.setMinWidth(1270);
            mainStage.setMinHeight(750);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeLoginStage() {
        loginStage.close();
    }

    public void closeMainStage() {
        mainStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
