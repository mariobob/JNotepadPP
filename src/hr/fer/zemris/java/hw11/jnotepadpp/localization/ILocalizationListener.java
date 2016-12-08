package hr.fer.zemris.java.hw11.jnotepadpp.localization;

/**
 * This interface represents a listener for change in localization and is
 * associated with the {@linkplain ILocalizationProvider}.
 *
 * @author Mario Bobic
 */
public interface ILocalizationListener {

	/**
	 * Invoked when a change in localization occurs.
	 */
	void localizationChanged();
	
}
