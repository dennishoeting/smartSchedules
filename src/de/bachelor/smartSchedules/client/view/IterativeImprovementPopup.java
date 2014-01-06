package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.SpinnerItem;
import com.smartgwt.client.widgets.viewer.DetailViewer;
import com.smartgwt.client.widgets.viewer.DetailViewerField;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für Iterative Verbesserung
 * @author Dennis
 */
public class IterativeImprovementPopup extends Window {
	/**
	 * Größe
	 */
	public final int WIDTH = 950,
					 HEIGHT = 500,
					 SMALL_WIDTH = 200,
					 SMALL_HEIGHT = 1,
					 RIGHT_SIDE_WIDTH = 170;
	
	/**
	 * Chart
	 */
	private final KeyFigureTrendChart chart;
	
	/**
	 * Canvas für Chart
	 */
	private final Canvas chartCanvas;
	
	/**
	 * Buttons
	 */
	private final Button zoomInButton, zoomOutButton, showKeyFiguresButton, showImprovedScheduleButton, electImprovedScheduleButton;

	/**
	 * DetailViewer
	 */
	private final DetailViewer detailViewer;
	
	/**
	 * SelectItem
	 */
	private final SelectItem keyFigureChooser;

	/**
	 * Spinner
	 */
	private final SpinnerItem updateSpinner;
	
	/**
	 * Konstruktor
	 */
	public IterativeImprovementPopup() {
		/*
		 * Initialisierung
		 */
		this.setTitle("Iterative Improvement");
		this.setWidth(SMALL_WIDTH);
		this.setHeight(SMALL_HEIGHT);
		this.setTop(100);
		this.setLeft(350);

		/*
		 * Hauptordnung
		 */
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
		final VerticalStack rightStack = new VerticalStack(VerticalStack.STACK_TYPE_SECOUND_ORDER);

		/*
		 * Chart initialisieren
		 */
		this.chartCanvas = new Canvas();
		this.chartCanvas.setWidth(680);
		this.chartCanvas.setHeight(340);
		this.chartCanvas.setBorder("1px solid black");
		this.chart = new KeyFigureTrendChart(530, 240, "Iterative improvement vs. time", "Time [s]", "Value");
		this.chartCanvas.addChild(chart);
		
		/*
		 * KeyFigure Chooser und Spinner
		 */
		final DynamicForm keyFigureChooserForm = new DynamicForm();
		this.keyFigureChooser = new SelectItem("show", "Show");
		this.keyFigureChooser.setWidth(RIGHT_SIDE_WIDTH-50);
		keyFigureChooserForm.setItems(keyFigureChooser);

		final DynamicForm spinnerForm = new DynamicForm();
		this.updateSpinner = new SpinnerItem("updateInterval", "Update interval");
		this.updateSpinner.setHint("in&nbsp;sec.");
		this.updateSpinner.setValue(5);
		this.updateSpinner.setWidth(50);
		this.updateSpinner.setWrapTitle(false);
		spinnerForm.setItems(updateSpinner);

		
		/*
		 * Buttons
		 */
		this.zoomInButton = new Button("Zoom in", RIGHT_SIDE_WIDTH);
		this.zoomOutButton = new Button("Zoom out", RIGHT_SIDE_WIDTH);
		this.showKeyFiguresButton = new Button("Show key figures", RIGHT_SIDE_WIDTH);
		this.showKeyFiguresButton.setDisabled(true);
		this.showImprovedScheduleButton = new Button("Show schedule", RIGHT_SIDE_WIDTH);
		this.showImprovedScheduleButton.setDisabled(true);
		this.electImprovedScheduleButton = new Button("Elect schedule", RIGHT_SIDE_WIDTH);
		this.electImprovedScheduleButton.setDisabled(true);
		
		/*
		 * DetailViewer
		 */
		this.detailViewer = new DetailViewer();
		this.detailViewer.setWidth(RIGHT_SIDE_WIDTH-25);
		this.detailViewer.setFields(new DetailViewerField("timeLeft", "Time left"));
			
		rightStack.addMembers(keyFigureChooserForm, spinnerForm, zoomInButton, zoomOutButton, detailViewer, new HLine(RIGHT_SIDE_WIDTH), showKeyFiguresButton, showImprovedScheduleButton, electImprovedScheduleButton);
		mainStack.addMembers(chartCanvas, rightStack);
		this.addItem(mainStack);
	}

	public SelectItem getKeyFigureChooser() {
		return keyFigureChooser;
	}

	public KeyFigureTrendChart getChart() {
		return chart;
	}

	public Button getZoomInButton() {
		return zoomInButton;
	}

	public Button getZoomOutButton() {
		return zoomOutButton;
	}

	public DetailViewer getDetailViewer() {
		return detailViewer;
	}

	public Canvas getChartCanvas() {
		return chartCanvas;
	}

	public SpinnerItem getUpdateSpinner() {
		return updateSpinner;
	}

	public Button getShowKeyFiguresButton() {
		return showKeyFiguresButton;
	}

	public Button getShowImprovedScheduleButton() {
		return showImprovedScheduleButton;
	}

	public Button getElectImprovedScheduleButton() {
		return electImprovedScheduleButton;
	}
}
