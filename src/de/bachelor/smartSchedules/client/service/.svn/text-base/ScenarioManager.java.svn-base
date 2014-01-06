package de.bachelor.smartSchedules.client.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import de.bachelor.smartSchedules.shared.model.exceptions.EMailAlreadyInUseException;
import de.bachelor.smartSchedules.shared.model.exceptions.NicknameAlreadyInUseException;
import de.bachelor.smartSchedules.shared.model.exceptions.WrongPasswordException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioInformations;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioWrapper;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleInformation;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.User;

/**
 * 
 * @author timo
 *
 */
@RemoteServiceRelativePath("scenariomanager")
public interface ScenarioManager extends RemoteService{
	
	/**
	 * Nutzer LogIn Daten Checken
	 * @param nickname
	 * @param password
	 * @param scenarioID
	 * @return
	 * @throws WrongPasswordException
	 */
	public User logIn(String nickname, String password) throws WrongPasswordException;
	
	/**
	 * Nutzer Registrieren
	 * @param nickname
	 * @param password
	 * @param givenname
	 * @param surname
	 * @return
	 * @throws NicknameAlreadyInUseException
	 */
	public boolean registerUser(String nickname, String password, String eMail, String givenname, String surname) throws NicknameAlreadyInUseException, EMailAlreadyInUseException;
	
	/**
	 * Gibt das Scenario zur ScenarioID zurück.
	 * @param scenarioID
	 * @return
	 */
	public ScenarioWrapper getScenario(int scenarioID, int userID);
	
	/**
	 * Gibt eine Liste mit den möglichen keyFigures aus.
	 * Hier neue KeyFigures Eintragen!
	 * @return
	 */
	public List<KeyFigure> getKeyFigures(); 
	
	/**
	 * Ändert die KeyFigures.
	 * @param scenarioID
	 * @param keyFigureList
	 */
	public void setKeyFigures(int scenarioID, List<KeyFigure> keyFigureList);
	
	/**
	 * Fügt ein ScheduleEvent hinzu.
	 * @param scheduleEvent
	 */
	public void addScheduleEvent(ScheduleEvent scheduleEvent);
	
	/**
	 * Fügt mehrere ScheduleEvents hinzu.
	 * @param scheduleEventList
	 */
	public void addScheduleEvents(List<ScheduleEvent> scheduleEventList);
	
	/**
	 * Ändert die DeadLine für das Wechseln der Schedules.
	 * @param scenarioID
	 * @param newChangeScheduleDeadline
	 */
	public void changeScheduleChangeDeadline(int scenarioID, int newChangeScheduleDeadline);

	/**
	 * Gibt Schedules aus dem Offset und Limit zurück.
	 * CurrentSchedules (mit Ausnahme des ChosenSchedules) werden übersprungen.
	 * @param scenarioID
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<Schedule> getSchedules(int scenarioID, int offset, int limit);

	/**
	 * Stoppt das Iterative Improvement.
	 * @param scenarioID
	 */
	public Schedule stopIterativeImprovement(int scenarioID);

	/**
	 * Gibt die Werte für die keyFigures zurück beim Iterative Improvement.
	 * @param scenarioID
	 * @return
	 */
	HashMap<Integer, Integer> getKeyFigureValues(int scenarioID);
	
	/**
	 * Gibt eine List mit allen ScenarioInformations zurück.
	 * Wichtig für den LogIn View.
	 * @return
	 */
	public List<ScenarioInformations> getScenarioInformationsList();

	/**
	 * Startet das IterativeImprovement.
	 * @param scenario
	 * @param chosenSchedule
	 * @param startTimeInSec
	 * @param keyFiguresMap
	 * @param keyFigureFlexibilityMap
	 * @param wrapperId index in der Liste
	 * @param changerId index in der Liste
	 */
	void startIterativeImprovement(Scenario scenario, Schedule chosenSchedule,
			int startTimeInSec, HashMap<Integer, KeyFigure> keyFiguresMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap,
			int wrapperId, int changerId);
	
	/**
	 * Fügt ein neues Scenario hinzu.
	 * Wichtig für den createNewScenarioView.
	 * @param scenario
	 */
	public void addScenario(Scenario scenario);
	
	/**
	 * Wenn ein Nutzer sich auslogt.
	 * Gibt das Scenario danach wieder frei.
	 * @param scenarioID
	 * @param userID
	 */
	public void logOut(int scenarioID, int userID);
	
	/**
	 * Liefert eine Übersicht über alle Wrapper
	 * für das IterativeImprovement.
	 * @return <ID, String>
	 */
	public Map<Integer, String> getIterativeImprovementWrapperNames();
	
	/** 
	 * Liefert eine Übersicht über alle Changer
	 * für das IterativeImprovement.
	 * @return <ID, String>
	 */
	public Map<Integer, String> getIterativeImprovementChangerNames();

	/**
	 * Setzt den Wert zwischen die Allocations.
	 * @param seconds
	 */
	public void setSecondsBetweenAllocations(int scenarioID, int seconds);

	/**
	 * Liefert eine ältere Version eines Scenarios.
	 * Beispielsweise damit ältere Scheduls gezeichnet werden können.
	 * @param scenarioID
	 * @param scheduleID
	 * @return
	 */
	public Scenario getScenarioForOldSchedule(int scenarioID, int scheduleID);
	
	/**
	 * Liefert einen bestimmten Schedule.
	 * @param scenarioID
	 * @param scheduleID
	 * @return
	 */
	public Schedule getSchedule(int scenarioID, int scheduleID);
	
	/**
	 * Liefert die ScheduleInformation zu einem bestimmten Schedule.
	 * @param scenarioID
	 * @param scheduleID
	 * @return
	 */
	public ScheduleInformation getScheduleInformation(int scenarioID, int scheduleID);
	
	/**
	 * Gibt ScheduleInformations aus dem Offset und Limit zurück.
	 * CurrentSchedules (mit Ausnahme des ChosenSchedules) werden übersprungen.
	 * @param scenarioID
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<ScheduleInformation> getScheduleInformationList(int scenarioID, int offset, int limit);
}
