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

    private Timer timer;
    private int currentTime;
    TimerTask task;

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
        if(event.getSource().equals(btnReloadQuiz)){
            initialize(null, null);
        }
        if (event.getSource().equals(btnCandidateOK)) {
            String rb = "";
            if (rbtnAHS.isSelected()) {
                rb = "AHS";
            }
            if (rbtnNMS.isSelected()) {
                rb = "NMS";
            }
            if (rbtnOther.isSelected()) {
                rb = "Other";
            }
            if (db.getIp() != "") {
                if (tfCandiateName.getText().length() <= 3 || tfCandiateName.getText().isEmpty()) {
                    lblMessage.setText("name to short");
                } else {
                    try {
                        Candidate a = new Candidate(tfCandiateName.getText(), rb, (db.gethighestKandidateId()));
                        curCandidate = a;
                        db.addCandidate(a);
                    } catch (SQLException ex) {
                        Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    paneQuiz.setVisible(true);
                    paneCandidate.setDisable(true);
                    try {
                        obsvQuiz.setAll(db.getQuizData());
                    } catch (SQLException ex) {
                        Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } else {
                lblMessage.setText("no ip setted");
            }
        }
        if (event.getSource().equals(btnQuizSelOk)) {
            if (tfSelQuiz.getSelectionModel().getSelectedItem() != null) {
                paneQuiz.setDisable(true);
                paneWorkspace.setVisible(true);
                try {
                    obsvQuestion.setAll(db.getQuestions(tfSelQuiz.getSelectionModel().getSelectedItem()));
                    db.addTeilnahme(tfSelQuiz.getSelectionModel().getSelectedItem().getTid(), curCandidate.getId());
                } catch (SQLException ex) {
                    Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
                iterator = 0;
                lblQuestion.setText(obsvQuestion.get(iterator).toString());
                btnAnswerOK.fire();

            } else {
                lblMessage.setText("Select a Quiz");
            }
        }
        if (event.getSource().equals(btnAnswerOK)) {
            if (iterator != 0) {
                task.cancel();
            }
            if (!listviewAntwort.getSelectionModel().isEmpty()) {
                Antwort a1 = listviewAntwort.getSelectionModel().getSelectedItem();
                try {
                    db.addAnwortTest(a1.getQuizId(), a1.getFrageId(), a1.getId(), curCandidate.getId());
                    if (listviewAntwort.getSelectionModel().getSelectedItem().getRight()) {
                        right++;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                Antwort a1 = null;
            }

            if (obsvQuestion.size() <= iterator) {
                task.cancel();

                timer.cancel();
                timer.purge();
                lblMessage.setText("Test finished...  Question: " + iterator + "/" + obsvQuestion.size() + "  Ereichte Punkte: " + right);
                paneResult.setVisible(true);
                lblResult.setText(right + " von " + iterator + " Antworten sind richtig!");
                tableVgl.setVisible(true);
                btnReloadQuiz.setVisible(true);
                try {
                    ResultVergleich a = null;
                    obsvResult.setAll(db.getCompare(tfSelQuiz.getSelectionModel().getSelectedItem().getTid(), right));
                    for (ResultVergleich rv : db.getCompare(tfSelQuiz.getSelectionModel().getSelectedItem().getTid(), obsvQuestion.size())) {
                        if (rv.getUsername().equals(tfCandiateName.getText())) {
                            tableVgl.getSelectionModel().select(obsvRes.indexOf(rv));
                        }
                    }

                } catch (SQLException ex) {
                    Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
                    lblMessage.setText("An error happend: " + ex);
                }
                obsvAntwort.clear();
                lblQuestion.setDisable(true);
            } else {

                currentTime = 10;
                lblMessage.setText("Test started...   Question: " + iterator + "/" + obsvQuestion.size());
                timer = new Timer();
                task = new TimerTask() {
                    public void run() {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                if (currentTime >= 0) {
                                    lblTimer.setText("" + currentTime);
                                    //System.out.println("time left: " + currentTime);
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

                lblQuestion.setText(obsvQuestion.get(iterator).toString());
                try {

                    obsvAntwort.setAll(db.getAntworten(obsvQuestion.get(iterator), tfSelQuiz.getSelectionModel().getSelectedItem()));
                } catch (SQLException ex) {
                    Logger.getLogger(FXML_MainController.class.getName()).log(Level.SEVERE, null, ex);
                }
                iterator++;
            }
        }

        if (event.getSource().equals(btnSetIp)) {
            db.setIp(tfIp.getText());
            lblMessage.setText("Ip setted");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db=null;
        db = new Database();
        tfCandiateName.setText("");
        tfCandiateName.setPromptText("Name");
        right=0;
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
        obsvQuiz = FXCollections.observableArrayList();
        obsvQuestion = FXCollections.observableArrayList();
        obsvAntwort = FXCollections.observableArrayList();
        obsvResult = FXCollections.observableArrayList();
        tfSelQuiz.setItems(obsvQuiz);
        listviewAntwort.setItems(obsvAntwort);
        tfIp.setText("212.152.179.117");
        //db.setIp("192.168.128.152");
        db.setIp("212.152.179.117");
        lblMessage.setText("");
        lblResult.setText("");
        
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
        tableVgl.setItems(obsvRes);
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

        Runtime.getRuntime().addShutdownHook(new Thread() {
        });
    }

}
