package bms.client.gui;

import bms.domain.Developer;
import bms.services.IObserver;
import bms.services.IService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class DeveloperController implements IObserver {
    @FXML
    public Button logoutButton;

    private final Stage stage = new Stage();
    private Stage loginStage;
    private IService service;
    private Developer developer;

    public void setServer(IService service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.loginStage = stage;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    public void logoutAction() {
        try {
            stage.close();
            service.logout(developer, this);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
