package hr.fer.zemris.java.hw11.jnotepadpp.localization;

/**
 * The interface {@code ILocalizationProvider} provides methods for registering
 * instances of the {@linkplain ILocalizationListener} and deregistering them
 * and also for getting the string from a key.
 *
 * @author Mario Bobic
 */
public interface ILocalizationProvider {

	/**
	 * Adds the specified <tt>ILocalizationListener l</tt> to a collection of
	 * listeners.
	 * 
	 * @param l localization listener to be added
	 */
	void addLocalizationListener(ILocalizationListener l);

	/**
	 * Removes the specified <tt>ILocalizationListener l</tt> from a collection
	 * of listeners.
	 * 
	 * @param l localization listener to be removed
	 */
	void removeLocalizationListener(ILocalizationListener l);
	
	/**
	 * Returns a {@code String} associated to the specified <tt>key</tt>.
	 * 
	 * @param key key whose string is to be returned
	 * @return a String associated to the specified key
	 */
	String getString(String key);
	
}
