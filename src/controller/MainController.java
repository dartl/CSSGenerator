package controller;

import Main.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class MainController {
    @FXML
    private TextArea textAreaCode;

    private MainApp mainApp;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        textAreaCode.setText(this.mainApp.getAllTags().getCodeHTML());
    }
}
