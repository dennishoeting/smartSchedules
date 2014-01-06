package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.tab.TabSet;

import de.bachelor.smartSchedules.client.view.util.Button;

/**
 * View für Tabs
 * @author Dennis
 */
public class TabView extends TabSet {
	/**
	 * Abbrechen-Button
	 */
	private final Button aboutButton;
	
	/**
	 * Ladebereich
	 */
	private Canvas loadingCanvas;
	
	/**
	 * Größe
	 */
	public static final int WIDTH = 1000,
							HEIGHT = 650;
	
	/**
	 * Konstruktor
	 */
	public TabView() {
		/*
		 * Initialisierung
		 */
		this.setWidth(WIDTH);
		this.setHeight(HEIGHT);
		this.setTop(10);
		this.setLeft(10);
		
		/*
		 * About-Button
		 */
		this.aboutButton = new Button("about");
		this.aboutButton.setWidth(50);
		this.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, aboutButton);
		this.aboutButton.setOpacity(0);
		
		/*
		 * Lade-Bereich
		 */
		this.loadingCanvas = new Canvas();
		this.loadingCanvas.setBorder("1px dashed black");
		this.loadingCanvas.setBackgroundColor("#dfd");
		this.loadingCanvas.setAutoWidth();
		this.loadingCanvas.setAutoHeight();
		this.loadingCanvas.setWidth(750);
		this.loadingCanvas.setHeight(400);
		this.loadingCanvas.setLeft(90);
		this.loadingCanvas.setTop(90);
		final Label loadingLabel = new Label("<h4>...receiving data....</h4>");
		loadingLabel.setWrap(false);
		loadingLabel.setAlign(Alignment.CENTER);
		loadingLabel.setWidth(250);
		this.loadingCanvas.addChild(loadingLabel);
		this.loadingCanvas.setAlign(Alignment.CENTER);
		this.addChild(loadingCanvas);
	}
	
	public Button getAboutButton() {
		return this.aboutButton;
	}
	
	public Canvas getLoadingCanvas() {
		return this.loadingCanvas;
	}
}
