/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkgData;

import bll.Antwort;
import bll.Candidate;
import bll.Question;
import bll.ResultVergleich;
import bll.Test;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static java.time.Clock.system;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 *
 * @author schueler
 */
public class Database {

    private static String ip = "192.168.234.223";
    private static String user = "quiz";
    private static String pw = "quiz";
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    static final String DB_URL = "jdbc:oracle:thin:";
    static final String FILENAME = "SavedQuiz";

    public ArrayList<Test> getQuizData() throws SQLException {

        Connection dbConnection = null;
        Statement statement = null;

        String selectTableSQL = "SELECT tID, tName from test";

        ArrayList<Test> al = new ArrayList<Test>();
        try {
            dbConnection = getDBConnection();
            statement = (Statement) dbConnection.createStatement();

            //System.out.println(selectTableSQL);
            // execute select SQL stetement
            ResultSet rs = statement.executeQuery(selectTableSQL);

            while (rs.next()) {

                String testid = rs.getString("tID");
                //System.out.println(testid);
                String testname = rs.getString("tName");
                Test t = new Test(testid, testname);
                al.add(t);
            }
            System.out.println("Quizes loaded!");
        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
            return al;
        }
    }

    public static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {
            //System.out.println(DB_URL+user+"/"+pw+"@"+ip+":1521:ora11g");
            dbConnection = DriverManager.getConnection(DB_URL + user + "/" + pw + "@" + ip + ":1521:ora11g");
            //System.out.println("Connected to DB!");
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    public ArrayList<Question> getQuestions(Test selTest) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;
        String selectTableSQL = "SELECT * from frage where tID = ?";

        ArrayList<Question> al = new ArrayList<Question>();
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(selectTableSQL);
            statement.setString(1, selTest.getTid());
            // execute select SQL stetement
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                String qId = rs.getString("fNR");
                String question = rs.getString("fText");
                int antwortid = rs.getInt("aNrOk");
                Question q = new Question(qId, question);
                q.setAntwortId(antwortid);
                al.add(q);
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
            return al;
        }
    }

    public int gethighestKandidateId() throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;
        String selectTableSQL = "Select Max(KID) from kandidat";

        ArrayList<Question> al = new ArrayList<Question>();
        int highestid = 0;
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(selectTableSQL);

            // execute select SQL stetement
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                highestid = rs.getInt(1);
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
            highestid++;
            
            return highestid;
        }
    }

    public ArrayList<Antwort> getAntworten(Question selQuestion, Test selQuiz) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;
        String selectTableSQL = "SELECT * from Antwort where fNr = ? And tID = ?";

        ArrayList<Antwort> al = new ArrayList<Antwort>();
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(selectTableSQL);
            statement.setString(1, selQuestion.getId());
            statement.setString(2, selQuiz.getTid());
            // execute select SQL stetement
            ResultSet rs = statement.executeQuery();

            while (rs.next()) {
                int aId = rs.getInt("aNr");
                String antwort = rs.getString("aText");
                int qId = rs.getInt("fNr");
                String tid = rs.getString("tID");
                Antwort a = new Antwort(tid, qId, aId, antwort);
                if (selQuestion.getAntwortId() == a.getId()) {
                    a.setRight(true);
                }
                al.add(a);
            }

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
            return al;
        }

    }

    public void addCandidate(Candidate a) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;

        String addSQL = "INSERT INTO kandidat VALUES(?,?,?)";
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(addSQL);
            statement.setInt(1, a.getId());
            statement.setString(2, a.getName());
            statement.setString(3, a.getSchooltyp());
            //System.out.println(statement.toString());
            statement.executeUpdate();
            System.out.println("Candidate: " + a.getName() + " added to DB!");
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    public void addTeilnahme(String tid, int id) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;

        String addSQL = "INSERT INTO teilnahme VALUES(?,?,SYSDATE)";
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(addSQL);
            statement.setString(1, tid);
            statement.setInt(2, id);
            //System.out.println(statement.toString());
            statement.executeUpdate();
            System.out.println("The Candidate entered the Quiz: " + tid);
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    public void addAnwortTest(String quizId, int frageId, int antwortID, int clientId) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;

        String addSQL = "INSERT INTO testantwort VALUES(?,?,?,?)";
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(addSQL);
            statement.setString(1, quizId);
            statement.setInt(2, frageId);
            statement.setInt(3, antwortID);
            statement.setInt(4, clientId);
            //System.out.println(statement.toString());
            statement.executeUpdate();
            System.out.println("Antwort NR.: " + antwortID + ", added to DB");
        } catch (SQLException e) {
            throw new SQLException(e);
        } finally {
            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
        }
    }

    public ArrayList<ResultVergleich> getCompare(String quizId, int right) throws SQLException {
        Connection dbConnection = null;
        PreparedStatement statement = null;
        String selectTableSQL = "SELECT kname, (SELECT COUNT(*) FROM testantwort ta INNER JOIN teilnahme t ON ta.tid = t.tid AND ta.knr = t.kid INNER JOIN frage f ON t.tid = f.tid AND ta.fnr = f.fnr WHERE t.kid = k.kid AND ta.anr = f.anrok) AS cnt FROM kandidat k INNER JOIN teilnahme ON k.kid = teilnahme.kid WHERE teilnahme.tid = ?";

        ArrayList<ResultVergleich> al = new ArrayList<ResultVergleich>();
        try {
            dbConnection = getDBConnection();
            statement = dbConnection.prepareStatement(selectTableSQL);
            statement.setString(1, quizId);
            // execute select SQL stetement
            //System.out.println(selectTableSQL);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String kname = rs.getString("kname");
                int righta = rs.getInt(2);
                //System.out.println(righta);
                ResultVergleich a = new ResultVergleich(kname, righta, right);
                al.add(a);
            }
            System.out.println("Results were loaded from DB!");

        } catch (SQLException e) {

            System.out.println(e.getMessage());

        } finally {

            if (statement != null) {
                statement.close();
            }

            if (dbConnection != null) {
                dbConnection.close();
            }
            return al;
        }
    }

    public static void setIp(String ip) {
        Database.ip = ip;
    }

    public static String getIp() {
        return ip;
    }
}
