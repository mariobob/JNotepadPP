package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.Image;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

/**
 * A collection of icons used by the {@linkplain JNotepadPP} program.
 *
 * @author Mario Bobic
 */
public class Icons {

	/**
	 * Disables instantiation.
	 */
	private Icons() {
	}

	/** Dimensions of a tab icon. */
	private static final int TAB_SIZE = 12;

	/** Dimensions of a menu icon. */
	private static final int MENU_SIZE = 16;
	
	/** Dimensions of a flag icon. */
	private static final int FLAG_SIZE = 18;

	/** The tab icon of saved state. */
	public static final ImageIcon SAVED = getTabIcon("icons/saved.png");
	
	/** The tab icon of unsaved state. */
	public static final ImageIcon UNSAVED = getTabIcon("icons/unsaved.png");
	
	/** The new tab icon. */
	public static final ImageIcon NEW_TAB = getMenuIcon("icons/newTab.png");
	
	/** The open icon. */
	public static final ImageIcon OPEN = getMenuIcon("icons/open.png");

	/** The save icon. */
	public static final ImageIcon SAVE = getMenuIcon("icons/save.png");

	/** The save as icon. */
	public static final ImageIcon SAVE_AS = getMenuIcon("icons/saveAs.png");

	/** The close tab icon. */
	public static final ImageIcon CLOSE_TAB = getMenuIcon("icons/closeTab.png");

	/** The exit icon. */
	public static final ImageIcon EXIT = getMenuIcon("icons/exit.png");

	/** The cut icon. */
	public static final ImageIcon CUT = getMenuIcon("icons/cut.png");

	/** The copy icon. */
	public static final ImageIcon COPY = getMenuIcon("icons/copy.png");

	/** The paste icon. */
	public static final ImageIcon PASTE = getMenuIcon("icons/paste.png");

	/** The statistics icon. */
	public static final ImageIcon STATISTICS = getMenuIcon("icons/statistics.png");

	/** The to-uppercase icon. */
	public static final ImageIcon TO_UPPER = getMenuIcon("icons/toUpperCase.png");

	/** The to-lowercase icon. */
	public static final ImageIcon TO_LOWER = getMenuIcon("icons/toLowerCase.png");

	/** The invert case icon. */
	public static final ImageIcon INVERT_CASE = getMenuIcon("icons/invertCase.png");

	/** The sort ascending icon. */
	public static final ImageIcon ASCENDING = getMenuIcon("icons/ascending.png");

	/** The sort descending icon. */
	public static final ImageIcon DESCENDING = getMenuIcon("icons/descending.png");

	/** The unique icon. */
	public static final ImageIcon UNIQUE = getMenuIcon("icons/unique.png");

	/** The remove newlines icon. */
	public static final ImageIcon REMOVE_NEWLINES = getMenuIcon("icons/removeNewlines.png");

	/** The show toolbar icon. */
	public static final ImageIcon SHOW_TOOLBAR = getMenuIcon("icons/showToolbar.png");

	/** The hide toolbar icon. */
	public static final ImageIcon HIDE_TOOLBAR = getMenuIcon("icons/hideToolbar.png");

	/** The wrap text icon. */
	public static final ImageIcon WRAP_TEXT = getMenuIcon("icons/wrapText.png");

	/** The unwrap text icon. */
	public static final ImageIcon UNWRAP_TEXT = getMenuIcon("icons/unwrapText.png");
	
	/** The calculate average icon. */
	public static final ImageIcon CALCULATE_AVERAGE = getMenuIcon("icons/calculateAverage.png");
	
	/** The open links icon. */
	public static final ImageIcon OPEN_LINKS = getMenuIcon("icons/openLinks.png");
	
	/** The about icon. */
	public static final ImageIcon ABOUT = getMenuIcon("icons/about.png");
	
	/** The change language icon. */
	public static final ImageIcon CHANGE_LANGUAGE = getMenuIcon("icons/changeLanguage.png");
	
	/** The English (United States) language icon. */
	public static final ImageIcon EN = getFlagIcon("icons/flags/en_us.png");

	/** The German language icon. */
	public static final ImageIcon DE = getFlagIcon("icons/flags/de.png");

	/** The French language icon. */
	public static final ImageIcon FR = getFlagIcon("icons/flags/fr.png");

	/** The Croatian language icon. */
	public static final ImageIcon HR = getFlagIcon("icons/flags/hr.png");

	/** The Japanese language icon. */
	public static final ImageIcon JA = getFlagIcon("icons/flags/ja.png");

	/** The Chinese language icon. */
	public static final ImageIcon ZH = getFlagIcon("icons/flags/zh.png");
	
	
	/**
	 * Loads the resource icon from the specified <tt>path</tt> and returns the
	 * ImageIcon with both height and width scaled to {@linkplain #TAB_SIZE}.
	 * 
	 * @param path path of the resource icon
	 * @return an instance of ImageIcon
	 */
	private static ImageIcon getTabIcon(String path) {
		return getIcon(path, TAB_SIZE);
	}
	
	/**
	 * Loads the resource icon from the specified <tt>path</tt> and returns the
	 * ImageIcon with both height and width scaled to {@linkplain #MENU_SIZE}.
	 * 
	 * @param path path of the resource icon
	 * @return an instance of ImageIcon
	 */
	private static ImageIcon getMenuIcon(String path) {
		return getIcon(path, MENU_SIZE);
	}
	
	/**
	 * Loads the resource icon from the specified <tt>path</tt> and returns the
	 * ImageIcon with both height and width scaled to {@linkplain #FLAG_SIZE}.
	 * 
	 * @param path path of the resource icon
	 * @return an instance of ImageIcon
	 */
	private static ImageIcon getFlagIcon(String path) {
		return getIcon(path, FLAG_SIZE);
	}
	
	/**
	 * Loads the resource icon from the specified <tt>path</tt> by obtaining the
	 * resource as stream, decoding its bytes as an image icon and returns the
	 * ImageIcon with both height and width scaled to the specified <tt>size</tt>.
	 * 
	 * @param path path of the resource icon
	 * @param size the scaling size of the icon
	 * @return an instance of ImageIcon
	 */
	private static ImageIcon getIcon(String path, int size) {
		InputStream is = Icons.class.getResourceAsStream(path);
		if (is == null) {
			throw new InternalError("Resource unavailable: " + path);
		}
		
		byte[] bytes;
		try {
			bytes = readAllBytes(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
		ImageIcon icon = new ImageIcon(bytes);
		Image img = icon.getImage().getScaledInstance(size, size, 0);
		
		return new ImageIcon(img);
	}
	
	/**
	 * Reads all bytes from the specified <tt>InputStream is</tt> and returns
	 * all bytes of the stream.
	 * 
	 * @param is input stream whose bytes are to be read
	 * @return the bytes of the specified input stream
	 * @throws IOException if an I/O exception occurs
	 */
	public static byte[] readAllBytes(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		int len;
		byte[] data = new byte[4096];

		while ((len = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, len);
		}

		buffer.flush();
		return buffer.toByteArray();
	}

	/**
	 * Loads the resource icon from the specified <tt>path</tt> and returns the
	 * ImageIcon with both height and width scaled to {@linkplain #TAB_SIZE}.
	 * 
	 * @param path path of the resource icon
	 * @return an instance of ImageIcon
	 * 
	 * @deprecated Use {@linkplain #getIcon(String, int)} instead.
	 */
	@SuppressWarnings("unused")
	private static ImageIcon loadIcon(String path) {
		java.net.URL imgURL = Icons.class.getResource(path);
		if (imgURL == null) {
			throw new InternalError("Resource unavailable: " + path);
		}
		
		ImageIcon icon = new ImageIcon(imgURL);
		Image img = icon.getImage().getScaledInstance(TAB_SIZE, TAB_SIZE, 0);
		return new ImageIcon(img);
	}

}
