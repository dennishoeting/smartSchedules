package de.bachelor.smartSchedules.client.view;

import com.google.gwt.core.client.GWT;
import com.googlecode.gchart.client.GChart;

import de.bachelor.smartSchedules.client.presenter.ScheduleAlternativesPresenter.ComparisonChartRecord;

/**
 * Defines a traditional "quarterly revenues" grouped bar-chart.
 */
public class ScheduleComparisonChart extends GChart {
	private String[] algorithms;
	private String[] keyFigures;
	private ComparisonChartRecord[] data;
	private final String[] barColors = { "#F55", "#55F", "#5F5", "silver" };
	
	private static final int ALL = -1;

	public ScheduleComparisonChart(int width, int height) {
		setChartSize(width, height);
		setWidth("100%");
		setChartTitle("Alternatives comparison");
		setChartTitleThickness(30);

		getXAxis().setTickLength(0); // eliminates tick marks
		getXAxis().setTickLabelPadding(6); // 6px between label and axis
		getXAxis().setAxisMin(0); // keeps first bar on chart
		getXAxis().setTickLabelThickness(20);

		getYAxis().setTickCount(11);
		getYAxis().setHasGridlines(true);
		update();
	}

	public void setContent(String[] algorithms, String[] keyFigures) {
		this.algorithms = algorithms;
		this.keyFigures = keyFigures;

		for (int i = 0; i < algorithms.length; i++) {
			getXAxis().addTick(
					keyFigures.length / 2. + i * (keyFigures.length + 1),
					"#"+(i+1));
		}

		this.update();
	}

	public void setData(ComparisonChartRecord[] data) {
		if (algorithms == null || data.length != algorithms.length) {
			GWT.log("Data corrupt (AlternativeSchedulesListChart.java)");
			return;
		}

		for (ComparisonChartRecord ccr : data) {
			if (ccr.getKeyFigureAmount() != keyFigures.length) {
				GWT.log("Data corrupt (AlternativeSchedulesListChart.java)");
				return;
			}
		}
		
		this.data = data;

		this.update();
	}
	
	public void draw(int keyFigureId) {
		int maxValue = Integer.MIN_VALUE;
		int minValue = Integer.MAX_VALUE;
		
		this.clearCurves();
		
		if(keyFigureId == ALL) {
			for (int iCurve = 0; iCurve < keyFigures.length; iCurve++) {
				addCurve(iCurve); // one curve per quarter
				getCurve(iCurve).getSymbol().setSymbolType(
						SymbolType.VBAR_SOUTHWEST);
				getCurve(iCurve).getSymbol().setBackgroundColor(barColors[iCurve]);
				getCurve(iCurve).setLegendLabel(keyFigures[iCurve]);
				getCurve(iCurve).getSymbol().setModelWidth(1.0);
				getCurve(iCurve).getSymbol().setBorderColor("#AAA");
				getCurve(iCurve).getSymbol().setBorderWidth(1);
	
				for (int jGroup = 0; jGroup < algorithms.length; jGroup++) {
					// the '+1' creates a bar-sized gap between groups
					getCurve(iCurve).addPoint(1 + iCurve + jGroup * (keyFigures.length + 1), data[jGroup].getValue(iCurve));
					if(data[jGroup].getValue(iCurve)>maxValue) maxValue = data[jGroup].getValue(iCurve);
					if(data[jGroup].getValue(iCurve)<minValue) minValue = data[jGroup].getValue(iCurve);
				}
			}
		} else {
			addCurve(); // one curve per quarter
			getCurve().getSymbol().setSymbolType(
					SymbolType.VBAR_SOUTHWEST);
			getCurve().getSymbol().setBackgroundColor(barColors[keyFigureId]);
			getCurve().setLegendLabel(keyFigures[keyFigureId]);
			getCurve().getSymbol().setModelWidth(1.0);
			getCurve().getSymbol().setBorderColor("#AAA");
			getCurve().getSymbol().setBorderWidth(1);

			for (int jGroup = 0; jGroup < algorithms.length; jGroup++) {
				// the '+1' creates a bar-sized gap between groups
				getCurve().addPoint(1 + jGroup * (keyFigures.length + 1), data[jGroup].getValue(keyFigureId));
				if(data[jGroup].getValue(keyFigureId)>maxValue) maxValue = data[jGroup].getValue(keyFigureId);
				if(data[jGroup].getValue(keyFigureId)<minValue) minValue = data[jGroup].getValue(keyFigureId);
			}
		}

		this.getYAxis().setAxisMax(maxValue + (maxValue/10));
		this.getYAxis().setAxisMin(0);
		
		this.update();
	}
}