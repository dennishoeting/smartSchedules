package de.bachelor.smartSchedules.client.view;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.types.HeaderControls;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HeaderControl;
import com.smartgwt.client.widgets.Window;

/**
 * View für das Start-Fenster (Entry-Point)
 * @author Dennis
 */
public class StartingWindow extends Window {
	/**
	 * Liste mit den Kindern
	 */
	private final List<Canvas> children;
	
	/**
	 * Help-Icon in Titelleiste
	 */
	private final HeaderControl help;
	
	/**
	 * Konstruktor
	 */
	public StartingWindow() {
		super();
		
		/*
		 * Initialisierung
		 */
		this.children = new ArrayList<Canvas>();
		this.setTitle("Entrypoint");
		this.setShowTitle(true);
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setCanDragReposition(false);
		this.setLeft(100);
		this.setTop(100);
		
		/*
		 * Help-Icon
		 */
        help = new HeaderControl(HeaderControl.HELP);  
		this.setHeaderControls(HeaderControls.HEADER_LABEL, help);
	}
	
	/**
	 * Kind hinzufügen
	 * @param child Kind
	 */
	public void addToChildren(Canvas child) {
		// Höhe der Titelleiste
		child.setTop(23);
		child.setOpacity(0);
		this.children.add(child);
	}

	/**
	 * Kinder hinzufügen
	 * @param childs Kinder
	 */
	public void addChilds(Canvas... childs) {
		for(Canvas child : childs) {
			this.addToChildren(child);
		}
	}
	
	/**
	 * Bestimmtes Kind zeigen, die anderen verbergen
	 * @param canvas
	 */
	public void visibleCanvas(Canvas canvas) {
		this.unvisibleAll();
		this.addChild(canvas);
		canvas.animateFade(100);
	}
	
	/**
	 * Alle Kinder verbergen
	 */
	public void unvisibleAll() {
		for(Canvas c : this.getChildren()) {
			c.animateFade(0);
			this.removeChild(c);
		}
	}
	
	public HeaderControl getHelpHeaderControl() {
		return help;
	}
}
