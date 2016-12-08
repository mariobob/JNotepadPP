package hr.fer.zemris.java.hw11.jnotepadpp.localization;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is an abstract implementation of {@linkplain ILocalizationProvider}
 * that contains a list of listeners and has a public {@linkplain #fire()} method.
 *
 * @author Mario Bobic
 */
public abstract class AbstractLocalizationProvider implements ILocalizationProvider {
	
	/** Localization listeners. */
	private List<ILocalizationListener> listeners;
	
	/**
	 * Constructs an instance of {@code AbstractLocalizationProvider}.
	 */
	public AbstractLocalizationProvider() {
		listeners = new ArrayList<>();
	}

	@Override
	public void addLocalizationListener(ILocalizationListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.add(l);
	}

	@Override
	public void removeLocalizationListener(ILocalizationListener l) {
		listeners = new ArrayList<>(listeners);
		listeners.remove(l);
	}
	
	/**
	 * Fires a localization change event to all registered listeners.
	 */
	public void fire() {
		for (ILocalizationListener l : listeners) {
			l.localizationChanged();
		}
	}

}
