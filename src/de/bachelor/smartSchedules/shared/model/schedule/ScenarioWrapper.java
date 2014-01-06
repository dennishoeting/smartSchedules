package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;

public class ScenarioWrapper implements Serializable{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = 4980071435287417712L;
	
	private Scenario scenario;
	private boolean advancedUser;
	
	/**
	 * Default
	 */
	public ScenarioWrapper() {
		
	}
	
	/**
	 * 
	 * @param scenario
	 * @param advancedUser
	 */
	public ScenarioWrapper(Scenario scenario, boolean advancedUser) {
		this.scenario = scenario;
		this.advancedUser = advancedUser;
	}

	public Scenario getScenario() {
		return scenario;
	}

	public void setScenario(Scenario scenario) {
		this.scenario = scenario;
	}

	public boolean isAdvancedUser() {
		return advancedUser;
	}

	public void setAdvancedUser(boolean advancedUser) {
		this.advancedUser = advancedUser;
	}
}
