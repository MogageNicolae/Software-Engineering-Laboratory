package bms.client;

import bms.client.gui.DeveloperController;
import bms.client.gui.LoginController;
import bms.protocol.ClientRpcProxy;
import bms.services.IService;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StartClient extends Application {
    private final static int defaultChatPort = 55555;
    private final static String defaultServer = "localhost";

    @Override
    public void start(Stage primaryStage) throws IOException {
        IService server = new ClientRpcProxy(defaultServer, defaultChatPort);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource("bms/logInView.fxml"));
        Parent root = fxmlLoader.load();

        FXMLLoader developerLoader = new FXMLLoader(getClass().getClassLoader().getResource("bms/developerView.fxml"));
        Parent developerRoot = developerLoader.load();

        LoginController loginController = fxmlLoader.getController();
        loginController.setParent(developerRoot);
        loginController.setServer(server);

        DeveloperController developerController = developerLoader.getController();
        developerController.setServer(server);

        loginController.setDeveloperController(developerController);

        primaryStage.setTitle("Hello!");
        primaryStage.setScene(new Scene(root, 250, 260));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
