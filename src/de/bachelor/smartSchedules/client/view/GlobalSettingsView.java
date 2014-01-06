package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.tab.Tab;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für globale Einstellungen
 * @author Dennis
 */
public class GlobalSettingsView extends Tab {
	
	/**
	 * SelectItems
	 */
	private final SelectItem normalTaskColorChooser,
							 selectedTaskColorChooser,
							 finishedTaskColorChooser,
							 lateTaskColorChooser;
	
	/**
	 * Spinner
	 */
	private final SpinnerItem scheduleChangeDeadline;

	/**
	 * Buttons für Einstellungen in Gantt-Bereich
	 */
	private final Button resetGanttDiagramSettingsButton,
						 restoreDetailtsGanttDiagrammButton;
	
	/**
	 * Globale Buttons
	 */
	private final Button resetAllButton,
						 saveButton;
	
	/**
	 * Konstruktor
	 */
	public GlobalSettingsView() {
		this.setTitle("Global settings");
		
		/*
		 * Hauptordnung
		 */
		final VerticalStack mainStack = new VerticalStack();
		final HorizontalStack upperMainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		upperMainStack.setHeight(540);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		final VerticalStack centerStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		final VerticalStack leftStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		final HorizontalStack buttonStack = new HorizontalStack(HorizontalStack.STACK_TYPE_SECOUND_ORDER);
		buttonStack.setHeight(40);
		buttonStack.setWidth(970);
		buttonStack.setAlign(Alignment.CENTER);
		
		/*
		 * GanttEinstellungen
		 */
		final VerticalStack ganttStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		ganttStack.setIsGroup(true);
		ganttStack.setGroupTitle("Gantt diagram");
		final Window ganttColorDescription = new Window();
		ganttColorDescription.setTitle("Description");
		ganttColorDescription.setWidth(250);
		ganttColorDescription.setHeight(100);
		final DynamicForm ganttColorSchemaForm = new DynamicForm();
		this.normalTaskColorChooser = new SelectItem("colorChooser", "Normal tasks");
		this.normalTaskColorChooser.setWrapTitle(false);
		this.selectedTaskColorChooser = new SelectItem("selectedTaskColor", "Selected tasks");
		this.selectedTaskColorChooser.setWrapTitle(false);
		this.finishedTaskColorChooser = new SelectItem("finishedTaskColor", "Finished tasks");
		this.finishedTaskColorChooser.setWrapTitle(false);
		this.lateTaskColorChooser = new SelectItem("lateTaskColor", "Late tasks");
		this.lateTaskColorChooser.setWrapTitle(false);
		ganttColorSchemaForm.setItems(this.normalTaskColorChooser, this.selectedTaskColorChooser, this.finishedTaskColorChooser, this.lateTaskColorChooser);
		this.resetGanttDiagramSettingsButton = new Button("Reset", 250);
		this.restoreDetailtsGanttDiagrammButton = new Button("Restore default values", 250);
		ganttStack.addMembers(ganttColorDescription, ganttColorSchemaForm, this.resetGanttDiagramSettingsButton, this.restoreDetailtsGanttDiagrammButton);
		
		/*
		 * ScenarioEinstellungen
		 */
		final VerticalStack scenarioStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		scenarioStack.setIsGroup(true);
		scenarioStack.setGroupTitle("Scenario");
		final Window scenarioDescription = new Window();
		scenarioDescription.setTitle("Description");
		scenarioDescription.setWidth(250);
		scenarioDescription.setHeight(100);
		final DynamicForm scenarioForm = new DynamicForm();
		this.scheduleChangeDeadline = new SpinnerItem("scheduleChangeDeadline", "Schedule change deadline");
		this.scheduleChangeDeadline.setWidth(55);
		this.scheduleChangeDeadline.setHint("in&nbsp;min.");
		this.scheduleChangeDeadline.setWrapTitle(false);
		this.scheduleChangeDeadline.setValue(0);
		scenarioForm.setItems(scheduleChangeDeadline);
		scenarioStack.addMembers(scenarioDescription, scenarioForm);
		
		/*
		 * Buttons
		 */
		this.resetAllButton = new Button("Reset all");
		this.saveButton = new Button("Save");
		buttonStack.addMembers(resetAllButton, saveButton);
		
		leftStack.addMembers(ganttStack);
		centerStack.addMembers(scenarioStack);
		upperMainStack.addMembers(leftStack, centerStack, rightStack);
		mainStack.addMembers(upperMainStack, buttonStack);
		this.setPane(mainStack);
	}

	public SelectItem getNormalTaskColorChooser() {
		return normalTaskColorChooser;
	}

	public SelectItem getSelectedTaskColorChooser() {
		return selectedTaskColorChooser;
	}

	public SelectItem getFinishedTaskColorChooser() {
		return finishedTaskColorChooser;
	}

	public SelectItem getLateTaskColorChooser() {
		return lateTaskColorChooser;
	}

	public SpinnerItem getScheduleChangeDeadline() {
		return scheduleChangeDeadline;
	}

	public Button getResetAllButton() {
		return resetAllButton;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public Button getResetGanttDiagramSettingsButton() {
		return resetGanttDiagramSettingsButton;
	}
	
	public Button getRestoreDetailsGanttDiagramButton() {
		return restoreDetailtsGanttDiagrammButton;
	}
}
