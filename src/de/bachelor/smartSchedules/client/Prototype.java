package de.bachelor.smartSchedules.client;

import com.google.gwt.core.client.EntryPoint;

import de.bachelor.smartSchedules.client.presenter.TabPresenter;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Prototype implements EntryPoint {
	@Override
	public void onModuleLoad() {
		TabPresenter.getInstance().getView().draw();
	}
}
