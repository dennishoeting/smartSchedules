package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für Liste von alternativen Schedules
 * @author Dennis
 */
public class SchedulesAlternativesView extends Window {
	/**
	 * Hauptkomponente: ListGrid
	 */
	private final AlternativesListGrid listGrid;
	
	/**
	 * Chart
	 */
	private final ScheduleComparisonChart chart;
	
	/**
	 * Buttons
	 */
	private final Button electButton,
						 examineButton,
						 showKeyFiguresButton;
	
	private final SelectItem comparedKeyFigureChooser;
	
	/**
	 * Konstruktor
	 */
	public SchedulesAlternativesView() {
		/*
		 * Initialisierung
		 */
		this.setTitle("Alternative schedules");
		this.setWidth(900);
		this.setHeight(475);
		
		/*
		 * Aufbau
		 */
		final VerticalStack superMainStack = new VerticalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_SECOUND_ORDER);
		this.listGrid = new AlternativesListGrid(565, 150);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		this.examineButton = new Button("Examine", 250);
		this.examineButton.setDisabled(true);
		this.electButton = new Button("Elect", 250);
		this.electButton.setDisabled(true);
		this.showKeyFiguresButton = new Button("Show key figures", 250);
		this.showKeyFiguresButton.setDisabled(true);
		final DynamicForm chooserForm = new DynamicForm();
		this.comparedKeyFigureChooser = new SelectItem("comparedKeyFigure", "Compared key figure");
		this.comparedKeyFigureChooser.setWidth(130);
		this.comparedKeyFigureChooser.setWrapTitle(false);
		chooserForm.setItems(comparedKeyFigureChooser);
		rightStack.addMembers(showKeyFiguresButton, examineButton, electButton, chooserForm);
		mainStack.addMembers(listGrid, rightStack);
		final Canvas chartCanvas = new Canvas();
		chartCanvas.setBorder("1px solid black");
		this.chart = new ScheduleComparisonChart(550, 180);
		chartCanvas.addChild(chart);
		superMainStack.addMembers(mainStack, chartCanvas);
		this.addItem(superMainStack);
		
//		/*
//		 * Zeichnen
//		 */
//		this.draw();
	}
	
	public AlternativesListGrid getListGrid() {
		return listGrid;
	}

	public Button getElectButton() {
		return electButton;
	}

	public Button getExamineButton() {
		return examineButton;
	}

	public ScheduleComparisonChart getChart() {
		return chart;
	}

	public SelectItem getComparedKeyFigureChooser() {
		return comparedKeyFigureChooser;
	}

	public Button getShowKeyFiguresButton() {
		return showKeyFiguresButton;
	}

	/**
	 * ListGrid für Ablaufplanungsalternativen
	 * @author Dennis
	 */
	public class AlternativesListGrid extends ListGrid {
		/**
		 * Konstruktor
		 * @param width Breite
		 * @param height Höhe
		 */
		private AlternativesListGrid(int width, int height) {
			this.setWidth(width);
			this.setHeight(height);
			
			this.setSelectionType(SelectionStyle.SINGLE);
			this.setCanSort(false);
			this.setCanMultiSort(false);
			this.setShowRowNumbers(true);
			
			final ListGridField rowNumberField = new ListGridField();
			rowNumberField.setWidth(20);
			this.setRowNumberFieldProperties(rowNumberField);
			
			final ListGridField creationDate = new ListGridField("creationDate", "Creation date");
			creationDate.setWidth(185);
			final ListGridField creator = new ListGridField("creator", "By algorithm");
			creator.setWidth(250);
			final ListGridField creationDuration = new ListGridField("creationDuration", "Duration");
			creationDuration.setWidth(70);
			final ListGridField isMarked = new ListGridField("isMarked", "&nbsp;");
			isMarked.setType(ListGridFieldType.BOOLEAN);
			isMarked.setWidth(20);
			
			this.setFields(creationDate, creator, creationDuration, isMarked);
		}
	}
}
