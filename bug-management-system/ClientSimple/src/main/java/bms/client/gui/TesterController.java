package bms.client.gui;

import bms.domain.Developer;
import bms.domain.Tester;
import bms.services.IObserver;
import bms.services.IService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TesterController implements IObserver {
    @FXML
    public Button logoutButton;

    private Stage stage;
    private Stage loginStage;
    private IService service;
    private Tester tester;

    public void setServer(IService service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.loginStage = stage;
    }

    public void setTesterStage(Stage stage) {
        this.stage = stage;
    }

    public void setTester(Tester tester) {
        this.tester = tester;
    }

    public void logoutAction() {
        try {
            stage.close();
            service.logout(tester, this);
            loginStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
