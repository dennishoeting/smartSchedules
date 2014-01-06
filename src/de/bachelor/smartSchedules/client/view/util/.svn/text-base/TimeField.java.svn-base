package de.bachelor.smartSchedules.client.view.util;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;

/**
 * Anordnung von TextItems zur Eingabe einer Uhrzeit
 * @author Dennis
 */
public class TimeField extends HorizontalStack {
	/**
	 * dazugehörige TextItems
	 */
	private TextItem hours,
					 minutes,
					 secounds;
	
	/**
	 * Konstruktor
	 * @param title Titel
	 */
	public TimeField(String title) {
		/*
		 * Stunden
		 */
		DynamicForm hf = new DynamicForm();
		this.hours = new TextItem("", title);
		this.hours.setWrapTitle(false);
		this.hours.setShouldSaveValue(false);
		this.hours.setLength(2);
		this.hours.setWidth(25);
		this.hours.setMask("[0-2][0-9]");
		this.hours.setValue("00");
		hf.setAutoWidth();
		hf.setItems(hours);
		
		/*
		 * Minuten
		 */
		DynamicForm mf = new DynamicForm();
		this.minutes = new TextItem("");
		this.minutes.setShouldSaveValue(false);
		this.minutes.setLength(2);
		this.minutes.setWidth(25);
		this.minutes.setMask("[0-5][0-9]");
		this.minutes.setValue("00");
		mf.setAutoWidth();
		mf.setItems(minutes);
		
		/*
		 * Sekunden
		 */
		DynamicForm sf = new DynamicForm();
		this.secounds = new TextItem("");
		this.secounds.setShouldSaveValue(false);
		this.secounds.setLength(2);
		this.secounds.setWidth(25);
		this.secounds.setMask("[0-5][0-9]");
		this.secounds.setValue("00");
		sf.setAutoWidth();
		sf.setItems(secounds);
		
		/*
		 * Zusammenfügen
		 */
		this.setAutoHeight();
		this.addMembers(hf, mf, sf);
	}
	
	/**
	 * Liefert die Zeit in ms seit dem 1.1.1970 0:00 aus Eingabe
	 */
	public long getTime() {
		if(this.hours.getValue().equals("")
		|| this.minutes.getValue().equals("")
		|| this.secounds.getValue().equals(""))
			return -1;
		
		int hours = Integer.valueOf(this.hours.getValueAsString());
		int minutes = Integer.valueOf(this.minutes.getValueAsString());
		int secounds = Integer.valueOf(this.secounds.getValueAsString());
		
		int hourInMS = 3600*1000;
		int minuteInMS = 60*1000;
		int secoundInMS = 1000;
		
		return hours*hourInMS + minutes*minuteInMS + secounds*secoundInMS;
	}
	
	@SuppressWarnings("deprecation")
	/**
	 * Setzt die Eingabe nach übergebenem Date
	 */
	public void setTime(Date date) {
		String hString = (date.getHours()<10)?("0"+date.getHours()):(date.getHours()+"");
		String mString = (date.getMinutes()<10)?("0"+date.getMinutes()):(date.getMinutes()+"");
		String sString = (date.getSeconds()<10)?("0"+date.getSeconds()):(date.getSeconds()+"");
		
		this.hours.setValue(hString);
		this.minutes.setValue(mString);
		this.secounds.setValue(sString);
	}
	
	/**
	 * Deaktiviert alle Felder
	 */
	public void setDisabled(boolean disabled) {
		this.hours.setDisabled(disabled);
		this.minutes.setDisabled(disabled);
		this.secounds.setDisabled(disabled);
	}
	
	public int getHours() {
		return Integer.valueOf(this.hours.getValueAsString());
	}
	
	public int getMinutes() {
		return Integer.valueOf(this.minutes.getValueAsString());
	}
	
	public int getSeconds() {
		return Integer.valueOf(this.secounds.getValueAsString());
	}
}
