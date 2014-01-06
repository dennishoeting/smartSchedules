package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.tab.Tab;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HLine;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.KeyFigureCanvas;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * View für Definition der Evaluation
 * @author Dennis
 */
public class DefineEvaluationView extends Tab {
	/**
	 * Stack mit Boxen für die KeyFigures
	 */
	private final VerticalStack keyFigureStack;
	
	/**
	 * Informationsfesnter
	 */
	private final Window infoWindow,
						 keyFigureInfoWindow;
	
	/**
	 * Inhalt des Informationsfensters
	 */
	private final HTMLPane keyFigureDescription;
	
	/**
	 * Button zum Speichern
	 */
	private final Button saveButton;
	
	/**
	 * Größe
	 */
	private static final int LEFT_STACK_WIDTH = 350,
							 RIGHT_STACK_WIDTH = 560,
							 HEIGHT = 540,
							 KEY_FIGURE_HEIGHT = 30,
							 INFO_WINDOW_HEIGHT = 200;
	
	/**
	 * Konstruktor
	 */
	public DefineEvaluationView() {
		this.setTitle("Define Evaluation");
		final HorizontalStack mainStack = new HorizontalStack(HorizontalStack.STACK_TYPE_MAIN_STACK);
        
		/*
		 * Linker Bereich
		 */
		final VerticalStack leftStack = new VerticalStack();
		/*
		 * Info
		 */
		this.infoWindow = new Window();
		this.infoWindow.setTitle("Evaluation - Info");
		this.infoWindow.setWidth(LEFT_STACK_WIDTH);
		this.infoWindow.setHeight(INFO_WINDOW_HEIGHT);
		this.infoWindow.setShowCloseButton(false);
		this.infoWindow.setCanDragReposition(false);
		this.infoWindow.setAnimateMinimize(true);
		this.infoWindow.setMinimizeTime(500);
		final HLine line = new HLine(LEFT_STACK_WIDTH);
		/*
		 * Boxes
		 */
		this.keyFigureStack = new VerticalStack();
		this.keyFigureStack.setMembersMargin(10);
		this.keyFigureStack.setPadding(0);
		this.keyFigureStack.setShowEdges(false);
		this.keyFigureStack.setCanAcceptDrop(true);  
		this.keyFigureStack.setAnimateMembers(true);  
		this.keyFigureStack.setShowDragPlaceHolder(true);
        final HLine line2 = new HLine(LEFT_STACK_WIDTH);
        this.saveButton = new Button("Save changes", LEFT_STACK_WIDTH);
        leftStack.addMembers(this.infoWindow, line, this.keyFigureStack, line2, this.saveButton);
        
        /**
         * Große Beschreibung
         */
        this.keyFigureInfoWindow = new Window();
        this.keyFigureInfoWindow.setTitle("KeyFigure Description");
        this.keyFigureInfoWindow.setWidth(RIGHT_STACK_WIDTH);
        this.keyFigureInfoWindow.setHeight(HEIGHT);
        this.keyFigureInfoWindow.setShowCloseButton(false);
        this.keyFigureInfoWindow.setCanDragReposition(false);
        this.keyFigureInfoWindow.setAnimateMinimize(true);
        this.keyFigureInfoWindow.setMinimizeTime(500);
        this.keyFigureDescription = new HTMLPane();
        this.keyFigureDescription.setPadding(15);
        this.keyFigureDescription.setContents("Please select a key figure");
        this.keyFigureInfoWindow.addItem(keyFigureDescription);
        
        mainStack.addMembers(leftStack, keyFigureInfoWindow);
        this.setPane(mainStack);
	}
	
	/**
	 * Liefert ein Canvas, das die übergebene Kennzahl repräsentiert
	 * @param keyFigure Kennzahl
	 * @return das KeyFigureCanvas
	 */
	public KeyFigureCanvas generateKeyFigureCanvas(KeyFigure keyFigure) {
		KeyFigureCanvas c = new KeyFigureCanvas(keyFigure, LEFT_STACK_WIDTH, KEY_FIGURE_HEIGHT);
		
		return c;
	}
	
	/**
	 * Fügt die übergebenen Canvasses dem Stack hinzu
	 * @param keyFigures
	 */
	public void addKeyFigures(KeyFigureCanvas... keyFigures) {
		for(KeyFigureCanvas kf : keyFigures) {
			this.keyFigureStack.addMember(kf);
		}
	}
	
	public VerticalStack getKeyFiguteStack() {
		return this.keyFigureStack;
	}
	
	public Window getKeyFigureDescriptionWindow() {
		return this.keyFigureInfoWindow;
	}
	
	public HTMLPane getKeyFigureDescription() {
		return this.keyFigureDescription;
	}
	
	public Button getSaveButton() {
		return saveButton;
	}
}
