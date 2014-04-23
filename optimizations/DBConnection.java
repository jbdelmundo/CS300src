package optimizations;

import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBConnection {

	Connection connection = null;
	String username = "root";
	static String password = "";
	static int port = 3306;
	static String hostname = "localhost";
	static String database = "IDSNN";

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://" + hostname + "/" + database;

	public DBConnection() {
		try {
			Class.forName(JDBC_DRIVER);
			System.out.println("MySQL JDBC Driver Registered!");
		} catch (ClassNotFoundException e) {
			System.out.println("Where is your MySQL JDBC Driver?");
			e.printStackTrace();
			return;
		}

		try {
			connection = DriverManager
					.getConnection(DB_URL, username, password);

		} catch (SQLException e) {
			System.out.println("Connection Failed! Check output console");
			e.printStackTrace();
			return;
		}

		if (connection == null) {
			System.out.println("Failed to make connection!");

		} else {
			System.out.println("You made it, take control your database now!");
		}
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void insertNeighbor(int fromID, int neighborID) {
		try {
			java.sql.Statement statement = connection.createStatement();

			String sql = "INSERT INTO NearestNeighbors " + "VALUES (" + fromID
					+ "," + neighborID + ")";

			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void emptyTable() {
		try {
			java.sql.Statement statement = connection.createStatement();

			String sql = "DELETE FROM NearestNeighbors";

			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<Integer> NNQuerry(int referenceID) {
		ResultSet rs = null;
		ArrayList<Integer> neighbors = new ArrayList<Integer>();
		try {
			java.sql.Statement statement = connection.createStatement();

			String sql = "Select * FROM NearestNeighbors WHERE REFERENCEID = "+referenceID;

			rs = statement.executeQuery(sql);
			while(rs.next()){
				int neighbor = rs.getInt(2);
				
				neighbors.add(neighbor);
			}
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return neighbors;
	}

	public static void main(String[] args) {
		DBConnection dbconn = new DBConnection();
		dbconn.emptyTable();
		dbconn.insertNeighbor(1, 4);
		dbconn.insertNeighbor(1, 4213);
		dbconn.insertNeighbor(1, 123);
		dbconn.insertNeighbor(1, 4345345);
		dbconn.insertNeighbor(1, 432234234);
		dbconn.NNQuerry(1);
		dbconn.close();
	}
}
