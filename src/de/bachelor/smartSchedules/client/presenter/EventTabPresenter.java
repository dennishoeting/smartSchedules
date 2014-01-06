package de.bachelor.smartSchedules.client.presenter;

import de.bachelor.smartSchedules.client.view.EventTabView;

/**
 * Presenter für den EventTab (Ober-Gruppierung)
 * @author Dennis
 */
public class EventTabPresenter {
	/**
	 * zugehöriger View
	 */
	private final EventTabView view;
	
	/**
	 * zugehöriger Presenter (EventHistory)
	 */
	private final EventHistoryPresenter eventHistoryPresenter;
	
	/**
	 * zugehöriger Presenter (InvokeEvent)
	 */
	private final InvokeEventPresenter invokeEventPresenter;
	
	/**
	 * Konstruktor
	 */
	public EventTabPresenter() {
		this.view = new EventTabView();
		
		this.eventHistoryPresenter = new EventHistoryPresenter();
		this.invokeEventPresenter = new InvokeEventPresenter();

		this.view.addTab(eventHistoryPresenter.getView());
		this.view.addTab(invokeEventPresenter.getView());
	}
	
	public EventTabView getView() {
		return view;
	}

	public EventHistoryPresenter getEventHistoryPresenter() {
		return eventHistoryPresenter;
	}

	public InvokeEventPresenter getInvokeEventPresenter() {
		return invokeEventPresenter;
	}
}
