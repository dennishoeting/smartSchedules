package de.bachelor.smartSchedules.client.presenter;

import de.bachelor.smartSchedules.client.view.SettingsTabView;

/**
 * Presenter für Einstellungs-Tabs (Oper-Gruppierung)
 * @author Dennis
 */
public class SettingsTabPresenter {
	/**
	 * zugehöriger View
	 */
	private final SettingsTabView view;
	
	/**
	 * zugehöriger Presenter (GlobalSettings)
	 */
	private final GlobalSettingsPresenter globalSettingsPresenter;
	
	/**
	 * zugehöriger Presenter (DefineEvaluation)
	 */
	private final DefineEvaluationPresenter defineEvaluationPresenter;
	
	public SettingsTabPresenter() {
		this.view = new SettingsTabView();
		
		this.globalSettingsPresenter = new GlobalSettingsPresenter();		
		this.defineEvaluationPresenter = new DefineEvaluationPresenter();

		this.view.addTab(globalSettingsPresenter.getView());
		this.view.addTab(defineEvaluationPresenter.getView());
	}
	
	public SettingsTabView getView() {
		return view;
	}

	public DefineEvaluationPresenter getDefineEvaluationPresenter() {
		return defineEvaluationPresenter;
	}
}
