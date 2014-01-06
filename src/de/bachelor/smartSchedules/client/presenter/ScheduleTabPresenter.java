package de.bachelor.smartSchedules.client.presenter;

import de.bachelor.smartSchedules.client.view.ScheduleTabView;

/**
 * Presenter für die ScheduleTabs (Oper-Gruppierung)
 * @author Dennis
 */
public class ScheduleTabPresenter {
	/**
	 * zugehöriger View
	 */
	private final ScheduleTabView view;
	
	/**
	 * zugehöriger Presenter (CurrentSchedule)
	 */
	private final CurrentSchedulePresenter currentSchedulePresenter;
	
	/**
	 * zugehöriger Presenter (ScheduleHistory)
	 */
	private final ScheduleHistoryPresenter scheduleHistoryPresenter;
	
	/**
	 * zugehöriger Presenter (IterativeImprovement)
	 */
	private final IterativeImprovementPresenter iterativeImprovementPresenter;
	
	/**
	 * Konstruktor
	 */
	public ScheduleTabPresenter() {
		this.view = new ScheduleTabView();
		
		this.currentSchedulePresenter = new CurrentSchedulePresenter();
		this.scheduleHistoryPresenter = new ScheduleHistoryPresenter();
		this.iterativeImprovementPresenter = new IterativeImprovementPresenter();
		
		this.view.addTab(currentSchedulePresenter.getView());
		this.view.addTab(scheduleHistoryPresenter.getView());
		this.view.addTab(iterativeImprovementPresenter.getView());
	}
	
	public ScheduleTabView getView() {
		return view;
	}

	public CurrentSchedulePresenter getCurrentSchedulePresenter() {
		return currentSchedulePresenter;
	}

	public ScheduleHistoryPresenter getScheduleHistoryPresenter() {
		return scheduleHistoryPresenter;
	}

	public IterativeImprovementPresenter getIterativeAdvancementPresenter() {
		return iterativeImprovementPresenter;
	}
}
