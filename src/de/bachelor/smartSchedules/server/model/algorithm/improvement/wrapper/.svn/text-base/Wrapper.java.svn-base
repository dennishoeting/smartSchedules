package de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.Changer;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;
import de.bachelor.smartSchedules.shared.model.util.AlgorithmInformation;
/**
 * Wrapper stellt die abstrakte Oberklasse für alle iterativen Verbesserungsverfahren dar.
 * @author dennis/timo
 *
 */
public abstract class Wrapper {
	
	/**
	 * Befinden sich auch noch in @look IterativeImprovement.java
	 */
	public static final int SELECTED = 1,
			LOCKED = -1,
			NOTHING = 0;
	
	protected final Changer changer;
	protected final Scenario scenario;
	protected final Schedule initialSchedule;
	/**
	 * Der Aktuell besten Schedule.
	 */
	protected Schedule currentSchedule;
	protected final int duration;
	protected final Date startTime, endTime;
	protected final HashMap<Integer, Integer> keyFigureFlexibilityMap;
	protected final HashMap<Integer, KeyFigure> keyFigureMap;
	protected HashMap<Integer, Integer> keyFigureValueMap;

	protected boolean isRunning;
	
	protected final int MILLI_TO_SEC = 1000;
	
	public Wrapper() {
		this.changer = null;
		this.scenario = null;
		this.initialSchedule = null;
		this.currentSchedule = null;
		this.duration = -1;
		this.startTime = null;
		this.endTime = null;
		this.keyFigureFlexibilityMap = null;
		this.keyFigureMap = null;
		this.keyFigureValueMap = null;
	}
	
	/**
	 * Konstruktor
	 * 
	 * @param changer Änderungsalgorithmus
	 * @param scenario Scenario
	 * @param initialSchedule Initialer Ablaufplan
	 * @param duration Dauer der iterativen Verbesserung
	 * @param keyFigureMap Map mit KeyFigures (key, keyFigure)
	 * @param keyFigureFlexibilityMap Map mit Flexibilitäten (key, konstante)
	 */
	public Wrapper(Changer changer, Scenario scenario, Schedule initialSchedule, int duration, HashMap<Integer, KeyFigure> keyFigureMap, HashMap<Integer, Integer> keyFigureFlexibilityMap) {
		this.changer = changer;
		this.scenario = scenario;
		this.initialSchedule = initialSchedule;
		this.currentSchedule = initialSchedule;
		this.duration = duration;
		this.startTime = new Date();
		this.endTime = new Date(startTime.getTime() + duration*MILLI_TO_SEC);
		this.keyFigureFlexibilityMap = keyFigureFlexibilityMap;
		this.keyFigureMap = keyFigureMap;
		this.keyFigureValueMap = new HashMap<Integer, Integer>();
		/*
		 * Defaultwerte
		 */
		for(Integer i : keyFigureMap.keySet()) {
			keyFigureValueMap.put(i, 0);
		}
	}
	
	/**
	 * Erzeugt einen iterativ Verbesserten Schedule
	 * @return
	 */
	public Schedule process() {
		final Date startTime = new Date();
		isRunning = true;
		TimerTask tt = new TimerTask() {
			@Override
			public void run() {
				try {
					while(new Date().before(endTime) && isRunning) {
						
						// Neuen möglichen Schedule berechnen:
						Schedule tmpSchedule = changer.findNeighbor(Wrapper.this, scenario, currentSchedule);
						
						// neue KeyFigures berechnen
						HashMap<Integer, Integer> tempValueMap = new HashMap<Integer, Integer>();
						for(Integer i : keyFigureMap.keySet()) {
							tempValueMap.put(i, keyFigureMap.get(i).calculate(scenario, currentSchedule));
							System.out.println("######################## keyFigure:"+keyFigureMap.get(i).getName()+", value:"+keyFigureMap.get(i).calculate(scenario, currentSchedule));
						}
						keyFigureValueMap = tempValueMap;
						
						// Neue keyFigures auch im schedule speichern:
						currentSchedule.setKeyFigureValueMap(new HashMap<Integer, Integer>(tempValueMap));
						
						// AlgorithmInformations erstellen:
						currentSchedule.setAlgorithmInformations(new AlgorithmInformation(Wrapper.this.getName(), new Date(startTime.getTime()), new Date(), initialSchedule));
						
						// Ggf. neuen Schedule wählen:
						if(Wrapper.this.chooseNewSchedule(currentSchedule, tmpSchedule)) {	
							currentSchedule = tmpSchedule;
						}
					}
				} catch (NotPlannedException e) {
					e.printStackTrace();
				}
			}
		};
		
		Timer t = new Timer();
		t.schedule(tt, 0);
		
		return currentSchedule;
	}
	
	/**
	 * Der jeweilige Algorithmus entscheidet, ob der neue Schedule benutzt wird oder nicht.
	 * Hier kommt also die jeweilige Logik rein.
	 * @param oldSchedule
	 * @param newSchedule
	 * @return
	 */
	public abstract boolean chooseNewSchedule(Schedule oldSchedule, Schedule newSchedule);

	public Changer getChanger() {
		return changer;
	}

	public Schedule getInitialSchedule() {
		return initialSchedule;
	}

	/**
	 * Stoppt die Verbesserung.
	 */
	public void cancel() {
		isRunning = false;
	}
	
	public HashMap<Integer, Integer> getKeyFigureValueMap() {
		return keyFigureValueMap;
	}
	
	public boolean isRunning() {
		return isRunning;
	}
	
	/**
	 * Liefert den aktuell besten Schedule.
	 * @return
	 */
	public Schedule getSchedule() {
		return this.currentSchedule;
	}
	
	public abstract String getName();
}
