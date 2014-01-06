package de.bachelor.smartSchedules.server.model.db;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Verbindung mit der DB
 * @author timo
 *
 */
public final class DBConnection {

	/**
	 * Datenbankverbindung
	 */
	private static Connection connection;

	/**
	 * Datenbanktreiber
	 */
	private static final String DB_DRIVER = "com.mysql.jdbc.Driver";

	/**
	 * Hashmap mit allen PreparedStatements
	 */
	private static HashMap<String, PreparedStatement> statements = null;

	/**
	 * Schließt die Datenbankverbindung, PreparedStatements und löscht alle PreparedStatements aus der HashMap
	 */
	public static void closeConnection() {
		if (DBConnection.connection != null) {
			try {
				closePstmts();
				DBConnection.connection.close();
				DBConnection.statements.clear();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Schließt alle PreparedStatements
	 * 
	 * @throws SQLException
	 *             Falls ein Datenbankfehler auftritt
	 */
	private static void closePstmts() throws SQLException {
		if (DBConnection.statements != null && !DBConnection.statements.isEmpty()) {
			for (PreparedStatement pstmt : DBConnection.statements.values()) {
				pstmt.close();
			}
		}
	}

	/**
	 * Verbindung aufbauen:
	 * @return
	 */
	public static Connection getConnection() {
		if (DBConnection.connection == null) {
			new DBConnection();
		} else {
			DBConnection.isConnectionValid();
		}
		return DBConnection.connection;
	}

	/**
	 * Liefert das SQL-Statement zum übergebenen SQL-Query. Ist das SQL-Statement noch nicht vorbereitet, so wird es kompiliert
	 * und in die HashMap mit den PreparedStatments hinzugefügt.
	 * 
	 * @param sql
	 *            SQL-Statement, das vorbereitet werden soll.
	 * @return PreparedStatement des übergebenen SQL-Query
	 * 
	 * @throws SQLException
	 *             Falls bspw. die Verbindung nicht vorhanden ist oder das Statement syntaktisch falsch ist.
	 */
	public static PreparedStatement getPstmt(String sql) {
		PreparedStatement pstmt;
		if (DBConnection.connection != null) {
			DBConnection.isConnectionValid();
		}
		if (DBConnection.statements != null && DBConnection.statements.containsKey(sql)) {
			pstmt = DBConnection.statements.get(sql);
		} else {
			pstmt = prepareStatement(sql);
			DBConnection.statements.put(sql, pstmt);
		}

		return pstmt;
	}

	/**
	 * Prüft ob die Verbindung noch gültig ist
	 */
	private static boolean isConnectionValid() {
		boolean result = false;
		try {
			// Prüft ob die Verbindung noch gültig ist. Timeout=100
			if (!DBConnection.connection.isValid(100)) {
				DBConnection.closeConnection();
				DBConnection.connection = null;
				new DBConnection();
			}
			result = true;
		} catch (SQLException e) {
			e.printStackTrace();
			result = false;
		}

		return result;
	}

	/**
	 * Allgemeine Funktion zum compilieren eines SQL's
	 * 
	 * @param sql
	 *            SQL String, der im PreparedStatementobjekt compiliert wird
	 * 
	 * @return das compilierte Statement
	 */
	private static PreparedStatement prepareStatement(String sql) {
		PreparedStatement pstmt = null;
		try {
			if (DBConnection.connection == null) {
				DBConnection.getConnection();
			} else {
				DBConnection.isConnectionValid();
			}
			pstmt = DBConnection.connection.prepareStatement(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return pstmt;
	}

	/**
	 * Konstruktor
	 */
	private DBConnection() {
		if (DBConnection.connection == null) {
			try {
				Class.forName(DBConnection.DB_DRIVER).newInstance();
				DBConnection.connection = DriverManager.getConnection(getConnectionURL());
				DBConnection.statements = new HashMap<String, PreparedStatement>();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Schreibt die ConnectionURL für die DB
	 * @return
	 */
	private String getConnectionURL() {
		
		String jdbcURL = "";
		File propertiesFile = new File("database.properties");
		Properties properties = new Properties();

		if(propertiesFile.exists())
		{
			BufferedInputStream bis;
			try {
				
				bis = new BufferedInputStream(new FileInputStream(propertiesFile));
				properties.load(bis);
				bis.close();

				jdbcURL = "jdbc:mysql://" + properties.getProperty("server")+ ":" + properties.getProperty("port") + "/" + properties.getProperty("database")
						+ "?user=" + properties.getProperty("user") + "&password=" + properties.getProperty("password");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return jdbcURL;
	}
}
