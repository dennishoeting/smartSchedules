package de.bachelor.smartSchedules.client.view.util;

import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragAppearance;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;

import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Canvas für Kennzahlen-Ordnung
 * 
 * @author Dennis
 */
public class KeyFigureCanvas extends Canvas {
	/**
	 * dazugehörige Kennzahl
	 */
	private KeyFigure kf;
	
	/**
	 * Flags
	 */
	private boolean selected, locked;

	/**
	 * Konstruktor
	 * @param keyFigure Kennzahl
	 * @param width Breite
	 * @param height Höhe
	 */
	public KeyFigureCanvas(KeyFigure keyFigure, int width, int height) {
		this.kf = keyFigure;
		this.selected = false;
		this.locked = false;

		Label l = new Label("<b>" + keyFigure.getName() + "</b>");
		l.setAlign(Alignment.CENTER);
		l.setWidth(width);
		l.setHeight(height);
		this.setAutoWidth();
		this.setAutoHeight();
		this.setBorder("1px solid black");
		this.setCanDragReposition(true);
		this.setCanDrop(true);
		this.setDragAppearance(DragAppearance.TARGET);
		this.addChild(l);
	}

	public String getName() {
		return kf.getName();
	}

	public String getDescription() {
		return kf.getDescription();
	}

	public KeyFigure getKeyFigure() {
		return kf;
	}

	/**
	 * (De-)selektiert Canvas (Farbänderung)
	 * @param selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;

		if (selected) {
			this.setBackgroundColor("#DFD");
		} else {
			this.setBackgroundColor("#FFF");
		}

		this.locked = false;
	}

	/**
	 * Kehrt SelectionState um
	 */
	public void changeSelectionState() {
		this.setSelected(!this.selected);
	}

	public boolean isSelected() {
		return this.selected;
	}

	/*
	 * (Ent-)lockt Canvas (Farbänderung)
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;

		if (this.locked) {
			this.setBackgroundColor("#FDD");
		} else {
			this.setBackgroundColor("#FFF");
		}

		this.selected = false;
	}

	/**
	 * kehrt LockingState um
	 */
	public void changeLockingState() {
		this.setLocked(!this.locked);
	}

	public boolean isLocked() {
		return this.locked;
	}
}