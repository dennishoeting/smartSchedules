package de.bachelor.smartSchedules.client.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.DOM;
import com.googlecode.gchart.client.GChart;

import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Erweiterung eines GChart für iterative Verbesserung
 * 
 * @author Dennis
 */
public class KeyFigureTrendChart extends GChart {
	/**
	 * Kopie der Kennzahlenliste
	 */
	private List<KeyFigure> clonedKeyFigureList;

	/**
	 * Geordnete Liste der KeyFigureIDs
	 */
	private List<Integer> orderedKeyFigureIDList;

	/**
	 * Speicherung der Werte in Map<KeyFigureID, Werteliste>
	 */
	private Map<Integer, ArrayList<Value>> valueMap;

	private int zoomIndex = 0;

	private static final int MAX_ZOOM_INDEX = 10;

	/**
	 * Variable zur Speicherung der aktuellen Auswahl
	 */
	private int selectedKeyFigureID = ALL;

	/**
	 * Hilfsvariable
	 */
	public static final int ALL = -1;

	public boolean dragging = false;

	public boolean multiCurve = true;
	public int xPos, yPos;
	public final int width, height;
	
	public String[] colors = new String[]{"#f00", "#0f0", "#00f", "#ff0", "#f0f", "#0ff"};

	/**
	 * Konstruktor
	 * 
	 * @param width
	 *            Breite
	 * @param height
	 *            Höhe
	 * @param title
	 *            Titel
	 */
	public KeyFigureTrendChart(int width, int height, String title,
			String xAxisLabel, String yAxisLabel) {
		this.width = width;
		this.height = height;
		
		/*
		 * Initialisierung
		 */
		this.valueMap = new HashMap<Integer, ArrayList<Value>>();
		this.setChartSize(width, height);

		/*
		 * Achsenbeschriftung
		 */
		this.getXAxis().setAxisLabel(xAxisLabel);
		this.getYAxis().setAxisLabel(yAxisLabel);

		this.setChartTitle("Key figure trend");
	}

	/**
	 * Initialisierung des Chart
	 * 
	 * @param length
	 * @param keyFigureList
	 */
	public void initialize(int length, List<KeyFigure> keyFigureList) {
		this.clonedKeyFigureList = new ArrayList<KeyFigure>();

		/*
		 * Kopie der KeyFigures erstellen
		 */
		for (KeyFigure kf : keyFigureList)
			clonedKeyFigureList.add(kf);

		/*
		 * Liste der IDs der Kennzahlen erstellen und ordnen
		 */
		this.orderedKeyFigureIDList = new ArrayList<Integer>();
		for (KeyFigure kf : keyFigureList) {
			clonedKeyFigureList.set(kf.getId(), kf);
			this.orderedKeyFigureIDList.add(kf.getId());
			this.valueMap.put(kf.getId(), new ArrayList<Value>());
		}
		Collections.sort(orderedKeyFigureIDList);

		/*
		 * Achsen ziehen
		 */
		this.getXAxis().setAxisMax(length);
		this.getXAxis().setTickCount((length / 60) + 1);

		/*
		 * Zeichnen
		 */
		this.setCurves();
	}

	/**
	 * Visualisiert eine Kurve, die die übergebene Kennzahl repräsentiert
	 * 
	 * @param keyFigureID
	 *            ID der Kennzahl
	 */
	public void setCurve(int keyFigureID) {
		multiCurve = false;

		this.clearCurves();

		this.addCurve();
		this.getCurve().getSymbol().setSymbolType(SymbolType.LINE);
		this.getCurve().getSymbol().setBorderColor(colors[keyFigureID]);
		
		for (Value v : this.valueMap.get(keyFigureID)) {
			this.getCurve().addPoint(v.getX(), v.getY());
		}
		
		/*
		 * Titel
		 */
		this.setChartFootnotes("Key: <span style='color:"+colors[keyFigureID]+"'>"+clonedKeyFigureList.get(keyFigureID).getName()+"</span>");
		this.getChartFootnotes().setWidth(width+"px");
		this.update();
	}

	/**
	 * Visualisiert alle Kurven (für alle Kennzahlen)
	 */
	public void setCurves() {
		multiCurve = true;

		this.clearCurves();

		String footnote = "Key: ";
		
		for (Integer keyFigureID : orderedKeyFigureIDList) {
			this.addCurve(keyFigureID);
			this.getCurve(keyFigureID).getSymbol().setSymbolType(SymbolType.LINE);
			this.getCurve().getSymbol().setBorderColor(colors[keyFigureID]);
			for (Value v : this.valueMap.get(keyFigureID)) {
				this.getCurve().addPoint(v.getX(), v.getY());
			}
			footnote += "<span style='color:"+colors[keyFigureID]+"'>"+clonedKeyFigureList.get(keyFigureID).getName()+"</span> ";
		}
		this.setChartFootnotes(footnote);

		this.update();
	}

	/**
	 * Fügt dem Chart (bzw. der übergebenen Kennzahl) einen Wert hinzu
	 * 
	 * @param x
	 * @param y
	 * @param keyFigure
	 */
	public void push(double x, double y, KeyFigure keyFigure) {
		this.valueMap.get(keyFigure.getId()).add(new Value(x, y));
		
		if (selectedKeyFigureID < 0) {
			this.getCurve(keyFigure.getId()).addPoint(x, y);
		} else if (keyFigure.getId() == selectedKeyFigureID) {
			this.getCurve().addPoint(x, y);
		}

		this.update();
	}

	public void zoomInAction() {
		if (this.zoomIndex < MAX_ZOOM_INDEX) {
			double xMin = getXAxis().getAxisMin();
			double xMax = getXAxis().getAxisMax();
			double yMin = getYAxis().getAxisMin();
			double yMax = getYAxis().getAxisMax();
			double xCenter = (xMin + xMax) / 2;
			double yCenter = (yMin + yMax) / 2;
			double dx = (xMax - xMin) / (2 * Math.sqrt(2));
			double dy = (yMax - yMin) / (2 * Math.sqrt(2));
			this.getXAxis().setAxisMin(xCenter - dx);
			this.getXAxis().setAxisMax(xCenter + dx);
			this.getYAxis().setAxisMin(yCenter - dy);
			this.getYAxis().setAxisMax(yCenter + dy);
			this.update();
			this.zoomIndex++;
		}
	}

	public void zoomOutAction() {
		if (this.zoomIndex > -MAX_ZOOM_INDEX) {
			double xMin = getXAxis().getAxisMin();
			double xMax = getXAxis().getAxisMax();
			double yMin = getYAxis().getAxisMin();
			double yMax = getYAxis().getAxisMax();
			double xCenter = (xMin + xMax) / 2;
			double yCenter = (yMin + yMax) / 2;
			double dx = (xMax - xMin) / Math.sqrt(2);
			double dy = (yMax - yMin) / Math.sqrt(2);
			this.getXAxis().setAxisMin(xCenter - dx);
			this.getXAxis().setAxisMax(xCenter + dx);
			this.getYAxis().setAxisMin(yCenter - dy);
			this.getYAxis().setAxisMax(yCenter + dy);
			this.update();
			this.zoomIndex--;
		}
	}
	
	public void resetZoomAction() {
		int difference = Math.abs(zoomIndex - 0);
		if(difference == 0) return;
		
		if(zoomIndex < 0) {
			for(int i=0; i<difference; i++) {
				zoomInAction();
			}
		} else {
			for(int i=0; i<difference; i++) {
				zoomOutAction();
			}
		}
		
		resetViewport();
		if (multiCurve) {
			for (Integer keyFigureID : orderedKeyFigureIDList) {
				getCurve(keyFigureID).setXShift(0);
				getCurve(keyFigureID).setYShift(0);
			}
		} else {
			getCurve().setXShift(0);
			getCurve().setYShift(0);
		}
	}

	/**
	 * Wechsel der visualisierten Kennzahl(en)
	 */
	public void changeVisualizedKeyFigures(int selectedKeyFigureID) {
		this.selectedKeyFigureID = selectedKeyFigureID;
		if (selectedKeyFigureID < 0) {
			setCurves();
		} else {
			setCurve(selectedKeyFigureID);
		}

		this.update();
	}

	void setDragCursorEnabled(boolean enabled) {
		DOM.setStyleAttribute(getElement(), "cursor", enabled ? "move"
				: "default");
	}

	void moveViewport(double dx, double dy) {
		getXAxis().setAxisMin(getXAxis().getAxisMin() + dx);
		getXAxis().setAxisMax(getXAxis().getAxisMax() + dx);
		getYAxis().setAxisMin(getYAxis().getAxisMin() + dy);
		getYAxis().setAxisMax(getYAxis().getAxisMax() + dy);
		update();
	}
	
	void resetViewport() {
		double xLength = getXAxis().getAxisMax() - getXAxis().getAxisMin();
		double yLength = getYAxis().getAxisMax() -  getYAxis().getAxisMin();
		
		getXAxis().setAxisMin(0);
		getXAxis().setAxisMax(xLength);
		getYAxis().setAxisMin(0);
		getYAxis().setAxisMax(yLength);
	}

	public void mouseDownAction(int x, int y) {
		dragging = true;
		xPos = x;
		yPos = y;
		setDragCursorEnabled(true);
	}

	public void mouseMoveAction(int x, int y) {
		if (dragging) {
			int deltaX = x - xPos;
			int deltaY = yPos - y;

			if (multiCurve) {
				for (Integer keyFigureID : orderedKeyFigureIDList) {
					getCurve(keyFigureID).setXShift(deltaX);
					getCurve(keyFigureID).setYShift(deltaY);
				}
			} else {
				getCurve().setXShift(deltaX);
				getCurve().setYShift(deltaY);
			}

			update();
		}
	}

	public void mouseUpAction(int x, int y) {
		dragging = false;
		double deltaX = getXAxis().clientToModel(x)
				- getXAxis().clientToModel(xPos);
		double deltaY = getYAxis().clientToModel(y)
				- getYAxis().clientToModel(yPos);
		moveViewport(-deltaX, -deltaY);

		setDragCursorEnabled(false);

		if (multiCurve) {
			for (Integer keyFigureID : orderedKeyFigureIDList) {
				getCurve(keyFigureID).setXShift(0);
				getCurve(keyFigureID).setYShift(0);
			}
		} else {
			getCurve().setXShift(0);
			getCurve().setYShift(0);
		}
	}

	/**
	 * Hilfsklasse für Werte
	 */
	private class Value {

		/**
		 * Repräsentation der Werte
		 */
		private double x, y;

		/**
		 * Konstruktor
		 * 
		 * @param x
		 *            Zeit
		 * @param y
		 *            Wert
		 */
		public Value(double x, double y) {
			this.x = x;
			this.y = y;
		}

		public double getX() {
			return x;
		}

		public double getY() {
			return y;
		}
	}
}
