package com.github.mefi.jkuuza.gui.model;

import com.github.mefi.jkuuza.gui.AppView;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Class to manage holding and displaying FlashMessages into FlashMessages panel and showing/hiding of header panel. 
 *
 * @author Marek Pilecky
 */
public class FlashMessagesDisplayer {

	private List<FlashMessage> list;
	private static FlashMessagesDisplayer instance;

	private FlashMessagesDisplayer() {
		list = new ArrayList<FlashMessage>();
	}

	/**
	 *  Adds FlashMessage into collection
	 *
	 * @param flashMessage FlashMessage
	 */
	public void add(FlashMessage flashMessage) {
		list.add(flashMessage);
	}

	/**
	 * Return FlashMessage by its index
	 *
	 * @param i index
	 * @return FlashMessage
	 */
	public FlashMessage get(int i) {
		return list.get(i);
	}

	/**
	 * Displays and revalidates panel with FlashMessages in AppView.	 *
	 */
	public void repaint() {
		AppView appView = AppView.getInstance();

		appView.getFlashMessagesPanel().removeAll();
		ListIterator<FlashMessage> it = list.listIterator(list.size());

		while (it.hasPrevious()) {
			appView.getFlashMessagesPanel().add((it.previous().getJLabel()));
		}
		appView.getHeaderPanel().setVisible(true);
		appView.getFlashMessagesPanel().revalidate();
	}

	/**
	 * Removes all FlashMessages from collection
	 */
	public void removeAll() {
		list.clear();
	}

	/**
	 *  Hides FlashMessages panels
	 */
	public void hide() {
		AppView appView = AppView.getInstance();

		appView.getFlashMessagesPanel().removeAll();
		appView.getFlashMessagesPanel().revalidate();
		appView.getHeaderPanel().setVisible(false);
		appView.getHeaderPanel().revalidate();
	}

	/**
	 * Returns FlashMessagesDisplayer singleton
	 *
	 * @return FlashMessagesDisplayer
	 */
	public static synchronized FlashMessagesDisplayer getInstance() {
		if (instance == null) {
			instance = new FlashMessagesDisplayer();
		}
		return instance;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();

	}
}
