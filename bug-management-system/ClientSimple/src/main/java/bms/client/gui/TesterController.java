package bms.client.gui;

import bms.domain.Bug;
import bms.domain.StatusBug;
import bms.domain.Tester;
import bms.services.IObserver;
import bms.services.IService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class TesterController implements IObserver {
    @FXML
    public Button logoutButton;
    @FXML
    public TextArea bugDescriptionText;
    @FXML
    public TextField bugNameText;
    @FXML
    public Spinner<Integer> severitySpinner;
    @FXML
    public TableView<Bug> bugsTable;
    @FXML
    public TableColumn<Bug, String> bugNameColumn;
    @FXML
    public TableColumn<Bug, String> bugDescriptionColumn;

    private final ObservableList<Bug> bugsList = FXCollections.observableArrayList();

    private Stage stage;
    private Stage loginStage;
    private IService service;
    private Tester tester;

    public void setService(IService service) {
        this.service = service;
    }

    public void setLoginStage(Stage loginStage) {
        this.loginStage = loginStage;
    }

    public void setTesterStage(Stage stage) {
        this.stage = stage;
    }

    public void setTester(Tester tester) {
        this.tester = tester;
    }

    public void initialise() {
        bugNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        bugDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        severitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10));
        Collection<Bug> bugs;
        try {
            bugs = service.getUnsolvedBugsByTester(tester.getId());
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
            alert.show();
            return;
        }
        updateBugsTable(bugs);
    }

    private void updateBugsTable(Collection<Bug> bugs) {
        bugsList.setAll(bugs);
        bugsTable.setItems(bugsList);
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

    private boolean isInvalidBug(String description, String name, int severity) {
        if (description.isBlank() || description.isEmpty()) {
            return true;
        }
        if (name.isBlank() || name.isEmpty()) {
            return true;
        }

        return severity == 0;
    }

    public void reportBugAction() {
        String bugDescription = bugDescriptionText.getText();
        String bugName = bugNameText.getText();
        int severity = (severitySpinner.getValue() == null) ? 0 : severitySpinner.getValue();

        if (isInvalidBug(bugDescription, bugName, severity)) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Bug can't be empty", ButtonType.OK);
            alert.show();
            return;
        }

        Bug bug = new Bug(bugName, bugDescription, LocalDateTime.now(), severity, tester.getId(), StatusBug.UNSOLVED);
        try {
            service.addBug(bug);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error adding bug", ButtonType.OK);
            alert.show();
            return;
        }
        bugDescriptionText.setText("");
        bugNameText.setText("");
    }

    public void removeBugAction() {
        Bug bug = bugsTable.getSelectionModel().getSelectedItem();
        if (bug == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "No bug selected", ButtonType.OK);
            alert.show();
            return;
        }
        try {
            service.removeBug(bug);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error removing bug", ButtonType.OK);
            alert.show();
        }
    }

    @Override
    public void bugListChanged(Collection<Bug> bugs) {
        Collection<Bug> filteredBugs = bugs.stream().filter(bug -> bug.getTesterId() == tester.getId()).toList();
        Platform.runLater(() -> {
            try {
                updateBugsTable(filteredBugs);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, e.getMessage(), ButtonType.OK);
                alert.show();
            }
        });
    }
}
