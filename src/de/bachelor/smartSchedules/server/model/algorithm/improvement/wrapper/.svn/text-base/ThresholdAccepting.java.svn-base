package de.bachelor.smartSchedules.server.model.algorithm.improvement.wrapper;

import java.util.Date;
import java.util.HashMap;

import de.bachelor.smartSchedules.server.model.algorithm.improvement.changer.Changer;
import de.bachelor.smartSchedules.shared.model.exceptions.NotPlannedException;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Implementierung des Threshold Accepting Algorithmus.
 * @author timo
 *
 */
public class ThresholdAccepting extends Wrapper {
	
	private static final String name = "Threshold Accepting";
	/**
	 * Wie viel schlechter darf ein Schedule sein
	 */
	private int toleranceLimit; 
	/**
	 * Um wie viel wird das Tolerancelimit gesenkt?
	 */
	private static final int TOLERANCE_DECREASE_VALUE = 10;
	/**
	 * Wie viele Sekunden muss gewartet werden bis die Toleranz gesenkt wird?
	 */
	private static final int SECONDS_UNTIL_DECREASE = 30;
	/**
	 * Wann wurde das letzte Mal die die Toleranz verringert oder ein Plan gewählt?
	 */
	private Date lastDrecreaseOrAcceptTime;
	
	public ThresholdAccepting() {
		
	}
	
	public ThresholdAccepting(Changer changer, Scenario scenario,
			Schedule initialSchedule, int duration,
			HashMap<Integer, KeyFigure> keyFigureMap,
			HashMap<Integer, Integer> keyFigureFlexibilityMap) {
		super(changer, scenario, initialSchedule, duration, keyFigureMap,
				keyFigureFlexibilityMap);
		
		this.toleranceLimit = 0;
		this.lastDrecreaseOrAcceptTime = new Date();
		
	}

	@Override
	public boolean chooseNewSchedule(Schedule oldSchedule, Schedule newSchedule) {
		
		/*
		 * Toleranz muss verringert werden:
		 */
		if((int)(lastDrecreaseOrAcceptTime.getTime() - newSchedule.getAlgorithmInformations().getDueTime().getTime()) 
				>= ThresholdAccepting.SECONDS_UNTIL_DECREASE) {
			this.decreaseToleranceLimit();
		}
		
		/*
		 * Kennzahlen berechnen:
		 */
		for(KeyFigure kf : scenario.getKeyFigureList()) {
			try {
				
				int kfOld = kf.calculate(scenario, oldSchedule);
				int kfNew = kf.calculate(scenario, newSchedule);
				
				/*
				 * Rausfinden, ob größer oder kleiner besser ist:
				 */
				// Toleranz muss abgezogen werden:
				if(kf.doCompare(1, 2) == KeyFigure.BETTER) {
					
					kfNew -= this.toleranceLimit;
				
				} else { // Toleranz muss addiert werden:
					
					kfNew += this.toleranceLimit;
					
				}
				
				// Falls der neue Plan jetzt besser ist, wählen:
				if(kf.doCompare(kfNew, kfOld) == KeyFigure.BETTER) {
					
					// Vorher noch die neue Zeit setzen:
					this.lastDrecreaseOrAcceptTime = new Date();
					
					return true;
				}
				
			} catch (NotPlannedException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	private void decreaseToleranceLimit() {
		this.toleranceLimit += ThresholdAccepting.TOLERANCE_DECREASE_VALUE;
		this.lastDrecreaseOrAcceptTime = new Date();
	}
	
	@Override
	public String getName() {
		return name;
	}

}
