package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.tab.Tab;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für alte Ablaufpläne
 * @author Dennis
 */
public class ScheduleHistoryView extends Tab {
	/**
	 * ListGrid für alte Ablaufpläne
	 */
	private final OldSchedulesGrid oldSchedulesGrid;
	
	/**
	 * Button
	 */
	private final Button viewKeyFiguresButton, viewScheduleButton;
	private final Button zoomInButton, zoomOutButton;

	private final KeyFigureTrendChart historyChart;
	
	private final SelectItem keyFigureChooser;
	
	/**
	 * Größe
	 */
	private static final int LEFT_STACK_CONTENT_WIDTH = 640,
							 RIGHT_STACK_CONTENT_WIDTH = 250, 
							 CONTENT_HEIGHT = 535;
	
	/**
	 * Konstruktor
	 */
	public ScheduleHistoryView() {
		this.setTitle("Schedule History");
		
		final VerticalStack superMainStack = new VerticalStack(VerticalStack.STACK_TYPE_MAIN_STACK);
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_SECOUND_ORDER);
		this.oldSchedulesGrid = new OldSchedulesGrid(LEFT_STACK_CONTENT_WIDTH, CONTENT_HEIGHT-340);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);
		this.viewKeyFiguresButton = new Button("View key figure", RIGHT_STACK_CONTENT_WIDTH);
		this.viewKeyFiguresButton.setDisabled(true);
		this.viewScheduleButton = new Button("View selected schedule", RIGHT_STACK_CONTENT_WIDTH);
		this.viewScheduleButton.setDisabled(true);
		/*
		 * KeyFigure Chooser
		 */
		final DynamicForm keyFigureChooserForm = new DynamicForm();
		this.keyFigureChooser = new SelectItem("Show");
		this.keyFigureChooser.setWidth(RIGHT_STACK_CONTENT_WIDTH-50);
		keyFigureChooserForm.setItems(keyFigureChooser);
	
		/*
		 * Buttons
		 */
		this.zoomInButton = new Button("Zoom in", RIGHT_STACK_CONTENT_WIDTH);
		this.zoomOutButton = new Button("Zoom out", RIGHT_STACK_CONTENT_WIDTH);
		
		rightStack.addMembers(this.viewKeyFiguresButton, this.viewScheduleButton, keyFigureChooserForm, new HLine(RIGHT_STACK_CONTENT_WIDTH), zoomInButton, zoomOutButton);
		mainStack.addMembers(oldSchedulesGrid, rightStack);
		
		final Canvas chartCanvas = new Canvas();
		chartCanvas.setBorder("1px solid black");
		this.historyChart = new KeyFigureTrendChart(770, 120, "History trend", "#Schedule", "Value");
		chartCanvas.addChild(historyChart);
		
		superMainStack.addMembers(mainStack, chartCanvas);
		this.setPane(superMainStack);
	}
	
	public Button getZoomInButton() {
		return zoomInButton;
	}

	public Button getZoomOutButton() {
		return zoomOutButton;
	}

	public OldSchedulesGrid getOldSchedulesGrid() {
		return this.oldSchedulesGrid;
	}
	
	public Button getViewKeyFiguresButton() {
		return this.viewKeyFiguresButton;
	}
	
	public Button getViewScheduleButton() {
		return viewScheduleButton;
	}
	
	public KeyFigureTrendChart getHistoryChart() {
		return historyChart;
	}

	public SelectItem getKeyFigureChooser() {
		return keyFigureChooser;
	}

	/**
	 * ListGrid für alte Ablaufpläne
	 * @author Dennis
	 */
	public class OldSchedulesGrid extends ListGrid {
		public OldSchedulesGrid(int width, int height) {
			super();
			
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
			creationDate.setWidth(200);
			final ListGridField creator = new ListGridField("creator", "By algorithm");
			creator.setWidth(220);
			final ListGridField reason = new ListGridField("reason", "Reason");
			reason.setWidth(180);
			//...
			
			this.setFields(creationDate, creator, reason);
		}
		
		public OldSchedulesGrid() {
			super();
		}
	}
}
