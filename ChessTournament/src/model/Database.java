package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PiotrJ
 * Klasa odpowiadająca za obsługę bazy danych
 */
public class Database {
	private Connection connection = null;
	/**
	 * Konstruktor nawiązujący połączenie z bazą,
	 * tworzy potrzebne tabele, jeśli nie istnieją
	 */
	public Database() {
	    // load the sqlite-JDBC driver using the current class loader
	    try {
		    Class.forName("org.sqlite.JDBC");
		    // create a database connection
		    connection = DriverManager.getConnection("jdbc:sqlite:sample.db");
		    Statement statement = connection.createStatement();
		    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		    /*statement.executeUpdate("DROP TABLE gracze");
		    statement.executeUpdate("DROP TABLE turnieje");
		    statement.executeUpdate("DROP TABLE rozgrywki"); */
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS turnieje " +
		    		"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		      		"nazwa VARCHAR(50), rok VARCHAR(10), szachownic TINYINT, " +
		      		"rund TINYINT, rozegranych TINYINT, typ BOOLEAN)");
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS gracze " +
		      		"(id INTEGER PRIMARY KEY AUTOINCREMENT, turniej INTEGER, " +
		      		"imie VARCHAR(50), nazwisko VARCHAR(50), wiek INTEGER, " +
		      		"kategoria TINYINT, czy_zdyskwalifikowany BOOLEAN, grupa INTEGER)");
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS rozgrywki " +
		      		"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		      		"id_gr1 INTEGER, id_gr2 INTEGER, wynik TINYINT, " +
		      		"czyrozgrywana BOOLEAN, czywtrakcie BOOLEAN, runda INTEGER)");
	    }
	    catch(SQLException e) {
	      // if the error message is "out of memory", 
	      // it probably means no database file is found
	      System.err.println(e.getMessage());
	    } catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	public void close() {
		try {
    		if(connection != null) connection.close();
    	}
    	catch(SQLException e) {
    		System.err.println(e);
    	}
	}
	public void insertOrUpdateCompetitor(Competitor c, Integer turniej) {
		try {
			Statement statement = connection.createStatement();
			if(c.getId()==null) {
				statement.executeUpdate("insert into gracze " +
						"(turniej, imie, nazwisko, wiek, kategoria, czy_zdyskwalifikowany, grupa) VALUES (" +
						""+turniej	 				+ ", " + 
						"'"+c.getName() 			+ "', " + 
						"'"+c.getSurname() 			+ "', " + 
						"" +c.getAge() 				+ ", " + 
						"" +c.getChessCategory() 	+ ", " + 
						"" +(c.getIsDisqualified()?1:0)+ ", " +
						"" +c.getGroup() 			+ ")"
						);
			}
			else {
				statement.executeUpdate("update gracze set " + 
						"imie='" 					+ c.getName() 			+ "', " + 
						"nazwisko='" 				+ c.getSurname() 		+ "', " + 
						"wiek=" 					+ c.getAge() 			+ ", " + 
						"kategoria=" 				+ c.getChessCategory() 	+ ", " + 
						"czy_zdyskwalifikowany=" 	+ (c.getIsDisqualified()?1:0) + ", " +
						"grupa=" 					+ c.getGroup() 			+ " " +
						"where id=" 				+ c.getId() 			+ ""
						);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertOrUpdateTournament(Tournament t) {
		try {
			Statement statement = connection.createStatement();
			if(t.getId()==null) {
				statement.executeUpdate("insert into turnieje" +
						"(nazwa, rok, szachownic, rund, rozegranych, typ) VALUES (" +
						"'"+t.getName() 			+ "', " + 
						"'"+t.getYear() 			+ "', " + 
						"" +t.getBoards()			+ ", " + 
						"" +t.getRounds()		 	+ ", " + 
						"" +t.getRoundsCompleted()	+ ", " +
						"" +(t.getType()==Tournament.Type.SWISS ? 1 : 0)+")"
						);
				ResultSet rs = statement.executeQuery("select last_insert_rowid() AS id");
				t.setId(rs.getInt("id"));
			}
			else {
				statement.executeUpdate("update turnieje set " + 
						"nazwa='" 		+ t.getName() 			+ "', " + 
						"rok='" 		+ t.getYear() 			+ "', " + 
						"szachownic="	+ t.getBoards()			+ ", " + 
						"rund=" 		+ t.getRounds()	 		+ ", " + 
						"rozegranych=" 	+ t.getRoundsCompleted() + ", " 	+ 
						"typ="			+ (t.isSwiss() ? 1 : 0) + " " +
						"where id=" 	+ t.getId() 			+ ""
						);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertOrUpdateSingleGame(SingleGame g) {
		try {
			Statement statement = connection.createStatement();
			if(g.getId()==null) {
				statement.executeUpdate("insert into rozgrywki" +
						"(id_gr1, id_gr2, wynik, czyrozgrywana, czywtrakcie, runda) VALUES (" +
						"" +g.getCompetitor1() 		+ ", " + 
						"" +g.getCompetitor2()		+ ", " + 
						"" +g.getScore()			+ ", " + 
						"" +(g.getWasPlayed()?1:0) 	+ ", " + 
						"" +(g.isInProgress()?1:0)	+ ", " +
						"" +g.getRound()			+ ")"
						);
			}
			else {
				statement.executeUpdate("update rozgrywki set " + 
						"wynik="		+ g.getScore()					+ ", " + 
						"czyrozgrywana="+ (g.getWasPlayed()?1:0)	 	+ ", " + 
						"czywtrakcie=" 	+ (g.isInProgress()?1:0) + " " 	+ 
						"where id='" 	+ g.getId() 			+ "'"
						);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public List<Competitor> getCompetitors(int turniej) {
		List<Competitor> result = new ArrayList<Competitor>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from gracze where turniej="+turniej);
		    while(rs.next()) {
		    	Integer group = rs.getInt("grupa");
		        group = rs.wasNull() ? null : group;
		    	result.add(new Competitor(
		    			rs.getInt("id"),
		    			rs.getString("imie"),
		    			rs.getString("nazwisko"),
		    			rs.getInt("wiek"),
		    			rs.getInt("kategoria"),
		    			rs.getBoolean("czy_zdyskwalifikowany"),
		    			group
		    		));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public List<Tournament> getTournaments() {
		List<Tournament> result = new ArrayList<Tournament>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from turnieje");
			//nteger id, String name, String year, int boards, int rounds, int roundsCompleted
		    while(rs.next()) {
		    	result.add(new Tournament(
		    			rs.getInt("id"),
		    			rs.getString("nazwa"),
		    			rs.getString("rok"),
		    			rs.getInt("szachownic"),
		    			rs.getInt("rund"),
		    			rs.getInt("rozegranych"),
		    			rs.getBoolean("typ") ? Tournament.Type.SWISS : Tournament.Type.GROUP_ELIMINATIONS
		    		));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public List<SingleGame> getSingleGames() {
		List<SingleGame> result = new ArrayList<SingleGame>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("select * from rozgrywki");
		    while(rs.next()) {
		    	result.add(new SingleGame(
		    			rs.getInt("id"),
		    			rs.getInt("id_gr1"),
		    			rs.getInt("id_gr2"),
		    			rs.getInt("wynik"),
		    			rs.getBoolean("czyrozgrywana"),
		    			rs.getBoolean("czywtrakcie"),
		    			rs.getInt("runda")
		    		));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	public void removeCompetitor(int id) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM gracze WHERE id="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void removeTournament(int id) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM turnieje WHERE id="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void removeSingleGame(int id) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM rozgrywki WHERE id="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}	
}
