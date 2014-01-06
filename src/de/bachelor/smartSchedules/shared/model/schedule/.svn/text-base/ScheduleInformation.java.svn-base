package de.bachelor.smartSchedules.shared.model.schedule;

import java.io.Serializable;
import java.util.Map;

import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;
/**
 * Enthält eine Informationen zu einem Schedule.
 * So muss nicht immer der ganze Schedule übergeschickt werden.
 * @author timo
 *
 */
public class ScheduleInformation implements Serializable{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -1981568836696030055L;
	private int scenarioID, scheduleID, scheduleEventType;
	Map<Integer, Integer> keyFigureValueMap;
	AlgorithmInformation algorithmInformation;
	
	/**
	 * Default
	 */
	public ScheduleInformation() {
		
	}
	
	/**
	 * Konstruktor
	 * @param scenarioID
	 * @param scheduleID
	 * @param keyFigureValueMap
	 * @param algorithmInformation
	 * @param scheduleEventName
	 */
	public ScheduleInformation(int scenarioID, int scheduleID, Map<Integer, Integer> keyFigureValueMap, 
			AlgorithmInformation algorithmInformation, int scheduleEventType) {
		this.scenarioID = scenarioID;
		this.scheduleID = scheduleID;
		this.keyFigureValueMap = keyFigureValueMap;
		this.algorithmInformation = algorithmInformation;
		this.scheduleEventType = scheduleEventType;
	}
	
	public ScheduleInformation(Schedule schedule) {
		this.scenarioID = schedule.getScenarioID();
		this.scheduleID = schedule.getScheduleID();
		this.keyFigureValueMap = schedule.getKeyFigureValueMap();
		this.algorithmInformation = schedule.getAlgorithmInformations();
		this.scheduleEventType = schedule.getScheduleEvent().getType();
	}

	public int getScenarioID() {
		return scenarioID;
	}

	public void setScenarioID(int scenarioID) {
		this.scenarioID = scenarioID;
	}

	public int getScheduleID() {
		return scheduleID;
	}

	public void setScheduleID(int scheduleID) {
		this.scheduleID = scheduleID;
	}

	public Map<Integer, Integer> getKeyFigureValueMap() {
		return keyFigureValueMap;
	}

	public void setKeyFigureValueMap(Map<Integer, Integer> keyFigureValueMap) {
		this.keyFigureValueMap = keyFigureValueMap;
	}

	public AlgorithmInformation getAlgorithmInformation() {
		return algorithmInformation;
	}

	public void setAlgorithmInformation(AlgorithmInformation algorithmInformation) {
		this.algorithmInformation = algorithmInformation;
	}

	public int getScheduleEventType() {
		return scheduleEventType;
	}

	public void setScheduleEventType(int scheduleEventType) {
		this.scheduleEventType = scheduleEventType;
	}
}
