package bms.client.gui;

import bms.domain.Developer;
import bms.domain.Tester;
import bms.services.IService;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Objects;

public class LoginController {
    @FXML
    public Button loginButton;
    @FXML
    public TextField usernameInput;
    @FXML
    public TextField passwordInput;

    private IService server;
    private Parent root;
    private TesterController testerController = new TesterController();
    private DeveloperController developerController;
    private final Stage stage = new Stage();

    public void setServer(IService server) {
        this.server = server;
    }

    public void setParent(Parent root) {
        this.root = root;
    }

    public void setDeveloperController(DeveloperController developerController) {
        this.developerController = developerController;
    }

    public void loginAction() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        Alert alert;

        try {
            Tester testerToLogIn = new Tester(username, password);
            Tester tester = server.login(testerToLogIn, testerController);
            if (tester == null) {
                Developer developerToLogIn = new Developer(username, password);
                Developer developer = server.login(developerToLogIn, developerController);
                if (developer == null) {
                    alert = new Alert(Alert.AlertType.ERROR, "Wrong username", ButtonType.OK);
                    alert.show();
                    return;
                }
                if (!Objects.equals(developer.getPassword(), password)) {
                    alert = new Alert(Alert.AlertType.ERROR, "Wrong password", ButtonType.OK);
                    alert.show();
                    return;
                }

                developerController.setStage((Stage) loginButton.getScene().getWindow());
                developerController.setDeveloper(developer);
                stage.setTitle("Developer");
                if (stage.getScene() == null)
                    stage.setScene(new Scene(root, 500, 370));
                stage.setOnCloseRequest(event -> developerController.logoutAction());

                stage.show();
                Stage currentStage = (Stage) loginButton.getScene().getWindow();
                currentStage.close();

                return;
            }

            if (!Objects.equals(tester.getPassword(), password)) {
                alert = new Alert(Alert.AlertType.ERROR, "Wrong password", ButtonType.OK);
                alert.show();
            }

            stage.setTitle("Tester");
            if (stage.getScene() == null)
                stage.setScene(new Scene(root, 500, 370));
//            stage.setOnCloseRequest(event -> testerController.logoutAction());

            stage.show();
            Stage currentStage = (Stage) loginButton.getScene().getWindow();
            currentStage.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
