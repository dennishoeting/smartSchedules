package de.bachelor.smartSchedules.shared.model.util.event;

import de.bachelor.smartSchedules.shared.model.schedule.ScenarioInformations;
import de.novanic.eventservice.client.event.Event;

/**
 * Event für das Scenario Grid im LogIn View.
 * Enthält entweder ein neues Event oder datet die alten
 * Informationen ab.
 * @author timo
 *
 */
public class ScenarioInformationGridEvent implements Event{
	
	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -3953799549737960235L;

	private ScenarioInformations scenarioInformations;
	
	/**
	 * Default
	 */
	public ScenarioInformationGridEvent() {
		
	}
	
	/**
	 * Konstruktor
	 * @param scenarioInformations
	 */
	public ScenarioInformationGridEvent(ScenarioInformations scenarioInformations) {
		this.scenarioInformations = scenarioInformations;
	}

	public ScenarioInformations getScenarioInformations() {
		return scenarioInformations;
	}

	public void setScenarioInformations(ScenarioInformations scenarioInformations) {
		this.scenarioInformations = scenarioInformations;
	}
}
