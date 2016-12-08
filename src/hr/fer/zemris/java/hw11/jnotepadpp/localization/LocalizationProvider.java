package hr.fer.zemris.java.hw11.jnotepadpp.localization;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * This singleton class represents a provider for application localization
 * settings. In order to set the application language, follow the next step:
 * 
 * <pre>
 * <blockquote>
 * LocalizationProvider.getInstance().setLanguage(language);
 * </blockquote>
 * </pre>
 *
 * @author Mario Bobic
 */
public class LocalizationProvider extends AbstractLocalizationProvider {

	/** Language to be set upon application startup. */
	private static final String STARTUP_LANGUAGE = ResourceLanguage.getResourceLanguage();
	
	/** Path to Java Properties files (translations). */
	private static final String TRANSLATION_PATH =
			"hr.fer.zemris.java.hw11.jnotepadpp.translations.translations";
	
	/** The singleton instance of LocalizationProvider. */
	private static LocalizationProvider instance = new LocalizationProvider();
	
	/** Language that is currently being used. */
	private String language;
	/** Locale that is currently being used. */
	private Locale locale;
	/** Bundle that is currently being used. */
	private ResourceBundle bundle;
	
	/**
	 * Constructs an instance of {@code LocalizationProvider} with language set
	 * to {@linkplain #STARTUP_LANGUAGE}.
	 */
	private LocalizationProvider() {
		setLanguage(STARTUP_LANGUAGE);
	}
	
	/**
	 * Returns the singleton {@code LocalizationProvider} instance.
	 * 
	 * @return the single instance of this class
	 */
	public static LocalizationProvider getInstance() {
		return instance;
	}
	
	/**
	 * Sets the language of this provider to the specified <tt>language</tt>.
	 * 
	 * @param language language to be set to this provider
	 */
	public void setLanguage(String language) {
		if (!language.equals(this.language)) {
			this.language = language;
			locale = Locale.forLanguageTag(language);
			bundle = ResourceBundle.getBundle(TRANSLATION_PATH, locale);
			
			fire();
			ResourceLanguage.setResourceLanguage(language);
		}
	}
	
	/**
	 * Returns the locale of this provider that is currently being used.
	 * 
	 * @return the locale that is currently being used
	 */
	public Locale getLocale() {
		return locale;
	}

	@Override
	public String getString(String key) {
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return ResourceBundle.getBundle(
				TRANSLATION_PATH,
				Locale.forLanguageTag(ResourceLanguage.DEFAULT_LANGUAGE)
			).getString(key);
		}
	}
	
}
