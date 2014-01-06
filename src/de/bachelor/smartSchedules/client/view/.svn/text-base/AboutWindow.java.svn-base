package de.bachelor.smartSchedules.client.view;

import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.AnimationCallback;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Window;

import de.bachelor.smartSchedules.client.view.util.Button;
import de.bachelor.smartSchedules.client.view.util.HorizontalStack;
import de.bachelor.smartSchedules.client.view.util.VerticalStack;

/**
 * View für About-Fenster
 * @author Dennis
 */
public class AboutWindow extends Window {
	/**
	 * Größe
	 */
	public static final int WIDTH = 700,
							HEIGHT = 500;
	
	/**
	 * Hintergrundbild des Fensters
	 */
	private final Img image;
	
	/**
	 * Stack für die beiden Bereiche (horizontal)
	 */
	private final HorizontalStack hStack;
	
	/**
	 * Hilfsstack
	 */
	private final VerticalStack leftStack, rightStack;
	
	/**
	 * Content-Bereich
	 */
	private final HTMLPane leftPane, rightPane, bottomPane;
	
	/**
	 * Button zum Regeln der Bereichsanzeige
	 */
	private final Button leftButton, rightButton, bottomButton;
	
	/**
	 * Hilfsvariable
	 */
	private boolean rightIsShown, leftIsShown, bottomIsShown;
	
	/**
	 * Konstruktor
	 * @param title Titel des Fensters
	 */
	public AboutWindow(String title) {
		/*
		 * Initialisierung 
		 */
		this.setTitle(title);
		this.setWidth(AboutWindow.WIDTH);
		this.setHeight(AboutWindow.HEIGHT + 23);
		this.setTop(100);
		this.setLeft(100);
		this.setIsModal(true);
		this.setShowModalMask(true);
		this.setOpacity(0);
		this.setOverflow(Overflow.HIDDEN);
		
		/*
		 * Hintergrundbild
		 */
		this.image = new Img("webcamPic.jpg");
		this.image.setWidth100();
		this.image.setHeight100();
		
		/*
		 * Hauptteil
		 */
		final VerticalStack vStack = new VerticalStack();
		this.hStack = new HorizontalStack();
		
		/*
		 * Linke Seite
		 */
		leftStack = new VerticalStack();
		leftButton = new Button("Dennis Höting", 350);
		leftPane = new HTMLPane();
		leftPane.setBackgroundColor("#fff");
		leftPane.setOpacity(0);
		leftPane.setBorder("1px solid black");
		leftPane.setContents("<i>blabla</b>");
		leftPane.setWidth(350);
		leftPane.setHeight(500-48);
		leftStack.addMembers(leftButton, leftPane);
		
		/*
		 * Rechte Seite
		 */
		rightStack = new VerticalStack();
		rightButton = new Button("Timo Lottmann", 350);
		rightPane = new HTMLPane();
		rightPane.setBackgroundColor("#fff");
		rightPane.setOpacity(0);
		rightPane.setBorder("1px solid black");
		rightPane.setContents("<i>blabla</b>");
		rightPane.setWidth(350);
		rightPane.setHeight(500-48);
		rightStack.addMembers(rightButton, rightPane);
		hStack.addMembers(leftStack, rightStack);
		
		/*
		 * Untere Seite
		 */
		bottomPane = new HTMLPane();
		bottomPane.setHeight(250-48);
		bottomPane.setWidth(700);
		bottomPane.setTop(250+24);
		bottomPane.setBackgroundColor("#fff");
		bottomPane.setBorder("1px solid black");
		bottomPane.setOpacity(0);
		bottomPane.setContents("bla");
		this.bottomButton = new Button("<b>A</b>wesome <b>A</b>pplications", 700);
		
		vStack.addMembers(hStack, bottomButton);
		
		final VerticalStack mainPane = new VerticalStack(VerticalStack.STACK_TYPE_MAIN_STACK);
		mainPane.setOverflow(Overflow.HIDDEN);
		mainPane.addChild(image);
		mainPane.addChild(vStack);
		mainPane.addChild(bottomPane);
		
		this.addItem(mainPane);
	}
	
	/**
	 * Zeigt/Verbirgt rechte Seite
	 */
	public void animateRightPaneFade() {
		/*
		 * Buttons über Zeitspanne der Animation deaktivieren
		 */
		this.disableButtons(true);
		
		/*
		 * Rechte Seite zeigen/verbergen
		 */
		this.rightPane.animateFade((rightIsShown?0:70), new AnimationCallback() {
			@Override
			public void execute(boolean earlyFinish) {
				rightIsShown = !rightIsShown;
				
				/*
				 * Linke Seite verbergen, wenn momentan da
				 */
				if(leftIsShown) {
					leftPane.animateFade(0, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							leftIsShown = !leftIsShown;
							disableButtons(false);
						}
					}, 1000);
				}
				
				/*
				 * Untere Seite verbergen, wenn momentan da
				 */
				if(bottomIsShown) {
					bottomPane.animateFade(0, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							bottomIsShown = !bottomIsShown;
							disableButtons(false);
						}
					}, 1000);
				}
				
				/*
				 * Buttons aktivierne
				 */
				if(!leftIsShown && !bottomIsShown)
					disableButtons(false);
			}
		}, 1000);
	}

	/**
	 * Zeigt/Verbirgt linke Seite
	 */
	public void animateLeftPaneFade() {
		/*
		 * Buttons über Zeitspanne der Animation deaktivieren
		 */
		this.disableButtons(true);
		
		/*
		 * Rechte Seite zeigen/verbergen
		 */
		this.leftPane.animateFade((leftIsShown?0:70), new AnimationCallback() {
			@Override
			public void execute(boolean earlyFinish) {
				leftIsShown = !leftIsShown;
				
				/*
				 * Rechte Seite verbergen, wenn momentan da
				 */
				if(rightIsShown) {
					rightPane.animateFade(0, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							rightIsShown = !rightIsShown;
							disableButtons(false);
						}
					}, 1000);
				}
				
				/*
				 * Untere Seite verbergen, wenn momentan da
				 */
				if(bottomIsShown) {
					bottomPane.animateFade(0, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							bottomIsShown = !bottomIsShown;
							disableButtons(false);
						}
					}, 1000);
				}
				
				/*
				 * Buttons aktivierne
				 */
				if(!rightIsShown && !bottomIsShown)
					disableButtons(false);
			}
		}, 1000);
	}
	
	/**
	 * Zeigt/Verbirgt untere Seite
	 */
	public void animateBottomPaneFade() {
		this.disableButtons(true);
		this.bottomPane.animateFade((bottomIsShown?0:70), new AnimationCallback() {
			@Override
			public void execute(boolean earlyFinish) {
				bottomIsShown = !bottomIsShown;
				
				/*
				 * Linke Seite verbergen, wenn momentan da
				 */
				if(leftIsShown) {
					leftPane.animateFade(0, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							leftIsShown = !leftIsShown;
							disableButtons(false);
						}
					}, 1000);
				}
				
				/*
				 * Rechte Seite verbergen, wenn momentan da
				 */
				if(rightIsShown) {
					rightPane.animateFade(0, new AnimationCallback() {
						@Override
						public void execute(boolean earlyFinish) {
							rightIsShown = !rightIsShown;
							disableButtons(false);
						}
					}, 1000);
				}
				
				/*
				 * Buttons aktivierne
				 */
				if(!leftIsShown && !rightIsShown)
					disableButtons(false);
			}
		}, 1000);
	}
	
	/**
	 * Buttons (de)aktivieren
	 * @param disabled true, wenn deaktiviert werden soll
	 */
	private void disableButtons(boolean disabled) {
		leftButton.setDisabled(disabled);
		rightButton.setDisabled(disabled);
		bottomButton.setDisabled(disabled);
	}

	public Button getLeftButton() {
		return leftButton;
	}

	public Button getRightButton() {
		return rightButton;
	}

	public Button getBottomButton() {
		return bottomButton;
	}

	public HTMLPane getLeftPane() {
		return leftPane;
	}

	public HTMLPane getRightPane() {
		return rightPane;
	}
}
