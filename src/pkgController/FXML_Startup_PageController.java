/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgController;

import bll.Candidate;
import bll.Test;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import pkgData.Database;

/**
 * FXML Controller class
 *
 * @author schueler
 */
public class FXML_Startup_PageController implements Initializable {

    Database db = new Database();
    ObservableList<Test> obsvQuiz = null;
    private Candidate curCandidate = null;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private Button btnPlaySolo;

    @FXML
    private Button btnPlayDuo;

    @FXML
    private Pane paneCandidate;

    @FXML
    private TextField tfCandiateName;

    @FXML
    private RadioButton rbtnAHS;

    @FXML
    private ToggleGroup groupSchool;

    @FXML
    private RadioButton rbtnNMS;

    @FXML
    private RadioButton rbtnOther;

    @FXML
    private Pane paneQuiz;

    @FXML
    private ComboBox<Test> tfSelQuiz;

    @FXML
    private Button btnCandidateOK;

    @FXML
    private Button btnQuizSelOk;

    @FXML
    private Label lblMessage;

    @FXML
    void onbtnClick(ActionEvent event) {
        try {
            if (event.getSource().equals(btnCandidateOK)) {
                if (validateInsertedData()) {
                    Candidate a = new Candidate(tfCandiateName.getText(), getSelectedSchooltype(),db.gethighestKandidateId());
                    curCandidate = a;
                    db.addCandidate(a);
                    paneQuiz.setDisable(false);
                    paneCandidate.setDisable(true);
                    obsvQuiz.setAll(db.getQuizData());
                }
            }
            if (event.getSource().equals(btnQuizSelOk)) {
                if (isQuizSelected()) {
                    //iterator = 0;
                    paneQuiz.setDisable(true);
                    //paneWorkspace.setVisible(true);
                    btnPlaySolo.setDisable(false);
                    btnPlayDuo.setDisable(false);
                    //obsvQuestion.setAll(db.getQuestions(tfSelQuiz.getSelectionModel().getSelectedItem()));
                    db.addTeilnahme(tfSelQuiz.getSelectionModel().getSelectedItem().getTid(), curCandidate.getId());

                    //btnAnswerOK.fire();
                }
            }
            if (event.getSource().equals(btnPlaySolo)){
               AnchorPane nextPage = FXMLLoader.load(getClass().getResource("../Main/resource/FXML_GameWindow.fxml")); // idk syntax -- soll auf neues FXML sheet verweisen
               rootPane.getChildren().setAll(nextPage);                                               // alle FXML Sheets sollten den gleichen Controller haben
            }
        } catch (Exception ex) {
            lblMessage.setText("Error:" +ex);
            ex.printStackTrace();
        }

    }
    private boolean isQuizSelected() {
        boolean validate = true;
        if (tfSelQuiz.getSelectionModel().getSelectedItem() == null) {
            lblMessage.setText("No Quiz Selected");
            validate = false;
        }
        return validate;
    }

    private String getSelectedSchooltype() {
        String selectedRbtn = "";
        if (rbtnAHS.isSelected()) {
            selectedRbtn = "AHS";
        }
        if (rbtnNMS.isSelected()) {
            selectedRbtn = "NMS";
        }
        if (rbtnOther.isSelected()) {
            selectedRbtn = "Other";
        }
        return selectedRbtn;
    }

    private boolean validateInsertedData() {
        boolean validate = true;
        if (db.getIp() == "") {
            validate = false;
            lblMessage.setText("no ip setted");
        }
        if (tfCandiateName.getText().isEmpty() || tfCandiateName.getText().length() < 3) {
            lblMessage.setText("Name is not valid.Maybe to short");
            validate = false;
        }
        return validate;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        obsvQuiz = FXCollections.observableArrayList();
        tfSelQuiz.setItems(obsvQuiz);
    }

}
