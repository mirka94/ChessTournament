package chessTournament;

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
		      		"rund TINYINT, rozegranych TINYINT)");
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS gracze " +
		      		"(id INTEGER PRIMARY KEY AUTOINCREMENT, turniej INTEGER, " +
		      		"imie VARCHAR(50), nazwisko VARCHAR(50), wiek INTEGER, " +
		      		"kategoria TINYINT, czy_zdyskwalifikowany BOOLEAN)");
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS rozgrywki " +
		      		"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		      		"id_gr1 INTEGER, id_gr2 INTEGER, wynik TINYINT, " +
		      		"czyrozgrywana BOOLEAN, czywtrakcie BOOLEAN)");
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
						"(id, turniej, imie, nazwisko, wiek, kategoria, czy_zdyskwalifikowany) VALUES (" +
						""+c.getId() 				+ ", " + 
						""+turniej	 				+ ", " + 
						"'"+c.getName() 			+ "', " + 
						"'"+c.getSurname() 			+ "', " + 
						"" +c.getAge() 				+ ", " + 
						"" +c.getChessCategory() 	+ ", " + 
						"" +(c.getIsDisqualified()?1:0)+ ")"
						);
			}
			else {
				statement.executeUpdate("update gracze set " + 
						"imie='" 					+ c.getName() 			+ "', " + 
						"nazwisko='" 				+ c.getSurname() 		+ "', " + 
						"wiek=" 					+ c.getAge() 			+ ", " + 
						"kategoria=" 				+ c.getChessCategory() 	+ ", " + 
						"czy_zdyskwalifikowany=" 	+ (c.getIsDisqualified()?1:0) + " " 	+ 
						"where id='" 				+ c.getId() 			+ "'"
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
		    	result.add(new Competitor(
		    			rs.getInt("id"),
		    			rs.getString("imie"),
		    			rs.getString("nazwisko"),
		    			rs.getInt("wiek"),
		    			rs.getInt("kategoria"),
		    			rs.getBoolean("czy_zdyskwalifikowany")
		    		));
		    }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
}
