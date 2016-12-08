package hr.fer.zemris.java.hw11.jnotepadpp.localization;

/**
 * This class represents a localization provider bridge for the
 * {@linkplain FormLocalizationProvider} whose listener fires a localization
 * change event upon localization change.
 *
 * @author Mario Bobic
 */
public class LocalizationProviderBridge extends AbstractLocalizationProvider {

	/** Indicates that the bridge has been connected. */
	private boolean connected;
	
	/** Localization provider from which strings are taken. */
	private ILocalizationProvider parent;
	
	/** Listener that fires a localization event upon localization change. */
	private ILocalizationListener listener = new ILocalizationListener() {
		@Override
		public void localizationChanged() {
			fire();
		}
	};
	
	/**
	 * Constructs an instance of {@code LocalizationProviderBridge} with the
	 * specified localization provider from which strings are taken.
	 * 
	 * @param parent localization provider from which strings are taken
	 */
	public LocalizationProviderBridge(ILocalizationProvider parent) {
		this.parent = parent;
		connect();
	}

	/**
	 * Connects the bridge by adding a localization listener to the localization
	 * provider.
	 */
	public void connect() {
		if(!connected) {
			parent.addLocalizationListener(listener);
		}
	}
	
	/**
	 * Disconnects the bridge by removing a localization listener to the
	 * localization provider.
	 */
	public void disconnect() {
		if(connected) {
			parent.removeLocalizationListener(listener);
		}
	}
	
	@Override
	public String getString(String key) {
		return parent.getString(key);
	}
	
}
