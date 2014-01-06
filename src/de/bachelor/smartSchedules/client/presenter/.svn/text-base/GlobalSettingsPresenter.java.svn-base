package de.bachelor.smartSchedules.client.presenter;

import java.util.LinkedHashMap;

import main.java.com.bradrydzewski.gwtgantt.model.Task;

import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import de.bachelor.smartSchedules.client.view.GlobalSettingsView;

/**
 * Presenter für die globalen Einstellungen
 * @author Dennis
 */
public class GlobalSettingsPresenter {
	/**
	 * zugehöriger View
	 */
	private final GlobalSettingsView view;
	
	/**
	 * Hilfsmap für die Farben
	 */
	private final LinkedHashMap<String, String> colorMap;
	
	public GlobalSettingsPresenter() {
		view = new GlobalSettingsView();
		
		/*
		 * Hilfsmap füllen
		 */
		colorMap = new LinkedHashMap<String, String>();
		colorMap.put(Task.STYLE_BLUE, Task.STYLE_BLUE);
		colorMap.put(Task.STYLE_GREEN, Task.STYLE_GREEN);
		colorMap.put(Task.STYLE_ORANGE, Task.STYLE_ORANGE);
		colorMap.put(Task.STYLE_PURPLE, Task.STYLE_PURPLE);
		colorMap.put(Task.STYLE_RED, Task.STYLE_RED);

		view.getNormalTaskColorChooser().setValueMap(colorMap);
		view.getSelectedTaskColorChooser().setValueMap(colorMap);
		view.getFinishedTaskColorChooser().setValueMap(colorMap);
		view.getLateTaskColorChooser().setValueMap(colorMap);

		view.getNormalTaskColorChooser().setValue(colorMap.get(GanttManager.getNormalTaskColor()));
		view.getSelectedTaskColorChooser().setValue(colorMap.get(GanttManager.getSelectedTaskColor()));
		view.getFinishedTaskColorChooser().setValue(colorMap.get(GanttManager.getFinishedTaskColor()));
		view.getLateTaskColorChooser().setValue(GanttManager.getLateTaskColor());
		
		view.getScheduleChangeDeadline().setValue(ScenarioPresenter.getInstance().getScenario().getScheduleChangeDeadline());
		
		this.addListeners();
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		/**
		 * Bei Reset der Einstellungen für das GanttDiagramm
		 */
		this.view.getResetGanttDiagramSettingsButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getNormalTaskColorChooser().setValue(colorMap.get(GanttManager.getNormalTaskColor()));
				view.getSelectedTaskColorChooser().setValue(colorMap.get(GanttManager.getSelectedTaskColor()));
				view.getFinishedTaskColorChooser().setValue(colorMap.get(GanttManager.getFinishedTaskColor()));
				view.getLateTaskColorChooser().setValue(GanttManager.getLateTaskColor());
			}
		});
		
		/**
		 * Zurücksetzen der Einstellungen für das GanttDiagramm auf die Standardeinstellungen
		 */
		this.view.getRestoreDetailsGanttDiagramButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getNormalTaskColorChooser().setValue(colorMap.get(GanttManager.getNormalTaskColor_Default()));
				view.getSelectedTaskColorChooser().setValue(colorMap.get(GanttManager.getSelectedTaskColor_Default()));
				view.getFinishedTaskColorChooser().setValue(colorMap.get(GanttManager.getFinishedTaskColor_Default()));
				view.getLateTaskColorChooser().setValue(GanttManager.getLateTaskColor_Default());
			}
		});

		/**
		 * Zurücksetzen aller Einstellungen
		 */
		this.view.getResetAllButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.getNormalTaskColorChooser().setValue(colorMap.get(GanttManager.getNormalTaskColor()));
				view.getSelectedTaskColorChooser().setValue(colorMap.get(GanttManager.getSelectedTaskColor()));
				view.getFinishedTaskColorChooser().setValue(colorMap.get(GanttManager.getFinishedTaskColor()));
				view.getLateTaskColorChooser().setValue(GanttManager.getLateTaskColor());
			}
		});
		
		/**
		 * Speichern der Einstellungen
		 */
		this.view.getSaveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GanttManager.setNormalTaskColor(view.getNormalTaskColorChooser().getValueAsString());
				GanttManager.setSelectedTaskColor(view.getSelectedTaskColorChooser().getValueAsString());
				GanttManager.setFinishedTaskColor(view.getFinishedTaskColorChooser().getValueAsString());
				GanttManager.setLateTaskColor(view.getLateTaskColorChooser().getValueAsString());

				TabPresenter.getInstance().getScheduleTabPresenter().getCurrentSchedulePresenter().getView().getDataProvider().setList(GanttManager.visualizeSchedule(ScenarioPresenter.getInstance().getScenario(), ScenarioPresenter.getInstance().getChosenSchedule()));
				TabPresenter.getInstance().getScheduleTabPresenter().getIterativeAdvancementPresenter().getView().getDataProvider().setList(GanttManager.visualizeSchedule(ScenarioPresenter.getInstance().getScenario(), ScenarioPresenter.getInstance().getChosenSchedule()));
				
				SC.say("Settings saved.");
			}
		});
	}
	
	public GlobalSettingsView getView() {
		return this.view;
	}
}
