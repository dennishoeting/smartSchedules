package de.bachelor.smartSchedules.client.presenter;

import com.google.gwt.core.client.GWT;

import de.bachelor.smartSchedules.client.service.ScenarioManager;
import de.bachelor.smartSchedules.client.service.ScenarioManagerAsync;

public class ServerCommunicationsManager {
	private static ScenarioManagerAsync instance = GWT.create(ScenarioManager.class);
	
	public static ScenarioManagerAsync getInstance() {
		return instance;
	}
}
