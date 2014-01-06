package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;

/**
 * Beinhaltet alle Informationen zu einem Scenario.
 * @author timo
 *
 */
public class ScenarioInformations implements Serializable{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -4517658338700856264L;
	private int scenarioID;
	private String scenarioName, authorName;
	private boolean advancedUserPossible;

	/**
	 * Default
	 */
	public ScenarioInformations() {
		
	}
	
	/**
	 * Konstruktor
	 * @param scenarioID
	 * @param scenarioName
	 * @param autherName
	 */
	public ScenarioInformations(int scenarioID, String scenarioName, String autherName, boolean advancedUserPossible) {
		this.scenarioID = scenarioID;
		this.scenarioName = scenarioName;
		this.authorName = autherName;
		this.advancedUserPossible = advancedUserPossible;
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public String getScenarioName() {
		return scenarioName;
	}

	public String getAuthorName() {
		return authorName;
	}

	public boolean isAdvancedUserPossible() {
		return advancedUserPossible;
	}
}
