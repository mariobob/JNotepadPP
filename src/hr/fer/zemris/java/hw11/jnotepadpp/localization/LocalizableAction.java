package hr.fer.zemris.java.hw11.jnotepadpp.localization;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * This class extends {@linkplain AbstractAction} and represents an Action
 * object that can be localized. A localizable action changes its text (and
 * description, if specified) to the language of the currently used locale.
 *
 * @author Mario Bobic
 */
public abstract class LocalizableAction extends AbstractAction {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;

	/** Key extension of description keys. */
	public static final String DESCRIPTION_EXTENSION = "Desc";
	
	/** The property key. */
	private String key;

	/** Localization provider from which strings are taken. */
	private ILocalizationProvider provider;
	
	/** True if description should also be localized. */
	private boolean withDesc;
	
	/**
	 * Constructs an instance of {@code LocalizableAction} with the specified
	 * parameters.
	 * 
	 * @param key the property key
	 * @param provider localization provider from which strings are taken
	 */
	public LocalizableAction(String key, ILocalizationProvider provider) {
		this(key, provider, true);
	}
	
	/**
	 * Constructs an instance of {@code LocalizableAction} with the specified
	 * parameters.
	 * 
	 * @param key the property key
	 * @param provider localization provider from which strings are taken
	 * @param withDesc true if description should also be localized
	 */
	public LocalizableAction(String key, ILocalizationProvider provider, boolean withDesc) {
		this.key = key;
		this.provider = provider;
		this.withDesc = withDesc;
		
		provider.addLocalizationListener(() -> {
			update();
		});
		update();
	}
	
	/**
	 * Updates the action name and description, if the <tt>withDesc</tt> flag is true.
	 */
	private void update() {
		putValue(Action.NAME, provider.getString(key));
		if (withDesc) {
			putValue(Action.SHORT_DESCRIPTION, provider.getString(key + DESCRIPTION_EXTENSION));
		}
	}
	
	/**
	 * Sets the property key of this action and updates the values.
	 * <p>
	 * This is useful for defining behavior of toggleable actions.
	 * 
	 * @param key key to be set to this action
	 */
	protected void setKey(String key) {
		this.key = key;
		update();
	}

}
