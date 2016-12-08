package hr.fer.zemris.java.hw11.jnotepadpp.localization;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

/**
 * Class {@code FormLocalizationProvider} extends the
 * {@linkplain LocalizationProviderBridge} class and is a form localization
 * provider.
 *
 * @author Mario Bobic
 */
public class FormLocalizationProvider extends LocalizationProviderBridge {

	/**
	 * Constructs an instance of {@code FormLocalizationProvider} with the
	 * specified parameters.
	 * 
	 * @param parent localization provider from which strings are taken.
	 * @param frame frame to be connected and disconnected
	 */
	public FormLocalizationProvider(ILocalizationProvider parent, JFrame frame) {
		super(parent);
		
		frame.addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				connect();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				disconnect();
			}
			
		});
	}

}
