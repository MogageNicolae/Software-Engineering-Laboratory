package bms.client.gui;

import bms.domain.Developer;
import bms.domain.Tester;
import bms.services.IService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

    private IService service;
    private Parent root;
    private TesterController testerController;
    private DeveloperController developerController;
    private final Stage stage = new Stage();

    public void setService(IService service) {
        this.service = service;
    }

    private void startDeveloperWindow(Developer developer) {
        developerController.setService(service);
        developerController.setLoginStage((Stage) loginButton.getScene().getWindow());
        developerController.setDeveloper(developer);
        developerController.initialise();

        stage.setTitle("Developer");
        stage.setScene(new Scene(root, 500, 370));
        stage.setOnCloseRequest(event -> developerController.logoutAction());

        developerController.setDeveloperStage(stage);

        stage.show();
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
    }

    private void startTesterWindow(Tester tester) {
        testerController.setLoginStage((Stage) loginButton.getScene().getWindow());
        testerController.setTester(tester);
        testerController.setService(service);
        testerController.initialise();

        stage.setTitle("Tester");
        stage.setScene(new Scene(root, 500, 400));
        stage.setOnCloseRequest(event -> testerController.logoutAction());

        testerController.setTesterStage(stage);

        stage.show();
        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        currentStage.close();
    }

    public void loginAction() {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        Alert alert;

        try {
            FXMLLoader testerLoader = new FXMLLoader(getClass().getClassLoader().getResource("bms/testerView.fxml"));
            root = testerLoader.load();
            testerController = testerLoader.getController();
            Tester testerToLogIn = new Tester(username, password);
            Tester tester = service.login(testerToLogIn, testerController);
            if (tester == null) {
                FXMLLoader developerLoader = new FXMLLoader(getClass().getClassLoader().getResource("bms/developerView.fxml"));
                root = developerLoader.load();
                developerController = developerLoader.getController();
                Developer developerToLogIn = new Developer(username, password);
                Developer developer = service.login(developerToLogIn, developerController);
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

                startDeveloperWindow(developer);

                testerController = null;

                return;
            }

            if (!Objects.equals(tester.getPassword(), password)) {
                alert = new Alert(Alert.AlertType.ERROR, "Wrong password", ButtonType.OK);
                alert.show();
            }

            startTesterWindow(tester);

        } catch (Exception e) {
            alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }
}
