package hr.fer.zemris.java.hw11.jnotepadpp.localization;

import java.util.prefs.Preferences;

/**
 * This class provides static methods for storing and loading of a resource
 * language. In order for an application to remember the last set language, it
 * needs to store it as a resource.
 * <p>
 * This way, when an application language change is made and the application is
 * restarted, the last set language will be set upon loading.
 *
 * @author Mario Bobic
 */
public class ResourceLanguage {
	
	/** The default used language. */
	public static final String DEFAULT_LANGUAGE = "en";
	
	/** Name of the language preference. */
	private static final String PREFERENCE_NAME = "startup_language";
	
	/** The user preferences for choosing startup language. */
	private static final Preferences prefs = Preferences.userNodeForPackage(ResourceLanguage.class);

	/**
	 * Disables instantiation.
	 */
	private ResourceLanguage() {
	}
	
	/**
	 * Returns the language from user preferences or
	 * {@linkplain #DEFAULT_LANGUAGE} if the backing store is inaccessible.
	 * 
	 * @return the language from a user preferences or the default language
	 */
	public static String getResourceLanguage() {
		return prefs.get(PREFERENCE_NAME, DEFAULT_LANGUAGE);
	}
	
	/**
	 * Sets the language to the user preferences as a new startup language.
	 * 
	 * @param language language to be set to the user preferences
	 */
	public static void setResourceLanguage(String language) {
		prefs.put(PREFERENCE_NAME, language);
	}

}
