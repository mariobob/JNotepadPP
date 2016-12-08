package hr.fer.zemris.java.hw11.jnotepadpp.localization;

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
	
	/** Name of the language resource file. */
	@SuppressWarnings("unused")
	private static final String RESOURCE_NAME = "startup_language.dat";

	/**
	 * Disables instantiation.
	 */
	private ResourceLanguage() {
	}
	
	/**
	 * Returns the language from a resource file or
	 * {@linkplain #DEFAULT_LANGUAGE} if the resource file is unavailable.
	 * 
	 * @return the language from a resource file or the default language
	 */
	public static String getResourceLanguage() {
		// TODO: return language from resource file. This is for another time.
		return DEFAULT_LANGUAGE;
	}
	
	/**
	 * Sets the language to the resource file as a new startup language.
	 * If there is no resource file present, it will be created.
	 * 
	 * @param language language to be set to the resource file
	 */
	public static void setResourceLanguage(String language) {
		// TODO: set language to resource file. This is for another time.
	}

}
