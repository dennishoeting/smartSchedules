package de.bachelor.smartSchedules.client.presenter;

import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import de.bachelor.smartSchedules.client.view.AboutWindow;

/**
 * Presenter für das About-Popup
 * @author Dennis
 */
public class AboutPresenter {
	/**
	 * zugehöriger View
	 */
	private final AboutWindow view;
	
	/**
	 * Konstruktor
	 */
	public AboutPresenter() {
		view = new AboutWindow("About us");
		
		this.addListeners();
	}
	
	/**
	 * Hinzufügen der Listener
	 */
	private void addListeners() {
		this.view.getLeftButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.animateRightPaneFade();
			}
		});
		
		this.view.getRightButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.animateLeftPaneFade();
			}
		});
		
		this.view.getBottomButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				view.animateBottomPaneFade();
			}
		});
	}
	
	public AboutWindow getView() {
		return view;
	}
}
