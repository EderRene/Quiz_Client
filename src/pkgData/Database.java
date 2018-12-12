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
     private static String ip= "212.152.179.117";
    private static String user = "d4b22";
    private static String pw = "d4b";
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:oracle:thin:";
   static final String FILENAME="SavedQuiz";
   

	public ArrayList<Test> getQuizData() throws SQLException {

		Connection dbConnection = null;
		Statement statement = null;

		String selectTableSQL = "SELECT tID, tName from test";

                  ArrayList<Test> al=new ArrayList<Test>();
		try {
			dbConnection = getDBConnection();
			statement = (Statement) dbConnection.createStatement();
                      
			System.out.println(selectTableSQL);

			// execute select SQL stetement
			ResultSet rs = statement.executeQuery(selectTableSQL);

			while (rs.next()) {

				String testid = rs.getString("tID");
                                System.out.println(testid);
				String testname = rs.getString("tName");
                                Test t=new Test(testid,testname);
                                al.add(t);
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

    
	

	public static Connection getDBConnection(){
		Connection dbConnection = null;
		try {
			Class.forName(JDBC_DRIVER);
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		try {
                    System.out.println(DB_URL+user+"/"+pw+"@"+ip+":1521:ora11g");
			dbConnection = DriverManager.getConnection(DB_URL+user+"/"+pw+"@"+ip+":1521:ora11g");
			return dbConnection;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return dbConnection;
	}
        
        public static void setIp(String ip) {
        Database.ip = ip;                                    
    }
        public static String getIp() {
        return ip;
    }

    public void deleteQuiz(Test testdel) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String deleteSQL = "DELETE FROM test WHERE tID = ?";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(deleteSQL);
			statement.setString(1, testdel.getTid());
			statement.executeUpdate();     
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

    public void addQuiz(Test testadd) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String addSQL = "INSERT INTO test VALUES(?,?)";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(addSQL);
			statement.setString(1,testadd.getTid());
                        statement.setString(2,testadd.getTname());
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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

    public void updateQuiz(Test testupdate) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String addSQL = "UPDATE test SET tName = ? WHERE tID = ?";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(addSQL);
			statement.setString(1,testupdate.getTname());
                        statement.setString(2,testupdate.getTid());
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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
    public void updateQuestion(Question toUpdateQ, Test selectedTest) throws SQLException{
        Connection dbConnection =null;
        PreparedStatement statement= null;
        String updateSQL="UPDATE frage SET fText= ? WHERE (tID= ?) AND (fNR= ?)";
        try{
            dbConnection=getDBConnection();
            statement=dbConnection.prepareStatement(updateSQL);
            statement.setString(1,toUpdateQ.getQuestion());
            statement.setString(2,selectedTest.getTid());
            statement.setInt(3,Integer.parseInt(toUpdateQ.getId()));
            System.out.println(statement.toString());
            statement.executeUpdate();
        }catch(SQLException ex){
            throw new SQLException(ex);
        }finally {
			if (statement != null) {
				statement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}
		}
        
    }

    public ArrayList<Question> getQuestions(Test selTest) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;
                String selectTableSQL = "SELECT * from frage where tID = ?";

                  ArrayList<Question> al=new ArrayList<Question>();
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(selectTableSQL);
                        statement.setString(1,selTest.getTid());                            
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				String qId = rs.getString("fNR");
				String question = rs.getString("fText");
                                int antwortid=rs.getInt("aNrOk");
                                Question q=new Question(qId,question);
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

    public void addQuestion(Question question, Test selectedTest) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String addSQL = "INSERT INTO frage VALUES(?,?,?,null)";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(addSQL);
                        statement.setString(1,selectedTest.getTid());
			statement.setInt(2,Integer.parseInt(question.getId()));
                        statement.setString(3,question.getQuestion());
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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

    public int gethighestKandidateId() throws SQLException{
        Connection dbConnection = null;
		PreparedStatement statement = null;
                String selectTableSQL = "Select Max(KID) from kandidat";

                  ArrayList<Question> al=new ArrayList<Question>();
                  int highestid=0;
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(selectTableSQL);
                                                   
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery();
                        
			while (rs.next()) {
				 highestid=rs.getInt(1);
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
    public void removeQuestion(Question question, Test selectedTest) throws SQLException {
         Connection dbConnection = null;
		PreparedStatement statement = null;

		String deleteSQL = "DELETE FROM frage WHERE fNR = ? AND fText=? AND tID=?";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(deleteSQL);
			statement.setInt(1, Integer.parseInt(question.getId()));
                        statement.setString(2,question.getQuestion());
                        statement.setString(3,selectedTest.getTid());
			statement.executeUpdate();     
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

    public ArrayList<Antwort> getAntworten(Question selQuestion,Test selQuiz) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;
                String selectTableSQL = "SELECT * from Antwort where fNr = ? And tID = ?";

                  ArrayList<Antwort> al=new ArrayList<Antwort>();
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(selectTableSQL);
                        statement.setString(1,selQuestion.getId());   
                        statement.setString(2,selQuiz.getTid());
			// execute select SQL stetement
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				int aId = rs.getInt("aNr");
				String antwort = rs.getString("aText");
                                int qId=rs.getInt("fNr");
                                String tid=rs.getString("tID");
                                Antwort a=new Antwort(tid,qId,aId,antwort);
                                if(selQuestion.getAntwortId()==a.getId()){
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

    public void addAntwort(Question selQuestion, Test selQuiz,int id) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String addSQL = "INSERT INTO antwort VALUES(?,?,?,?)";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(addSQL);
                        statement.setString(1,selQuiz.getTid());
			statement.setInt(2,Integer.parseInt(selQuestion.getId()));
                        statement.setInt(3,id);
                        statement.setString(4, "No Value Inserted");
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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

    public void removeAntwort(Antwort selectedAntwort) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String deleteSQL = "DELETE FROM antwort WHERE tID = ? AND aNr= ? AND fNr=?";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(deleteSQL);
			statement.setString(1, selectedAntwort.getQuizId());
                        statement.setInt(2,selectedAntwort.getId());
                        statement.setInt(3,selectedAntwort.getFrageId());
			statement.executeUpdate();     
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

    public void updateAntwortText(Antwort antwortold, String newValue) throws SQLException {
        Connection dbConnection =null;
        PreparedStatement statement= null;
        String updateSQL="UPDATE antwort SET aText= ? WHERE (tID= ?) AND (fNr= ?) AND (aNr = ?)";
        try{
            dbConnection=getDBConnection();
            statement=dbConnection.prepareStatement(updateSQL);
            statement.setString(1,newValue);
            statement.setString(2,antwortold.getQuizId());
            statement.setInt(3,antwortold.getFrageId());
            statement.setInt(4,antwortold.getId());
            System.out.println(statement.toString());
            statement.executeUpdate();
        }catch(SQLException ex){
            throw new SQLException(ex);
        }finally {
			if (statement != null) {
				statement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}
		}
    }

    public void setTrueAnswer(Question selQuestion, Antwort selantwort) throws SQLException {
        Connection dbConnection =null;
        PreparedStatement statement= null;
        String updateSQL="UPDATE frage SET aNrOk=? WHERE (tID= ?) AND (fText = ?)";
        try{
            dbConnection=getDBConnection();
            statement=dbConnection.prepareStatement(updateSQL);
            statement.setString(2,selantwort.getQuizId());
            statement.setInt(1,selantwort.getId());
            statement.setString(3,selQuestion.getQuestion());
            System.out.println(statement.toString());
            statement.executeUpdate();
        }catch(SQLException ex){
            throw new SQLException(ex);
        }finally {
			if (statement != null) {
				statement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}
		}
    }

    public void addCandidate(Candidate a) throws SQLException {
        Connection dbConnection = null;
		PreparedStatement statement = null;

		String addSQL = "INSERT INTO kandidat VALUES(?,?,?)";
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(addSQL);
                        statement.setInt(1,a.getId());
			statement.setString(2,a.getName());
                        statement.setString(3,a.getSchooltyp());
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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
                        statement.setString(1,tid);
			statement.setInt(2,id);
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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
                        statement.setString(1,quizId);
			statement.setInt(2,frageId);
                        statement.setInt(3,antwortID);
                        statement.setInt(4,clientId);
                        System.out.println(statement.toString());
			statement.executeUpdate();     
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
     public ArrayList<ResultVergleich> getCompare(String quizId,int right) throws SQLException {
     Connection dbConnection = null;
		PreparedStatement statement = null;
                String selectTableSQL = "SELECT kname, (SELECT COUNT(*) FROM testantwort ta INNER JOIN teilnahme t ON ta.tid = t.tid AND ta.knr = t.kid INNER JOIN frage f ON t.tid = f.tid AND ta.fnr = f.fnr WHERE t.kid = k.kid AND ta.anr = f.anrok) AS cnt FROM kandidat k INNER JOIN teilnahme ON k.kid = teilnahme.kid WHERE teilnahme.tid = ?";

                  ArrayList<ResultVergleich> al=new ArrayList<ResultVergleich>();
		try {
			dbConnection = getDBConnection();
			statement = dbConnection.prepareStatement(selectTableSQL);
                        statement.setString(1,quizId);   
			// execute select SQL stetement
                        System.out.println(selectTableSQL);
			ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				String kname = rs.getString("kname");
				int righta = rs.getInt(2);
                                System.out.println(righta);
                                 ResultVergleich a=new ResultVergleich(kname,righta,right);
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
     
}
