package de.bachelor.smartSchedules.client.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioInformations;
import de.bachelor.smartSchedules.shared.model.schedule.ScenarioWrapper;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.ScheduleInformation;
import de.bachelor.smartSchedules.shared.model.schedule.event.ScheduleEvent;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.User;

public interface ScenarioManagerAsync {

	void logIn(String nickname, String password, AsyncCallback<User> callback);

	void registerUser(String nickname, String password, String eMail,
			String givenname, String surname, AsyncCallback<Boolean> callback);

	void getScenario(int scenarioID, int userID,
			AsyncCallback<ScenarioWrapper> callback);

	void getKeyFigures(AsyncCallback<List<KeyFigure>> callback);

	void setKeyFigures(int scenarioID, List<KeyFigure> keyFigureList,
			AsyncCallback<Void> callback);

	void addScheduleEvent(ScheduleEvent scheduleEvent,
			AsyncCallback<Void> callback);

	void addScheduleEvents(List<ScheduleEvent> scheduleEventList,
			AsyncCallback<Void> callback);

	void getSchedules(int scenarioID, int offset, int limit,
			AsyncCallback<List<Schedule>> callback);

	void stopIterativeImprovement(int scenarioID, AsyncCallback<Schedule> callback);

	void changeScheduleChangeDeadline(int scenarioID,
			int newChangeScheduleDeadline, AsyncCallback<Void> callback);

	void getKeyFigureValues(int scenarioID, AsyncCallback<HashMap<Integer, Integer>> callback);

	void getScenarioInformationsList(
			AsyncCallback<List<ScenarioInformations>> callback);

	void startIterativeImprovement(Scenario scenario, Schedule chosenSchedule,
			int startTimeInSec, HashMap<Integer, KeyFigure> keyFiguresMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap,
			int wrapperId, int changerId,
			AsyncCallback<Void> asyncCallback);

	void addScenario(Scenario scenario, AsyncCallback<Void> callback);

	void logOut(int scenarioID, int userID, AsyncCallback<Void> callback);

	void getIterativeImprovementWrapperNames(
			AsyncCallback<Map<Integer, String>> callback);

	void getIterativeImprovementChangerNames(
			AsyncCallback<Map<Integer, String>> callback);

	void setSecondsBetweenAllocations(int scenarioID, int seconds,
			AsyncCallback<Void> callback);

	void getScenarioForOldSchedule(int scenarioID, int scheduleID,
			AsyncCallback<Scenario> callback);

	void getSchedule(int scenarioID, int scheduleID,
			AsyncCallback<Schedule> callback);

	void getScheduleInformation(int scenarioID, int scheduleID,
			AsyncCallback<ScheduleInformation> callback);

	void getScheduleInformationList(int scenarioID, int offset, int limit,
			AsyncCallback<List<ScheduleInformation>> callback);
}
