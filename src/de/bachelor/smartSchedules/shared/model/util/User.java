package de.bachelor.smartSchedules.shared.model.util;

import java.io.Serializable;
/**
 * Enthält die Userdaten so wie das geöffnete Scenario
 * @author timo
 *
 */
public class User implements Serializable{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -4030888653482428870L;
	private String name, givenname, surname, eMail;
	private int userID;
	
	/**
	 * Default
	 */
	public User() {
		
	}
	
	/**
	 * Konstruktor
	 * @param name
	 * @param givenname
	 * @param surname
	 * @param scenario
	 */
	public User(int userID, String name, String givenname, String surname, String eMail) {
		this.userID = userID;
		this.name = name;
		this.givenname = givenname;
		this.surname = surname;
		this.eMail = eMail;
	}

	public String getName() {
		return name;
	}

	public String getGivenname() {
		return givenname;
	}

	public String getSurname() {
		return surname;
	}

	public String geteMail() {
		return eMail;
	}
	
	public int getUserID() {
		return this.userID;
	}
}
