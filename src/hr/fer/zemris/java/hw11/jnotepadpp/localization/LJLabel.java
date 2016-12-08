package hr.fer.zemris.java.hw11.jnotepadpp.localization;

import javax.swing.JLabel;

/**
 * This class extends the {@linkplain JLabel} class and represents a localizable
 * display area for a short text string. A localizable label changes its text to
 * the language of the currently used locale.
 *
 * @author Mario Bobic
 */
public class LJLabel extends JLabel {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;
	
	/** The property key. */
	private String key;

	/** Localization provider from which strings are taken. */
	private ILocalizationProvider provider;
	
	/**
	 * Constructs an instance of {@code LJLabel} with the specified parameters.
	 * 
	 * @param key the property key
	 * @param provider localization provider from which strings are taken
	 */
	public LJLabel(String key, ILocalizationProvider provider) {
		this.key = key;
		this.provider = provider;
		
		provider.addLocalizationListener(() -> {
			update();
		});
		update();
	}
	
	/**
	 * Updates the text of this component using the key to get a string from the
	 * provider.
	 */
	private void update() {
		setText(provider.getString(key));
	}

}
