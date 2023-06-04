package bms.client.gui;

import bms.domain.Bug;
import bms.domain.Developer;
import bms.services.IObserver;
import bms.services.IService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.Collection;
import java.util.Comparator;

public class DeveloperController implements IObserver {
    @FXML
    public Button logoutButton;
    @FXML
    public TableView<Bug> bugsTable;
    @FXML
    public TableColumn<Bug, String> bugNameColumn;
    @FXML
    public TableColumn<Bug, String> bugDescriptionColumn;
    @FXML
    public TableColumn<Bug, Integer> bugSeverityColumn;
    @FXML
    public CheckBox severityCheckBox;

    private final ObservableList<Bug> bugsList = FXCollections.observableArrayList();

    private Stage stage;
    private Stage loginStage;
    private IService service;
    private Developer developer;

    public void setServer(IService service) {
        this.service = service;
    }

    public void setStage(Stage stage) {
        this.loginStage = stage;
    }

    public void setDeveloperStage(Stage stage) {
        this.stage = stage;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
    }

    private Collection<Bug> getBugs() {
        try {
            return service.getUnsolvedBugs();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
            return null;
        }
    }

    public void initialise() {
        bugNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        bugDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        bugSeverityColumn.setCellValueFactory(new PropertyValueFactory<>("severity"));
        severityCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            updateBugsTable(getBugs(), newValue);
        });
        updateBugsTable(getBugs(), false);
    }

    public void updateBugsTable(Collection<Bug> bugs, boolean sorted) {
        if (sorted) {
            bugs = bugs.stream().sorted(Comparator.comparingInt(Bug::getSeverity)).toList();
        }
        bugsList.setAll(bugs);
        bugsTable.setItems(bugsList);
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

    public void solveBugAction() {
        Bug bug = bugsTable.getSelectionModel().getSelectedItem();
        if (bug == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "You must select a bug!", ButtonType.OK);
            alert.show();
            return;
        }
        try {
            service.solveBug(bug);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
        }
    }

    @Override
    public void bugListChanged(Collection<Bug> bugs) {
        Platform.runLater(() -> {
            try {
                updateBugsTable(bugs, severityCheckBox.isSelected());
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.show();
            }
        });
    }
}
