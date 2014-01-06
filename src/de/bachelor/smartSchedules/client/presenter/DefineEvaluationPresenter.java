package de.bachelor.smartSchedules.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import de.bachelor.smartSchedules.client.view.DefineEvaluationView;
import de.bachelor.smartSchedules.client.view.util.KeyFigureCanvas;
import de.bachelor.smartSchedules.shared.model.schedule.keyfigures.KeyFigure;

/**
 * Presenter für die Definition der Evaluation
 * 
 * @author Dennis
 */
public class DefineEvaluationPresenter {
	/**
	 * zugrhöriger View
	 */
	private final DefineEvaluationView view;

	/**
	 * Hilfsliste mit den Feldern
	 */
	private final List<KeyFigureCanvas> keyFigureCanvasses;

	/**
	 * Konstruktor
	 */
	public DefineEvaluationPresenter() {
		view = new DefineEvaluationView();

		keyFigureCanvasses = new ArrayList<KeyFigureCanvas>();

		/*
		 * Erstelle pro Kennzahl einen KeyFigureCanvas und füge dem Feld hinzu
		 */
		KeyFigureCanvas keyFigureCanvas;
		for (KeyFigure kf : ScenarioPresenter.getInstance().getKeyFigureList()) {
			keyFigureCanvas = view.generateKeyFigureCanvas(kf);

			view.addKeyFigures(keyFigureCanvas);
			keyFigureCanvasses.add(keyFigureCanvas);
		}

		addListeners();
	}
	
	private void addListeners() {
		/*
		 * Je KeyFigureCanvas, adde Listener für Farbwechsel/Markierung
		 */
		for (KeyFigureCanvas c : keyFigureCanvasses) {
			c.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					for (KeyFigureCanvas c : keyFigureCanvasses) {
						c.setSelected(false);
					}

					((KeyFigureCanvas) (event.getSource())).setSelected(true);

					view.getKeyFigureDescriptionWindow().setTitle(
							((KeyFigureCanvas) (event.getSource())).getName()
									+ " - Description");
					view.getKeyFigureDescription().setContents(
							((KeyFigureCanvas) (event.getSource()))
									.getDescription());

				}
			});
		}
		
		/**
		 * Füge Funktionalität zu Save-Button
		 */
		view.getSaveButton().addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				keyFigureCanvasses.clear();
				for (Canvas c : view.getKeyFiguteStack().getMembers()) {
					keyFigureCanvasses.add((KeyFigureCanvas) c);
				}

				List<KeyFigure> keyFigureList = new ArrayList<KeyFigure>();
				for (KeyFigureCanvas kfc : keyFigureCanvasses) {
					keyFigureList.add(kfc.getKeyFigure());
				}

				ScenarioPresenter.getInstance().setKeyFigureList(keyFigureList);
			}
		});
	}

	public DefineEvaluationView getView() {
		return view;
	}
}
