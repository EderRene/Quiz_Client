/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgController;

import bll.Antwort;
import bll.Question;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import pkgData.Database;
import bll.Candidate;
import bll.ResultVergleich;
import bll.Test;
import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Int;
import java.awt.Color;
import java.sql.SQLException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import static javafx.collections.FXCollections.observableList;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 *
 * @author schueler
 */
public class FXML_MainController implements Initializable {

    Database db = null;
    ObservableList<Test> obsvQuiz = null;
    ObservableList<Question> obsvQuestion = null;
    ObservableList<Antwort> obsvAntwort = null;
    ObservableList<ResultVergleich> obsvResult = null;
    SortedList<ResultVergleich> obsvRes = null;
    private int iterator = 0;
    private int right = 0;
    private Candidate curCandidate = null;

    private static Timer timer = null;
    private int currentTime = 10;
    private TimerTask task = null;
    @FXML
    private Label label;

    @FXML
    private Button btnReloadQuiz;

    @FXML
    private Pane paneCandidate;

    @FXML
    private TextField tfCandiateName;

    @FXML
    private RadioButton rbtnAHS;

    @FXML
    private RadioButton rbtnNMS;

    @FXML
    private RadioButton rbtnOther;

    @FXML
    private Button btnCandidateOK;

    @FXML
    private Pane paneQuiz;

    @FXML
    private Button btnQuizSelOk;

    @FXML
    private ComboBox<Test> tfSelQuiz;

    @FXML
    private Pane paneWorkspace;

    @FXML
    private Label lblQuestion;

    @FXML
    private ListView<Antwort> listviewAntwort;

    @FXML
    private Button btnAnswerOK;

    @FXML
    private ToggleGroup groupSchool;

    @FXML
    private TextField tfIp;

    @FXML
    private Button btnSetIp;

    @FXML
    private Label lblMessage;

    @FXML
    private Label lblResult;

    @FXML
    private Label lblTimer;

    @FXML
    private TableView<ResultVergleich> tableVgl;

    @FXML
    private TableColumn<ResultVergleich, String> colUser;

    @FXML
    private TableColumn<ResultVergleich, Integer> colresult;

    @FXML
    private Label paneResult;

    @FXML
    void onbtnClick(ActionEvent event) {
        try {
            if (event.getSource().equals(btnReloadQuiz)) {
                db = new Database();
                right = 0;
                setLoginLabelsEnabled();
                initializeListsAndFields();
                initializeTable();
            }

            if (event.getSource().equals(btnCandidateOK)) {
                if (validateInsertedData()) {
                    Candidate a = new Candidate(tfCandiateName.getText(), getSelectedSchooltype(), (db.gethighestKandidateId()));
                    curCandidate = a;
                    db.addCandidate(a);
                    paneQuiz.setVisible(true);
                    paneCandidate.setDisable(true);
                    obsvQuiz.setAll(db.getQuizData());
                }
            }
            if (event.getSource().equals(btnQuizSelOk)) {
                if (isQuizSelected()) {
                    iterator = 0;
                    paneQuiz.setDisable(true);
                    paneWorkspace.setVisible(true);

                    obsvQuestion.setAll(db.getQuestions(tfSelQuiz.getSelectionModel().getSelectedItem()));
                    db.addTeilnahme(tfSelQuiz.getSelectionModel().getSelectedItem().getTid(), curCandidate.getId());

                    btnAnswerOK.fire();
                }
            }
            if (event.getSource().equals(btnAnswerOK)) {
                if (iterator != 0) {
                    task.cancel();
                    timer.cancel();
                    timer.purge();
                }
                processAntwort();
                if (obsvQuestion.size() > iterator) {
                    showNextQuestionAndAnswers();
                    setNewCountdown();
                } else {
                    showResults();
                    task.cancel();
                    timer.cancel();
                    timer.purge();
                }
            }

            if (event.getSource().equals(btnSetIp)) {
                db.setIp(tfIp.getText());
                lblMessage.setText("Ip setted");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void setLoginLabelsEnabled() {
        lblQuestion.setDisable(false);
        rbtnOther.setSelected(true);
        paneCandidate.setDisable(false);
        paneQuiz.setVisible(false);
        paneQuiz.setDisable(false);
        paneWorkspace.setVisible(false);
        paneWorkspace.setDisable(false);
        paneResult.setVisible(false);
        paneResult.setDisable(false);
        tableVgl.setVisible(false);
        btnReloadQuiz.setVisible(false);
        btnAnswerOK.setDisable(false);
    }

    private void initializeListsAndFields() {
        lblMessage.setText("");
        lblResult.setText("");
        tfCandiateName.setText("");
        tfCandiateName.setPromptText("Name");

        obsvQuiz = FXCollections.observableArrayList();
        obsvQuestion = FXCollections.observableArrayList();
        obsvAntwort = FXCollections.observableArrayList();
        obsvResult = FXCollections.observableArrayList();
        obsvRes = new SortedList<ResultVergleich>(obsvResult,
                (ResultVergleich stock1, ResultVergleich stock2) -> {
                    if (stock1.getErgebnis() < stock2.getErgebnis()) {
                        return 1;
                    } else if (stock1.getErgebnis() > stock2.getErgebnis()) {
                        return -1;
                    } else {
                        return 0;
                    }
                });

        tfSelQuiz.setItems(obsvQuiz);
        listviewAntwort.setItems(obsvAntwort);
        tableVgl.setItems(obsvRes);
    }

    private void initializeTable() {
        colUser.setCellValueFactory((new PropertyValueFactory<>("username")));
        colresult.setCellValueFactory(new PropertyValueFactory<>("ergebnis"));
        colUser.setCellFactory(column -> {
            return new TableCell<ResultVergleich, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty); //This is mandatory
                    if (item == null || empty) { //If the cell is empty
                        setText(null);
                        setStyle("");
                    } else { //If the cell is not empty
                        setText(item); //Put the String data in the cell
                        //We get here all the info of the Person of this row
                        ResultVergleich auxPerson = getTableView().getItems().get(getIndex());
                        // Style all persons wich name is "Edgard"
                        if (auxPerson.getUsername().equals(tfCandiateName.getText())) {
                            setStyle("-fx-background-color: yellow"); //The background of the cell in yellow
                        }
                    }
                }
            };
        });
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
        db = new Database();
        right = 0;
        setLoginLabelsEnabled();
        initializeListsAndFields();
        initializeTable();
        tfIp.setText("212.152.179.117");
        //db.setIp("192.168.128.152");
        db.setIp("212.152.179.117");
    }

    private boolean isQuizSelected() {
        boolean validate = true;
        if (tfSelQuiz.getSelectionModel().getSelectedItem() == null) {
            lblMessage.setText("No Quiz Selected");
            validate = false;
        }
        return validate;
    }

    private void showNextQuestionAndAnswers() throws SQLException {
        lblMessage.setText("Test started...   Question: " + iterator + "/" + obsvQuestion.size());
        lblQuestion.setText(obsvQuestion.get(iterator).toString());
        obsvAntwort.setAll(db.getAntworten(obsvQuestion.get(iterator), tfSelQuiz.getSelectionModel().getSelectedItem()));
        iterator++;
    }

    private void setResultTableData() throws SQLException {
        ResultVergleich a = null;
        obsvResult.setAll(db.getCompare(tfSelQuiz.getSelectionModel().getSelectedItem().getTid(), right));
    }

    private void processAntwort() throws SQLException {
        if (!listviewAntwort.getSelectionModel().isEmpty()) {
            Antwort a1 = listviewAntwort.getSelectionModel().getSelectedItem();
            db.addAnwortTest(a1.getQuizId(), a1.getFrageId(), a1.getId(), curCandidate.getId());
            a1 = null;
            if (listviewAntwort.getSelectionModel().getSelectedItem().getRight()) {
                right++;
            }
        }
    }

    private void setNewCountdown() {
        currentTime = 10;

        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        if (currentTime >= 0) {
                            lblTimer.setText("" + currentTime);
                            System.out.println("time left: " + currentTime);
                            currentTime--;
                        } else {
                            btnAnswerOK.setDisable(false);
                            btnAnswerOK.fire();
                        }
                    }
                });
            }
        };
        timer.schedule(task, 0, 1 * 1000);
    }

    private void showResults() throws SQLException {
        lblMessage.setText("Test finished...  Question: " + iterator + "/" + obsvQuestion.size() + "  Ereichte Punkte: " + right);
        paneResult.setVisible(true);
        lblResult.setText(right + " von " + iterator + " Antworten sind richtig!");
        tableVgl.setVisible(true);
        btnReloadQuiz.setVisible(true);
        setResultTableData();
        obsvAntwort.clear();
        lblQuestion.setDisable(true);
        btnAnswerOK.setDisable(true);
        paneWorkspace.setDisable(true);
    }
}
