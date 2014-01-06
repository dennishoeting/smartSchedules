package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.List;

import main.java.com.bradrydzewski.gwtgantt.model.Predecessor;
import main.java.com.bradrydzewski.gwtgantt.model.PredecessorType;
import main.java.com.bradrydzewski.gwtgantt.model.Task;
import de.bachelor.smartSchedules.shared.model.schedule.Allocation;
import de.bachelor.smartSchedules.shared.model.schedule.PlannedVariant;
import de.bachelor.smartSchedules.shared.model.schedule.Scenario;
import de.bachelor.smartSchedules.shared.model.schedule.Schedule;

/**
 * Manager für das Gantt-Diagramm (statisch) 
 * @author Dennis
 */
public class GanttManager {
	/**
	 * Konstanten für Default-Farbeinstellung von GanttDiagramm
	 */
	private static final String normalTaskColor_Default = Task.STYLE_GREEN,
						 selectedTaskColor_Default = Task.STYLE_ORANGE,
						 finishedTaskColor_Default = Task.STYLE_BLUE,
						 lateTaskColor_Default = Task.STYLE_RED;
	
	/**
	 * Farbeinstellungen von GanttDiagramm
	 */
	private static String normalTaskColor = normalTaskColor_Default,
				   selectedTaskColor = selectedTaskColor_Default,
				   finishedTaskColor = finishedTaskColor_Default,
				   lateTaskColor = lateTaskColor_Default;
	
	/**
	 * Hilfsvariable
	 */
	private static boolean showConnectors = false;
	
	/**
	 * Visualisierung von Gantt-Task-List aus Schedule
	 * @param schedule
	 * @return
	 */
	public static List<Task> visualizeSchedule(Scenario scenario, Schedule schedule) {
		/*
		 * Resultatliste
		 */
		List<Task> taskList = new ArrayList<Task>();
		
		if(schedule!=null) {
			Task tempTask;
			List<Predecessor> predecessors;
			
			/*
			 * Je eingeplante Variante
			 */
			for(PlannedVariant v : schedule.getPlannedVariants())
				/*
				 * Je Allokation in gewählter Variante
				 */
				for(Allocation a : v.getAllocationList()) {
					/*
					 * Überführe nötige Daten von Allocation in Task
					 */
					tempTask = new Task();
					tempTask.setUID(a.getUID());
					tempTask.setFinished(a.isFinished());
					tempTask.setLate(a.isLate());
					tempTask.setStart(a.getStart());
					tempTask.setFinish(a.getFinish(ScenarioPresenter.getInstance().getScenario()));
					tempTask.setOrder(a.getScheduleOrder(scenario).getOrderID());
					tempTask.setLevel(a.getResourceID());
					tempTask.setSelected(false);
					if(!a.getAllocationPredecessors().isEmpty()) {
						predecessors = new ArrayList<Predecessor>();
						for(Allocation p : a.getAllocationPredecessors()) {
							predecessors.add(new Predecessor(p.getUID(), PredecessorType.FS));
						}
						tempTask.setPredecessors(predecessors);
					}
					
					/*
					 * Füge Task hinzu
					 */
					taskList.add(tempTask);
				}
		}
		
		return taskList;
	}
	
	public static void setShowConnectors(boolean showConnectors) {
		GanttManager.showConnectors = showConnectors;
	}

	public static boolean getShowConnectors() {
		return GanttManager.showConnectors;
	}
	public static String getNormalTaskColor() {
		return normalTaskColor;
	}
	
	public static String getSelectedTaskColor() {
		return selectedTaskColor;
	}
	
	public static String getFinishedTaskColor() {
		return finishedTaskColor;
	}
	
	public static String getLateTaskColor() {
		return lateTaskColor;
	}

	public static void setNormalTaskColor(String normalTaskColor) {
		GanttManager.normalTaskColor = normalTaskColor;
	}

	public static void setSelectedTaskColor(String selectedTaskColor) {
		GanttManager.selectedTaskColor = selectedTaskColor;
	}

	public static void setFinishedTaskColor(String finishedTaskColor) {
		GanttManager.finishedTaskColor = finishedTaskColor;
	}

	public static void setLateTaskColor(String lateTaskColor) {
		GanttManager.lateTaskColor = lateTaskColor;
	}

	public static String getNormalTaskColor_Default() {
		return normalTaskColor_Default;
	}

	public static String getSelectedTaskColor_Default() {
		return selectedTaskColor_Default;
	}

	public static String getFinishedTaskColor_Default() {
		return finishedTaskColor_Default;
	}

	public static String getLateTaskColor_Default() {
		return lateTaskColor_Default;
	}
}
