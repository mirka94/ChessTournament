package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author PiotrJ
 * Klasa odpowiadająca za obsługę bazy danych
 */
public class Database2 {
	private Connection connection = null;
	/**
	 * Konstruktor nawiązujący połączenie z bazą,
	 * tworzy potrzebne tabele, jeśli nie istnieją
	 */
	private Database2() {
	    // load the sqlite-JDBC driver using the current class loader
	    try {
		    Class.forName("org.sqlite.JDBC");
		    // create a database connection
		    connection = DriverManager.getConnection("jdbc:sqlite:ChessTournament.data");
		    Statement statement = connection.createStatement();
		    statement.setQueryTimeout(30);  // set timeout to 30 sec.
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS turnieje " +
		    		"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		      		"nazwa VARCHAR(50), rok VARCHAR(10), szachownic TINYINT, " +
		      		"rund TINYINT, rozegranych TINYINT, typ BOOLEAN)");
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS gracze " +
		      		"(id INTEGER PRIMARY KEY AUTOINCREMENT, turniej INTEGER, " +
		      		"imie VARCHAR(50), nazwisko VARCHAR(50), wiek INTEGER, " +
		      		"kategoria TINYINT, czy_zdyskwalifikowany BOOLEAN, grupa INTEGER)"); //, etap INTEGER
		    statement.executeUpdate("CREATE TABLE IF NOT EXISTS rozgrywki " +
		      		"(id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		      		"id_gr1 INTEGER, id_gr2 INTEGER, wynik TINYINT, szachownica TINYINT, " +
		      		"czyrozgrywana BOOLEAN, runda INTEGER)");
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
			PreparedStatement st;
			if(c.getId()==null) {
				st = connection.prepareStatement("INSERT INTO gracze "
					+ "(turniej, imie, nazwisko, wiek, kategoria, czy_zdyskwalifikowany, grupa) "
					+ "VALUES (?,?,?,?,?,?,?)");
				st.setInt	 (1, turniej);
				st.setString (2, c.getName());
				st.setString (3, c.getSurname());
				st.setInt	 (4, c.getAge());
				st.setInt	 (5, c.getChessCategory());
				st.setBoolean(6,c.getIsDisqualified());
				if(c.getRawGroup()==null) 
					st.setNull(7, java.sql.Types.INTEGER);
				else
					st.setInt(7, c.getRawGroup());
			}
			else {
				st = connection.prepareStatement("UPDATE gracze SET "
					+ "imie=?, nazwisko=?, wiek=?, kategoria=?, czy_zdyskwalifikowany=?, "
					+ "grupa=? where id=?");
				st.setString(1, c.getName());
				st.setString(2, c.getSurname());
				st.setInt	(3, c.getAge());
				st.setInt	(4, c.getChessCategory());
				st.setBoolean(5,c.getIsDisqualified());
				if(c.getRawGroup()==null) 
					st.setNull(6, java.sql.Types.INTEGER);
				else
					st.setInt(6, c.getRawGroup());
				st.setInt(7, c.getId());
			}
			st.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertOrUpdateTournament(Tournament t) {
		try {
			PreparedStatement st;
			if(t.getId()==null) {
				st = connection.prepareStatement("INSERT INTO turnieje "
					+"(nazwa, rok, szachownic, rund, rozegranych, typ) "
					+"VALUES (?,?,?,?,?,?)");
			}
			else {
				st = connection.prepareStatement("UPDATE TURNIEJE SET "
					+ "nazwa=?, rok=?, szachownic=?, rund=?, rozegranych=?, typ=? "
					+ "where id=?");
				st.setInt(7, t.getId());
			}
			st.setString(1, t.getName());
			st.setString(2, t.getYear());
			st.setInt(3, t.getBoards());
			st.setInt(4, t.getRounds());
			st.setInt(5, t.getRoundsCompleted());
			st.setBoolean(6, t.isSwiss());
			st.execute();
			
			if(t.getId()==null) {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("select last_insert_rowid() AS id");
				t.setId(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertOrUpdateSingleGame(SingleGame g) {
		try {
			PreparedStatement st;
			if(g.getId()==null) {
				st = connection.prepareStatement("INSERT INTO rozgrywki "
						+ "(id_gr1, id_gr2, wynik, czyrozgrywana, runda, szachownica) "
						+ "VALUES (?,?,?,?,?,?)");
				st.setInt(1, g.getCompetitorW());
				st.setInt(2, g.getCompetitorB());
				st.setInt(3, g.getScore());
				st.setBoolean(4, g.getWasPlayed());
				st.setInt(5, g.getRound());
				st.setInt(6, g.getBoard());
			}
			else {
				st = connection.prepareStatement("UPDATE ROZGRYWKI SET " + 
						"wynik=?, czyrozgrywana=? where id=?");
				st.setInt(1, g.getScore());
				st.setBoolean(2, g.getWasPlayed());
				st.setInt(3, g.getId());
			}
			st.execute();
			if(g.getId()==null) {
				Statement statement = connection.createStatement();
				ResultSet rs = statement.executeQuery("select last_insert_rowid() AS id");
				g.setId(rs.getInt("id"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public List<Competitor> getCompetitors(int turniej) {
		return getFromDB("select * from gracze where turniej="+turniej, rs->RSToCompetitor(rs));
	}
	public List<Tournament> getTournaments() {
		return getFromDB("select * from turnieje", rs->RSToTournament(rs));
	}
	public List<SingleGame> getSingleGames(int turniej, boolean finaly) {
		return getFromDB(
				"select r.* from rozgrywki r JOIN gracze g ON r.id_gr1=g.id OR r.id_gr2=g.id WHERE "
				+ (finaly?"g.grupa>=100":"r.runda>=0")
				+ " AND g.turniej="+turniej+" GROUP BY r.id", rs->RSToSingleGame(rs));
	}
	private <E> List<E> getFromDB(String query, rsToObject<E> converter) {
		List<E> result = new ArrayList<E>();
		try {
			Statement statement = connection.createStatement();
			ResultSet rs = statement.executeQuery(query);
			while(rs.next()) result.add(converter.convert(rs));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}
	@FunctionalInterface
	private interface rsToObject<E> {
		public E convert(ResultSet rs) throws SQLException;
	}
	public void removeCompetitor(int id, int turniej) {
		removeById("gracze",id);
	}
	/* nieużywane polecenia usuwania
	public void removeTournament(int id) {
		removeById("turnieje",id);
	}
	public void removeSingleGame(int id) {
		removeById("rozgrywki",id);
	}
	/**/
	public void removeById(String table, int id) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("DELETE FROM "+table+" WHERE id="+id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static SingleGame RSToSingleGame(ResultSet rs) throws SQLException {
		System.out.println(rs.getInt("szachownica"));
		return new SingleGame(
    			rs.getInt("id"),
    			rs.getInt("id_gr1"),
    			rs.getInt("id_gr2"),
    			rs.getInt("wynik"),
    			rs.getBoolean("czyrozgrywana"),
    			rs.getInt("runda"),
    			rs.getInt("szachownica")
    		);
	}
	public static Tournament RSToTournament(ResultSet rs) throws SQLException {
		return new Tournament(
    			rs.getInt("id"),
    			rs.getString("nazwa"),
    			rs.getString("rok"),
    			rs.getInt("szachownic"),
    			rs.getInt("rund"),
    			rs.getInt("rozegranych"),
    			rs.getBoolean("typ") ? Tournament.Type.SWISS : Tournament.Type.GROUP_ELIMINATIONS
    		);
	}
	public static Competitor RSToCompetitor(ResultSet rs) throws SQLException {
		Integer group = rs.getInt("grupa");
        group = rs.wasNull() ? null : group;
		return new Competitor(
    			rs.getInt("id"),
    			rs.getString("imie"),
    			rs.getString("nazwisko"),
    			rs.getInt("wiek"),
    			rs.getInt("kategoria"),
    			rs.getBoolean("czy_zdyskwalifikowany"),
    			group
    		);
	}
}
