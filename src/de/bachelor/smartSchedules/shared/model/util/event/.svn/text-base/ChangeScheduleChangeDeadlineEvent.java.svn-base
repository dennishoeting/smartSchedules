package de.bachelor.smartSchedules.shared.model.util.event;

import de.bachelor.smartSchedules.client.presenter.ScenarioPresenter;

public class ChangeScheduleChangeDeadlineEvent implements UtilEvent{

	/**
	 * Seriennummer
	 */
	private static final long serialVersionUID = -934590408097785924L;
	
	private int scheduleChangeDeadline;
	
	/**
	 * Default
	 */
	public ChangeScheduleChangeDeadlineEvent() {
		
	}
	
	public ChangeScheduleChangeDeadlineEvent(int newScheduleChangeDeadline) {
		this.scheduleChangeDeadline = newScheduleChangeDeadline;
	}

	@Override
	public void process(ScenarioPresenter scenarioPresenter) {
		
		scenarioPresenter.getScenario().setScheduleChangeDeadline(this.scheduleChangeDeadline);
		
		// TODO Dennis ?
		
	}

	public int getScheduleChangeDeadline() {
		return scheduleChangeDeadline;
	}

	public void setScheduleChangeDeadline(int scheduleChangeDeadline) {
		this.scheduleChangeDeadline = scheduleChangeDeadline;
	}
}
