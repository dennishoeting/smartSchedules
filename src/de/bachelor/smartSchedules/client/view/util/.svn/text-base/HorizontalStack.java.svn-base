package de.bachelor.smartSchedules.client.view.util;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HStack;

/**
 * Konstruktor
 * @author Dennis
 * @see VerticalStack
 */
public class HorizontalStack extends HStack {
	/**
	 * Hilfsvariablen zur Typisierung
	 */
	public static final int STACK_TYPE_MAIN_STACK = 0,
							STACK_TYPE_SECOUND_ORDER = 1;
	
	/**
	 * Konstanten
	 */
	public static final int MAIN_STACK_MEMBERS_MARGIN = 20,
							MAIN_STACK_PADDING = 20,
							
							SECOUND_ORDER_STACK_MEMBERS_MARGIN = 10,
							SECOUND_ORDER_STACK_PADDING = 0;

	/**
	 * Konstruktor
	 * @param stackType Typ als Int
	 */
	public HorizontalStack(int stackType) {
		super();
		
		switch(stackType) {
		case STACK_TYPE_MAIN_STACK:
			this.setMembersMargin(MAIN_STACK_MEMBERS_MARGIN);
			this.setPadding(MAIN_STACK_PADDING);
			break;
		case STACK_TYPE_SECOUND_ORDER:
			this.setMembersMargin(SECOUND_ORDER_STACK_MEMBERS_MARGIN);
			this.setPadding(SECOUND_ORDER_STACK_PADDING);
			break;
		}
	}
	
	/**
	 * Konstruktor
	 * (MembersMargin und Padding sind 0)
	 */
	public HorizontalStack() {
		this.setMembersMargin(0);
		this.setPadding(0);
	}
	
	/**
	 * Hinzufügen von Members
	 * @param members Member extends Canvas
	 */
	public void addMembers(Canvas... members) {
		for(Canvas member : members) {
			this.addMember(member);
		}
	}
}
