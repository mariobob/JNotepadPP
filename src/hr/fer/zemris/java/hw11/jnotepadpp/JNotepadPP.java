package hr.fer.zemris.java.hw11.jnotepadpp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Collator;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.function.Function;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;

import hr.fer.zemris.java.hw11.jnotepadpp.localization.*;

/**
 * <u>The program JNotepadPP</u> is a textual editor equipped with specialized
 * buttons, menus, tabs actions, editing functions and even languages.
 * <p>
 * Almost all actions have shortcut key strokes and mnemonic keys, a name,
 * description and an image icon.
 * <p>
 * <u>The tabs</u> can be opened by double-clicking on the <tt>JTabbedPane</tt>
 * or by hitting the <b>CTRL+N key</b> combination and can be closed by pressing
 * the <b>CTRL+W</b> key combination.
 * <p>
 * File manipulation actions are disabled when no document is present <i>(when
 * there are no tabs opened)</i>. Some text manipulation actions are disabled
 * when no text is selected <i>(i.e. cut, copy actions, sort action and unique
 * action)</i>, and those that remain enabled can manipulate the whole text area
 * <i>(i.e. the case changing actions and statistics action)</i>.
 * <p>
 * <u>The task bar</u> and status bar are both added to the bottom of the
 * window, however <b>the task bar may be moved since it is a floatable
 * object</b>. The task bar can also be hidden from the whole frame.
 * <p>
 * <u>The status bar</u> shows some info about the current editor's caret (line
 * number, column number, selection length) and contains a unique clock that
 * shows date and time.
 * <p>
 * <u>The language words</u> are constants, in a way that every language is on
 * it's own translation. This prevents users to get lost in the application.
 * <p>
 * <u>The about page</u> is translated into every available language and
 * contains some info about the application and its developer.
 *
 * @author Mario Bobic
 * @version 1.0
 */
public class JNotepadPP extends JFrame {
	/** Serialization UID. */
	private static final long serialVersionUID = 1L;
	
	/** Title of the frame. */
	private static final String FRAME_TITLE = "JNotepad++";
	
	/** Form localization provider from which strings are taken. */
	private FormLocalizationProvider flp =
			new FormLocalizationProvider(LocalizationProvider.getInstance(), this);
	
	
	/** Tabs of the JNotepad++. */
	private JTabbedPane tabs;
	/** Current editor that is shown in currently selected tab. */
	private JEditor editor;
	
	/** Clock to be shown in the lower-right corner of the frame. */
	private Clock clock;
	/** Status bar that holds status info of the current tab. */
	private StatusBar statusBar;
	/** The toolbar which contains actions. */
	private JToolBar toolBar;
	
	/** Cached instance of file chooser for remembering last place. */
	private JFileChooser fileChooser;
	
	/**
	 * Constructs and initializes this frame with GUI components.
	 */
	public JNotepadPP() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle(FRAME_TITLE);
		
		configureClosing();
		
		initGUI();
		
		pack();
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setSize(getSize().width, screen.height / 2);

		setLocationRelativeTo(null);
	}

	/**
	 * Initializes the GUI first by initializing all fields of this class and
	 * then creating actions, menus, the toolbar and the status bar. A new tab
	 * is opened at first.
	 */
	private void initGUI() {
		tabs = getJTabbedPane();
		editor = new JEditor();
		
		clock = new Clock();
		statusBar = new StatusBar();
		toolBar = createToolbars();
		
		fileChooser = new JFileChooser();
		
		Container cp = getContentPane();
		
		cp.setLayout(new BorderLayout());
		cp.add(tabs);
		newTab(null, editor);
		
		createActions();
		createMenus();
		
		JPanel barsPanel = new JPanel(new BorderLayout());
		cp.add(barsPanel, BorderLayout.PAGE_END);
		
		barsPanel.add(toolBar, BorderLayout.CENTER);
		barsPanel.add(statusBar, BorderLayout.PAGE_END);
	}

	/**
	 * Returns an instance of {@linkplain JTabbedPane} with a
	 * mouse listener and a {@linkplain ChangeListener}.
	 * <p>
	 * The mouse listener listens for double clicks and opens a new tab if the
	 * user double-clicked on the tab pane.
	 * <p>
	 * The change listener listens for a state change and updates program
	 * information based on the currently opened tab. The information that is
	 * considered is:
	 * <ul>
	 * <li>the <tt>editor</tt> reference is changed to the currently opened
	 * editor in the currently opened tab,
	 * <li>if there are no tabs open (if <tt>editor == null</tt>), actions that
	 * are performed onto files are disabled and the method ends,
	 * <li>else these actions are re-enabled and the tab name and tooltip are
	 * updated.
	 * </ul>
	 * 
	 * @return an instance of JTabbedPane with listeners
	 */
	private JTabbedPane getJTabbedPane() {
		JTabbedPane pane = new JTabbedPane();
		
		pane.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					newAction.actionPerformed(null);
				}
			}
		});
		
		pane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				editor = getJEditorAt(pane.getSelectedIndex());
				
				caretListener.caretUpdate(dummyCaretEvent);
				
				if (editor == null) {
					setEnabled(false,
						saveAction, saveAsAction, closeTabAction,
						pasteAction, statisticsAction,
						toUppercaseAction, toLowercaseAction, invertCaseAction
					);
					return;
				} else {
					setEnabled(true,
						saveAction, saveAsAction, closeTabAction,
						pasteAction, statisticsAction,
						toUppercaseAction, toLowercaseAction, invertCaseAction
					);
				}
				
				setTitle(editor.getName() + " - " + FRAME_TITLE);
			}
		});
		
		flp.addLocalizationListener(() -> {
			for (int i = 0, n = pane.getTabCount(); i < n; i++) {
				JEditor editor = getJEditorAt(i);
				if (editor.filePath == null) {
					editor.setName(flp.getString("untitled"));
					pane.setTitleAt(i, flp.getString("untitled"));
				}
			}
			
			// Fire state changed (possibly to update window title)
			ChangeEvent e = new ChangeEvent(pane);
			for (ChangeListener l : pane.getChangeListeners()) {
				l.stateChanged(e);
			}
		});
		
		return pane;
	}

	/**
	 * Configures the closing action by adding a {@linkplain WindowAdapter}
	 * that calls the {@linkplain #closeTab(int)} method for each tab and
	 * finally disposes the window.
	 * <p>
	 * If the {@linkplain JOptionPane#CANCEL_OPTION} was chosen at the time of
	 * tab-closing, the procedure is halted and the frame remains active.
	 */
	private void configureClosing() {
		addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosing(WindowEvent e) {
				for (int i = 0, n = tabs.getTabCount(); i < n; i++) {
					boolean goOn = closeTab(0); // close all tabs
					if (!goOn) return;
				}
				dispose();
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				clock.stop();
			}
			
		});
	}
	
	/**
	 * Opens a new tab with the specified <tt>path</tt> and <tt>editor</tt>.
	 * <p>
	 * The tab name is set to the path's file name (obtained by the
	 * {@link Path#getFileName() path.getFileName()} method) and the tab tooltip
	 * is set to the absolute path (obtained by calling
	 * {@link Path#toAbsolutePath() path.toAbsolutePath()} method).
	 * <p>
	 * If the specified <tt>path</tt> is <tt>null</tt>, the tab name is set to
	 * untitled with no tooltip.
	 * 
	 * @param path path to file of the editor
	 * @param editor editor to be added to the new tab
	 */
	private void newTab(Path path, JEditor editor) {
		editor.setFilePath(path);
		String name = editor.getName();
		
		if (path != null) {
			String fullPath = path.toAbsolutePath().toString();
			tabs.addTab(name, Icons.SAVED, new JScrollPane(editor), fullPath);
		} else {
			tabs.addTab(name, Icons.SAVED, new JScrollPane(editor));
		}
		
		this.editor = editor;
		editor.addCaretListener(caretListener);

		tabs.setSelectedIndex(tabs.getTabCount() - 1);
	}
	
	/**
	 * Closes the tab at the specified <tt>index</tt> and returns the closing
	 * status. If the specified <tt>index</tt> is invalid (less than zero or
	 * greater than or equal to the tab count), or if the
	 * {@linkplain JOptionPane#CANCEL_OPTION} is chosen, this method returns
	 * <tt>false</tt>. Else the tab is successfully closed and <tt>true</tt> is
	 * returned.
	 * 
	 * @param index index of the tab to be closed
	 * @return true if the tab was closed, false if not
	 */
	private boolean closeTab(int index) {
		if (index < 0 || index >= tabs.getTabCount()) {
			return false;
		}
		
		JEditor editor = getJEditorAt(index);
		
		if (editor.isChanged()) {
			tabs.setSelectedIndex(index);
			
			int decision = JOptionPane.showConfirmDialog(
				JNotepadPP.this,
				flp.getString("doYouWantSaveChanges") + " " + editor.getName() + "?",
				flp.getString("saveChangesQ"),
				JOptionPane.YES_NO_CANCEL_OPTION);
				
			if (decision == JOptionPane.YES_OPTION) {
				JNotepadPP.this.editor = editor;
				saveAction.actionPerformed(null);
			} else if (decision == JOptionPane.CANCEL_OPTION) {
				return false;
			}
		}
		
        tabs.remove(index);
        return true;
	}
	
	/**
	 * Sets the enabled state of the specified <tt>Action</tt> objects.
	 * 
	 * @param enabled state to be set to all actions
	 * @param actions actions whose state is to be set
	 */
	private void setEnabled(boolean enabled, Action ...actions) {
		for (Action action : actions) {
			action.setEnabled(enabled);
		}
	}
	
	/**
	 * The caret listener is a listener for changes in the caret position of a
	 * text component. When the caret of the current <tt>editor</tt> is updated,
	 * the status bar information is updated.
	 * <p>
	 * If there is no selection present, all selection actions will be disabled.
	 */
	private CaretListener caretListener = new CaretListener() {
		
		@Override
		public void caretUpdate(CaretEvent e) {
			statusBar.updateStatus();
			
			// if no selection is present
			if (e.getDot() - e.getMark() == 0) {
				setEnabled(false,
					cutAction, copyAction,
					sortAscendingAction, sortDescendingAction,
					uniqueAction
				);
			} else {
				setEnabled(true,
					cutAction, copyAction,
					sortAscendingAction, sortDescendingAction,
					uniqueAction
				);
			}
		}
	};
	
	/**
	 * The dummy caret event represents an event for force-updating the
	 * {@linkplain #caretListener}.
	 */
	private CaretEvent dummyCaretEvent = new CaretEvent(this) {
		/** Serialization UID. */
		private static final long serialVersionUID = 1L;

		@Override
		public int getMark() {
			return editor == null ? 0 : editor.getCaret().getMark();
		}
		@Override
		public int getDot() {
			return editor == null ? 0 : editor.getCaret().getDot();
		}
	};
	
	/**
	 * Returns a <tt>JEditor</tt> at the specified <tt>index</tt>. Since
	 * JEditors are wrapped in a <tt>JScrollPane</tt>, this method unpacks the
	 * scroll pane and returns a JEditor. If the specified index is less than
	 * zero or greater or equal to the current tab count, <tt>null</tt> is
	 * returned.
	 * 
	 * @param index index of the <tt>JTabbedPane</tt>
	 * @return a <tt>JEditor</tt> at the specified <tt>index</tt>
	 */
	private JEditor getJEditorAt(int index) {
		if (index < 0 || index >= tabs.getTabCount()) {
			return null;
		}
		
		JViewport viewport = ((JScrollPane) tabs.getComponentAt(index)).getViewport();
		return (JEditor) viewport.getView();
	}
	
	///////////////////////////////////////////////////////////////////////////
	//////////////////////////////// ACTIONS //////////////////////////////////
	///////////////////////////////////////////////////////////////////////////
	
	//
    // File actions
    //
	
	/**
	 * Action that opens a new editor in a new tab.
	 */
	private Action newAction = new LocalizableAction("new", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			newTab(null, new JEditor());
		}
	};
	
	/**
	 * Action that opens an existing document and loads it in a new tab.
	 */
	private Action openAction = new LocalizableAction("open", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle(flp.getString("openFile"));
			
			int retVal = fileChooser.showOpenDialog(JNotepadPP.this);
			if (retVal != JFileChooser.APPROVE_OPTION) {
				return;
			}
			
			File filename = fileChooser.getSelectedFile();
			Path filepath = filename.toPath().toAbsolutePath();
			
			byte[] bytes;
			try {
				bytes = Files.readAllBytes(filepath);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(
					JNotepadPP.this,
					flp.getString("errorReadingFile") + " " + filepath,
					flp.getString("error"),
					JOptionPane.ERROR_MESSAGE
				);
				return;
			}
			
			String text = new String(bytes, StandardCharsets.UTF_8);
			checkTab();
			newTab(filepath, new JEditor(text));
		}

		/**
		 * Checks if the current tab is an empty tab (if the current editor file
		 * path is <tt>null</tt>) and closes the current tab if true.
		 */
		private void checkTab() {
			if (editor.filePath == null) {
				closeTab(tabs.getSelectedIndex());
			}
		}
	};
	
	/**
	 * Action that saves the current document to its file path.
	 * <p>
	 * If there is no file path present, that is if <tt>filePath</tt> is
	 * <tt>null</tt>, the {@linkplain #saveAsDialog()} method is called to ask
	 * the user where he wants the document to be saved.
	 */
	private Action saveAction = new LocalizableAction("save", flp) {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			fileChooser.setDialogTitle(flp.getString("saveFile"));
			
			if (editor.filePath == null) {
				saveAsDialog();
				if (editor.filePath == null) return;
			}
			
			byte[] podaci = editor.getText().getBytes(StandardCharsets.UTF_8);
			try {
				Files.write(editor.filePath, podaci);
			} catch (IOException ex) {
				JOptionPane.showMessageDialog(
					JNotepadPP.this,
					flp.getString("errorSavingFile") + " "  + editor.filePath.getFileName(),
					flp.getString("error"),
					JOptionPane.ERROR_MESSAGE
				);
				return;
			}
			
			updateTab();
			editor.setChanged(false);
		}
		
		/**
		 * Updates the tab information by setting the title and tooptip text
		 * to the current <tt>editor</tt>'s file name and absolute path.
		 */
		private void updateTab() {
			String name = editor.getName();
			String fullPath = editor.filePath.toAbsolutePath().toString();
			
			tabs.setTitleAt(tabs.getSelectedIndex(), name);
			tabs.setToolTipTextAt(tabs.getSelectedIndex(), fullPath);
			
			setTitle(name + " - " + FRAME_TITLE);
		}
	};
	
	/**
	 * Action that saves the current document to a user-specified path.
	 */
	private Action saveAsAction = new LocalizableAction("saveAs", flp) {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean saved = saveAsDialog();
			if (saved) {
				saveAction.actionPerformed(e);
			}
		}
	};
	
	/**
	 * Shows a "Save As" dialog and prompts the user to save the current
	 * document.
	 * <p>
	 * If the user chooses to save the file, <tt>true</tt> is returned. Else
	 * <tt>false</tt> is returned.
	 * 
	 * @return true if the fale is to be saved, false otherwise
	 */
	private boolean saveAsDialog() {
		fileChooser.setDialogTitle(flp.getString("saveAs"));
		
		int retVal = fileChooser.showSaveDialog(JNotepadPP.this);
		if (retVal != JFileChooser.APPROVE_OPTION) {
			return false;
		} else {
			Path path = fileChooser.getSelectedFile().toPath();
			if (Files.exists(path)) {
				int decision = JOptionPane.showConfirmDialog(
					this,
					flp.getString("file") + " "  + path.getFileName() + " "  + flp.getString("alreadyExists"),
					flp.getString("confirmSaveAs"),
					JOptionPane.YES_NO_OPTION
				);
				if (decision == JOptionPane.NO_OPTION) {
					return false;
				}
			}
			
			editor.setFilePath(path);
			return true;
		}
	}
	
	/**
	 * Closes the current tab by calling the {@linkplain #closeTab(int)} method.
	 */
	private Action closeTabAction = new LocalizableAction("closeTab", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			closeTab(tabs.getSelectedIndex());
		}
	};
	
	/**
	 * Exits the application by dispatching a
	 * {@linkplain WindowEvent#WINDOW_CLOSING} event.
	 */
	private Action exitAction = new LocalizableAction("exit", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JNotepadPP.this.dispatchEvent(
				new WindowEvent(JNotepadPP.this, WindowEvent.WINDOW_CLOSING)
			);
		}
	};
	
	//
    // Edit actions
    //
	
	/**
	 * Cuts the currently selected text from the current editor to clipboard.
	 */
	private Action cutAction = new LocalizableAction("cut", flp) {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.cut();
		}
	};
	
	/**
	 * Copies the currently selected text from the current editor to clipboard.
	 */
	private Action copyAction = new LocalizableAction("copy", flp) {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.copy();
		}
	};
	
	/**
	 * Pastes the text from clipboard to the current editor.
	 */
	private Action pasteAction = new LocalizableAction("paste", flp) {
		private static final long serialVersionUID = 1L;

		@Override
		public void actionPerformed(ActionEvent e) {
			editor.paste();
		}
	};
	
	/**
	 * Shows the current document statistics in a new dialog.
	 */
	private Action statisticsAction = new LocalizableAction("statistics", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(
				JNotepadPP.this,
				getStatsPanel(),
				flp.getString("statistics"),
				JOptionPane.INFORMATION_MESSAGE
			);
		}
		
		/**
		 * Returns an instance of <tt>JPanel</tt> with the <tt>GridLayout</tt>
		 * manager, a titled border and document statistics of the next
		 * attributes:
		 * <ol>
		 * <li>number of characters not including spaces,
		 * <li>number of characters including spaces and
		 * <li>number of lines.
		 * </ol>
		 * 
		 * @return a JPanel with document statistics
		 */
		private JPanel getStatsPanel() {
			JPanel statsPanel = new JPanel(new GridLayout(0, 2, 10, 0));
			statsPanel.setBorder(BorderFactory.createTitledBorder(flp.getString("statistics")));
			
			statsPanel.add(new JLabel(flp.getString("charactersNoSpaces")));
			statsPanel.add(new JLabel(charactersNoSpaces(), JLabel.CENTER));
			
			statsPanel.add(new JLabel(flp.getString("charactersWithSpaces")));
			statsPanel.add(new JLabel(charactersWithSpaces(), JLabel.CENTER));
			
			statsPanel.add(new JLabel(flp.getString("lines")));
			statsPanel.add(new JLabel(lines(), JLabel.CENTER));
			
			return statsPanel;
		}
		
		/**
		 * Returns the number of characters in the current document not
		 * including whitespace characters as a String.
		 * 
		 * @return the number of characters not including whitespaces
		 */
		private String charactersNoSpaces() {
			String text = editor.getText().replaceAll("\\s+", "");
			return Integer.toString(text.length());
		}
		
		/**
		 * Returns the number of characters in the current document including
		 * whitespace characters as a String.
		 * 
		 * @return the number of characters including whitespaces
		 */
		private String charactersWithSpaces() {
			return Integer.toString(documentLength());
		}
		
		/**
		 * Returns the number of lines in the current document as a String.
		 * 
		 * @return the number of lines in the document
		 */
		private String lines() {
			return Integer.toString(editor.getLineCount());
		}
	};
	
	/**
	 * Returns the length of the current document.
	 * 
	 * @return the length of the current document
	 */
	private int documentLength() {
		return editor.getText().length();
	}
	
	//
    // Tools actions
    //
	
	/**
	 * Changes case in selected part of text or in entire document to uppercase.
	 */
	private Action toUppercaseAction = new LocalizableAction("toUppercase", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			modifyText(Character::toUpperCase);
		}
	};
	
	/**
	 * Changes case in selected part of text or in entire document to lowercase.
	 */
	private Action toLowercaseAction = new LocalizableAction("toLowercase", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			modifyText(Character::toLowerCase);
		}
	};
	
	/**
	 * Inverts character case in selected part of text or in entire document.
	 */
	private Action invertCaseAction = new LocalizableAction("invertCase", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			modifyText((c) -> {
				if (Character.isLowerCase(c)) {
					return Character.toUpperCase(c);
				} else {
					return Character.toLowerCase(c);
				}
			});
		}
	};
	
	/**
	 * Modifies the text of the current <tt>editor</tt>, character by character,
	 * with the specified <tt>function</tt>.
	 * <p>
	 * If some text in the document is selected, the function only applies to
	 * the selected characters. Else the function is applied to the entire
	 * document.
	 * 
	 * @param function function for characters
	 */
	private void modifyText(Function<Character, Character> function) {
		Document doc = editor.getDocument();
		Caret caret = editor.getCaret();
		
		int len = Math.abs(caret.getDot() - caret.getMark());
		int offset = 0;
		if (len != 0) {
			offset = Math.min(caret.getDot(), caret.getMark());
		} else {
			len = doc.getLength();
		}
		
		try {
			int dotPosition = caret.getDot();
			int markPosition = caret.getMark();
			
			String text = doc.getText(offset, len);
			text = changeCase(text, function);
			doc.remove(offset, len);
			doc.insertString(offset, text, null);
			
			caret.setDot(markPosition);
			caret.moveDot(dotPosition);
		} catch (BadLocationException ex) {
			throw new InternalError(ex);
		}
	}
	
	/**
	 * Changes the casing of the <tt>text</tt>, or generally speaking modifies
	 * the text, character by character, with the specified <tt>function</tt>
	 * and returns the modified text.
	 * 
	 * @param text text to be modified
	 * @param function function which modifies the text
	 * @return the modified text
	 */
	private static String changeCase(String text, Function<Character, Character> function) {
		char[] znakovi = text.toCharArray();
		
		for (int i = 0; i < znakovi.length; i++) {
			znakovi[i] = function.apply(znakovi[i]);
		}
		
		return new String(znakovi);
	}
	
	/**
	 * Sorts the selected lines of text in an ascending order.
	 */
	private Action sortAscendingAction = new LocalizableAction("sortAscending", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			sortLines(true);
		}
	};
	
	/**
	 * Sorts the selected lines of text in a descending order.
	 */
	private Action sortDescendingAction = new LocalizableAction("sortDescending", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			sortLines(false);
		}
	};
	
	/**
	 * Sorts the selected lines of text in an order specified by the
	 * <tt>ascending</tt> boolean flag.
	 * <p>
	 * The sorting is done with a collator of the current locale.
	 * 
	 * @param ascending the specified order (ascending or descending)
	 */
	private void sortLines(boolean ascending) {
		Locale locale = LocalizationProvider.getInstance().getLocale();
		Collator collator = Collator.getInstance(locale);
		
		try {
			int line1 = editor.getLineOfOffset(editor.getCaret().getDot());
			int line2 = editor.getLineOfOffset(editor.getCaret().getMark());
			
			int startLine = Math.min(line1, line2);
			int endLine = Math.max(line1, line2);
			
			List<String> lines = getLines(startLine, endLine);
			Collections.sort(lines, ascending ? collator : collator.reversed());
			replaceLines(lines, startLine, endLine);
		} catch (BadLocationException e) {
			throw new InternalError(e);
		}
	}

	/**
	 * Returns a list of lines from the <tt>startLine</tt> to the
	 * <tt>endLine</tt>.
	 * 
	 * @param startLine the starting line
	 * @param endLine the ending line
	 * @return a list of lines from the starting line to the ending line
	 * @throws BadLocationException if {@linkplain JEditor} exception occurs
	 */
	private List<String> getLines(int startLine, int endLine) throws BadLocationException {
		List<String> lines = new ArrayList<>();
		
		for (int i = startLine; i <= endLine; i++) {
			lines.add(getLineText(i));
		}
		
		return lines;
	}
	
	/**
	 * Returns the text of the specified <tt>line</tt>, without a newline
	 * character.
	 * 
	 * @param line index of the line whose text is to be returned
	 * @return the text of the specified line
	 * @throws BadLocationException if {@linkplain JEditor} exception occurs
	 */
	private String getLineText(int line) throws BadLocationException {
		int start = editor.getLineStartOffset(line);
		int end = editor.getLineEndOffset(line);
		
		return editor.getText(start, end - start)
				.replaceAll("\\r|\\n", "");
	}
	
	/**
	 * Replaces all lines from the <tt>startLine</tt> to the <tt>endLine</tt>
	 * with the specified list of <tt>lines</tt>, inserting a newline character
	 * after every list element except the last one.
	 * 
	 * @param lines list of lines to be inserted to document
	 * @param startLine the starting line
	 * @param endLine the ending line
	 * @throws BadLocationException if {@linkplain JEditor} exception occurs
	 */
	private void replaceLines(List<String> lines, int startLine, int endLine) throws BadLocationException {
		Document doc = editor.getDocument();
		
		StringJoiner sj = new StringJoiner("\n");
		for (String line : lines) {
			sj.add(line);
		}
		
		int start = editor.getLineStartOffset(startLine);
		int end = editor.getLineEndOffset(endLine);
		
		doc.remove(start, end-start);
		doc.insertString(start, sj.toString(), null);
		
		editor.select(start, end);
	}
	
	/**
	 * Removes all duplicate lines in selected part of text.
	 */
	private Action uniqueAction = new LocalizableAction("unique", flp) {
		private static final long serialVersionUID = 1L;
		
		@Override
		public void actionPerformed(ActionEvent e) {
			try {
				int line1 = editor.getLineOfOffset(editor.getCaret().getDot());
				int line2 = editor.getLineOfOffset(editor.getCaret().getMark());
				
				int startLine = Math.min(line1, line2);
				int endLine = Math.max(line1, line2);
				
				List<String> lines = getLines(startLine, endLine);
				lines = removeDuplicates(lines);
				replaceLines(lines, startLine, endLine);
			} catch (BadLocationException ex) {
				throw new InternalError(ex);
			}
		}

		/**
		 * Removes the duplicate elements from the specified <tt>lines</tt> list
		 * and returns a new <tt>List</tt> object. The specified list remains
		 * unmodified.
		 * 
		 * @param lines list whose duplicates are to be removed
		 * @return a new list with unique elements
		 */
		private List<String> removeDuplicates(List<String> lines) {
			return new ArrayList<>(new LinkedHashSet<>(lines));
		}
	};
	
	/**
	 * Shows/hides the floatable toolbar.
	 */
	private Action showHideToolbarAction = new LocalizableAction("hideToolbar", flp) {
		private static final long serialVersionUID = 1L;		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			toolBar.setVisible(!toolBar.isVisible());
			if (!toolBar.isVisible()) {
				// programmatically click X if toolBar is floating
				setKey("showToolbar");
				showHideToolbarAction.putValue(Action.SMALL_ICON, Icons.SHOW_TOOLBAR);
			} else {
				setKey("hideToolbar");
				showHideToolbarAction.putValue(Action.SMALL_ICON, Icons.HIDE_TOOLBAR);
			}
		}
	};
	
	//
    // Help actions
    //
	
	/**
	 * Shows information about this program.
	 */
	private Action aboutAction = new LocalizableAction("about", flp) {
		private static final long serialVersionUID = 1L;		
		
		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(
				JNotepadPP.this,
				flp.getString("createdBy"),
				flp.getString("about") + " " + FRAME_TITLE,
				JOptionPane.INFORMATION_MESSAGE);
		}
	};
	
	
	///////////////////////////////////////////////////////////////////////////
	//////////////////////////////// UTILITY //////////////////////////////////
	///////////////////////////////////////////////////////////////////////////

	//
    // Program utility methods
    //
	
	/**
	 * Creates all <tt>Action</tt> objects in order for this program to work.
	 */
	private void createActions() {
		putActionValue(newAction, "control N", KeyEvent.VK_N, Icons.NEW_TAB);
		putActionValue(openAction, "control O", KeyEvent.VK_O, Icons.OPEN);
		putActionValue(saveAction, "control S", KeyEvent.VK_S, Icons.SAVE);
		putActionValue(saveAsAction, "control shift S", KeyEvent.VK_A, Icons.SAVE_AS);
		putActionValue(closeTabAction, "control W", KeyEvent.VK_C, Icons.CLOSE_TAB);
		putActionValue(exitAction, "control X", KeyEvent.VK_X, Icons.EXIT);
		
		putActionValue(cutAction, "control X", KeyEvent.VK_T, Icons.CUT);
		putActionValue(copyAction, "control C", KeyEvent.VK_C, Icons.COPY);
		putActionValue(pasteAction, "control V", KeyEvent.VK_P, Icons.PASTE);
		putActionValue(statisticsAction, "shift S", KeyEvent.VK_S, Icons.STATISTICS);
		
		putActionValue(toUppercaseAction, "control F1", KeyEvent.VK_U, Icons.TO_UPPER);
		putActionValue(toLowercaseAction, "control F2", KeyEvent.VK_L, Icons.TO_LOWER);
		putActionValue(invertCaseAction, "control F3", KeyEvent.VK_I, Icons.INVERT_CASE);
		putActionValue(sortAscendingAction, "control shift UP", KeyEvent.VK_A, Icons.ASCENDING);
		putActionValue(sortDescendingAction, "control shift DOWN", KeyEvent.VK_D, Icons.DESCENDING);
		putActionValue(uniqueAction, "control U", KeyEvent.VK_U, Icons.UNIQUE);
		
		putActionValue(showHideToolbarAction, "control shift T", KeyEvent.VK_T, Icons.HIDE_TOOLBAR);
		
		putActionValue(aboutAction, "F1", KeyEvent.VK_A, Icons.ABOUT);
		aboutAction.putValue(Action.NAME, aboutAction.getValue(Action.NAME) + " " + FRAME_TITLE);
		
		// invoke caret change to disable some actions
		caretListener.caretUpdate(dummyCaretEvent);
	}
	
	/**
	 * Puts the specified values to the specified <tt>action</tt> object.
	 * <p>
	 * Action values may be non-existent, which is useful if a value is not
	 * wanted in the <tt>Action</tt> object.
	 * 
	 * @param action the action
	 * @param keyStroke accelerator key, may be <tt>null</tt>
	 * @param mnemonic mnemonic key, may be <tt>-1</tt>
	 * @param icon small icon of the action, may be <tt>null</tt>
	 */
	private static void putActionValue(Action action, String keyStroke, int mnemonic, ImageIcon icon) {
		if (keyStroke != null) {
			action.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyStroke));
		}
		
		if (mnemonic != -1) {
			action.putValue(Action.MNEMONIC_KEY, mnemonic);
		}
		
		if (icon != null) {
			action.putValue(Action.SMALL_ICON, icon);
		}
	}

	/**
	 * Creates the program menus with menu items referenced to action objects.
	 */
	private void createMenus() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		/* File menu */
		JMenu fileMenu = new JMenu(getAction("file"));
		menuBar.add(fileMenu);

		fileMenu.add(new JMenuItem(newAction));
		fileMenu.add(new JMenuItem(openAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(saveAction));
		fileMenu.add(new JMenuItem(saveAsAction));
		fileMenu.addSeparator();
		fileMenu.add(new JMenuItem(closeTabAction));
		fileMenu.add(new JMenuItem(exitAction));

		
		/* Edit menu */
		JMenu editMenu = new JMenu(getAction("edit"));
		menuBar.add(editMenu);

		editMenu.add(new JMenuItem(cutAction));
		editMenu.add(new JMenuItem(copyAction));
		editMenu.add(new JMenuItem(pasteAction));
		editMenu.addSeparator();
		editMenu.add(new JMenuItem(statisticsAction));

		
		/* Tools menu */
		JMenu toolsMenu = new JMenu(getAction("tools"));
		menuBar.add(toolsMenu);
		
		// Change case menu
		JMenu changeCaseMenu = new JMenu(getAction("changeCase"));
		toolsMenu.add(changeCaseMenu);

		changeCaseMenu.add(new JMenuItem(toUppercaseAction));
		changeCaseMenu.add(new JMenuItem(toLowercaseAction));
		changeCaseMenu.add(new JMenuItem(invertCaseAction));
		
		// Sort menu
		JMenu sortMenu = new JMenu(getAction("sort"));
		toolsMenu.add(sortMenu);

		sortMenu.add(new JMenuItem(sortAscendingAction));
		sortMenu.add(new JMenuItem(sortDescendingAction));
		
		toolsMenu.add(new JMenuItem(uniqueAction));
		toolsMenu.addSeparator();
		
		// Change language menu
		JMenu changeLanguageMenu = createLanguageMenu();
		toolsMenu.add(changeLanguageMenu);
		
		toolsMenu.add(new JMenuItem(showHideToolbarAction));
		
		
		/* Help menu */
		JMenu helpMenu = new JMenu(getAction("help"));
		menuBar.add(helpMenu);
		
		helpMenu.add(new JMenuItem(aboutAction));
	}
	
	/**
	 * Creates and returns a language <tt>JMenu</tt> component.
	 * 
	 * @return a language menu
	 */
	private JMenu createLanguageMenu() {
		JMenu changeLanguageMenu = new JMenu(getAction("changeLanguage"));
		changeLanguageMenu.setIcon(Icons.CHANGE_LANGUAGE);
		
		changeLanguageMenu.add(new JMenuItem(new LanguageAction("en", Icons.EN)));
		changeLanguageMenu.add(new JMenuItem(new LanguageAction("de", Icons.DE)));
		changeLanguageMenu.add(new JMenuItem(new LanguageAction("fr", Icons.FR)));
		changeLanguageMenu.add(new JMenuItem(new LanguageAction("hr", Icons.HR)));
		
		return changeLanguageMenu;
	}

	/**
	 * Returns a new {@linkplain LocalizableAction} object, where string text is
	 * fetched with the specified <tt>key</tt> and there is no description of
	 * the action.
	 * 
	 * @param key the property key
	 * @return a LocalizableAction object with the specified key property
	 */
	private Action getAction(String key) {
		return new LocalizableAction(key, flp, false) {
			private static final long serialVersionUID = 1L;
			@Override
			public void actionPerformed(ActionEvent e) {}
		};
	}

	/**
	 * Creates a floatable <tt>JToolBar</tt> and fills it with buttons
	 * referenced to action objects.
	 * 
	 * @return a floatable toolbar filled with action buttons
	 */
	private JToolBar createToolbars() {
		JToolBar toolBar = new JToolBar(flp.getString("tools"));
		toolBar.setFloatable(true);

		toolBar.add(new JButton(newAction));
		toolBar.add(new JButton(openAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(saveAction));
		toolBar.add(new JButton(saveAsAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(closeTabAction));
		toolBar.add(new JButton(exitAction));
		toolBar.addSeparator();
		
		toolBar.addSeparator();
		toolBar.add(new JButton(cutAction));
		toolBar.add(new JButton(copyAction));
		toolBar.add(new JButton(pasteAction));
		toolBar.addSeparator();
		toolBar.add(new JButton(statisticsAction));
		
		flp.addLocalizationListener(() -> {
			toolBar.setName(flp.getString("tools"));
		});
		
		// Scroll up-down / left-right on toolbar
		toolBar.addMouseWheelListener((e) -> {
			final int SCROLL_SPEED = 10;
			
			Rectangle r = new Rectangle();
			toolBar.computeVisibleRect(r);
			
			int orientation = toolBar.getOrientation();
			if (orientation == JToolBar.HORIZONTAL) {
				r.x += SCROLL_SPEED*e.getUnitsToScroll();
			} else {
				r.y += SCROLL_SPEED*e.getUnitsToScroll();
			}
			
			toolBar.scrollRectToVisible(r);
		});
		
		return toolBar;
	}

	//
    // Main method
    //
	
	/**
	 * Program entry point.
	 * 
	 * @param args not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new JNotepadPP().setVisible(true);
		});
	}
	
	
	//
    // Utility classes
    //
	
	/**
	 * This class represents an action that changes the program language.
	 * The language is specified by the language tag and an image icon.
	 *
	 * @author Mario Bobic
	 */
	private class LanguageAction extends AbstractAction {
		/** Serialization UID. */
		private static final long serialVersionUID = 1L;
		
		/** Language tag of this action. */
		private String language;
		
		/**
		 * Constructs an instance of {@code LanguageAction} with the specified
		 * language tag and image icon.
		 * <p>
		 * The action name is set to a value with property key <tt>language</tt>.
		 * 
		 * @param language language tag
		 * @param icon language icon
		 */
		public LanguageAction(String language, ImageIcon icon) {
			this.language = language;
			putValue(Action.NAME, flp.getString(language));
			putValue(Action.SMALL_ICON, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			LocalizationProvider.getInstance().setLanguage(language);
		}
		
	}
	
	/**
	 * This class represents a status bar that holds information on the length
	 * of the current document and the caret info of the current document.
	 * <p>
	 * The caret info represents the line number and column of the caret and
	 * length of the current selection, if any.
	 * <p>
	 * The status bar also holds a {@linkplain Clock} object.
	 *
	 * @author Mario Bobic
	 */
	private class StatusBar extends JComponent {
		/** Serialization UID. */
		private static final long serialVersionUID = 1L;
		
		/** Length of the current document. */
		private JLabel length = new LJLabel("length", flp);
		
		/** Current caret line. */
		private JLabel ln = new LJLabel("ln", flp);
		/** Current caret column. */
		private JLabel col = new LJLabel("col", flp);
		/** Current caret selection length. */
		private JLabel sel = new LJLabel("sel", flp);
		
		/**
		 * Constructs an instance of a status bar by adding and initializing the
		 * length and caret info of the current document, and adding the clock.
		 */
		public StatusBar() {
			setLayout(new BorderLayout());
			setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY));
			
			/* Statistics panel. */
			JPanel left = new JPanel(new BorderLayout(10, 0));
			add(left, BorderLayout.LINE_START);
			
			JPanel caretInfo = new JPanel(new FlowLayout());
			caretInfo.add(ln);
			caretInfo.add(col);
			caretInfo.add(sel);
			
			left.add(length, BorderLayout.LINE_START); // add glue, box?
			left.add(appendSeparator(caretInfo), BorderLayout.LINE_END);
			updateStatus();
			
			
			/* Time panel. */
			JPanel right = new JPanel();
			add(right, BorderLayout.LINE_END);
			
			right.add(clock);
			
			flp.addLocalizationListener(() -> {
				updateStatus();
			});
		}
		
		/**
		 * Appends a vertical {@linkplain JSeparator} to the left side of
		 * specified component <tt>comp</tt> and returns that JPanel.
		 * 
		 * @param comp component which will be next to a JSeparator
		 * @return a JPanel containing the JSeparator and the component
		 */
		private JPanel appendSeparator(JComponent comp) {
			JPanel panel = new JPanel(new BorderLayout(3, 0));
			panel.add(new JSeparator(SwingConstants.VERTICAL), BorderLayout.WEST);
			panel.add(comp);
			return panel;
		}
		
		/**
		 * Updates the length and caret info to match the current document.
		 */
		public void updateStatus() {
			if (editor == null) {
				length.setText("");
				ln.setText("");
				col.setText("");
				sel.setText("");
				return;
			}
			
			try {
				Caret caret = editor.getCaret();
				int line = editor.getLineOfOffset(caret.getDot());
				int column = caret.getDot() - editor.getLineStartOffset(line);
				int select = Math.abs(caret.getDot() - caret.getMark());
				
				length.setText(flp.getString("length") + ": " + documentLength());
				ln.setText(flp.getString("ln") + ": " + (line+1));
				col.setText(flp.getString("col") + ": " + column);
				sel.setText(flp.getString("sel") + ": " + select);
			} catch (BadLocationException e) {
				throw new InternalError(e);
			}
		}
	}
	
	/**
	 * This class represents a clock which is started on a daemon thread upon
	 * construction. The clock pattern is actually <tt>yyyy/MM/dd HH:mm:ss</tt>.
	 *
	 * @author Mario Bobic
	 */
	private static class Clock extends JLabel {
		/** Serialization UID. */
		private static final long serialVersionUID = 1L;

		/** The date-time formatter. */
		private static final DateTimeFormatter FORMATTER =
				DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		
		/** String holding the current time. */
		private volatile String time;
		/** Flag that indicates that a stop was requested from outside. */
		private volatile boolean stopRequested;
		
		/**
		 * Constructs an instance of a clock, creating a daemon thread that will
		 * be running the clock.
		 */
		public Clock() {
			updateTime();
			
			Thread t = new Thread(()-> {
				while (true) {
					try {
						Thread.sleep(500);
					} catch (Exception e) {}
					
					if (stopRequested) break;
					
					SwingUtilities.invokeLater(() -> {
						updateTime();
					});
				}
			}, "Clock");
			
			t.setDaemon(true);
			t.start();
		}
		
		/**
		 * Requests the clock to be stopped, or more formally the daemon thread
		 * that is running the clock to stop executing.
		 */
		public void stop() {
			stopRequested = true;
		}
		
		/**
		 * Updates the time to current time with the date-time formatter.
		 */
		private void updateTime() {
			time = FORMATTER.format(LocalDateTime.now());
			setText(time);
		}
	}
	
	/**
	 * This class represents a text editor of the {@linkplain JNotepadPP}
	 * program. It extends the {@linkplain JTextArea} and additionally provides
	 * a flag that indicates if a change has been made and holds the path of the
	 * file it is currently editing.
	 *
	 * @author Mario Bobic
	 */
	public class JEditor extends JTextArea {
		/** Serialization UID. */
		private static final long serialVersionUID = 1L;
		
		/** Flag that indicates if a document change has been made. */
		private boolean changed;
		
		/** The path of the file that is currently opened. */
		private Path filePath;

		/**
		 * Constructs a new JEditor. A default model is set, the initial string
		 * is null, and rows/columns are set to 0.
		 */
		public JEditor() {
	        this(null, null, 0, 0);
		}

		/**
		 * Constructs a new JEditor with the specified text displayed.
		 * A default model is created and rows/columns are set to 0.
		 *
		 * @param text the text to be displayed, or <tt>null</tt>
		 */
		public JEditor(String text) {
	        this(null, text, 0, 0);
		}
		
		/**
		 * Constructs a new empty JEditor with the specified number of rows and
		 * columns. A default model is created, and the initial string is
		 * <tt>null</tt>.
		 *
		 * @param rows the number of rows &gt;= 0
		 * @param columns the number of columns &gt;= 0
		 * @throws IllegalArgumentException if the rows or columns are negative.
		 */
		public JEditor(int rows, int columns) {
	        this(null, null, rows, columns);
		}

		/**
		 * Constructs a new JEditor with the specified text and number of rows
		 * and columns. A default model is created.
		 *
		 * @param text the text to be displayed, or <tt>null</tt>
		 * @param rows the number of rows &gt;= 0
		 * @param columns the number of columns &gt;= 0
		 * @throws IllegalArgumentException if the rows or columns are negative.
		 */
		public JEditor(String text, int rows, int columns) {
	        this(null, text, rows, columns);
		}

		/**
		 * Constructs a new JEditor with the given document model, and
		 * defaults for all of the other arguments (null, 0, 0).
		 *
		 * @param doc the model to use
		 */
		public JEditor(Document doc) {
	        this(doc, null, 0, 0);
		}

		/**
		 * Constructs a new JEditor with the specified number of rows and
		 * columns, and the given model. All of the constructors feed through
		 * this constructor.
		 *
		 * @param doc the model to use, or create a default one if null
		 * @param text the text to be displayed, null if none
		 * @param rows the number of rows &gt;= 0
		 * @param columns the number of columns &gt;= 0
		 * @throws IllegalArgumentException if the rows or columns are negative.
		 */
		public JEditor(Document doc, String text, int rows, int columns) {
			super(doc, text, rows, columns);
			
			getDocument().addDocumentListener(getDocumentListener());
		}
		
		/**
		 * Returns a new document listener that sets the <tt>changed</tt>
		 * boolean flag to <tt>true</tt> and the tab icon to
		 * {@linkplain Icons#UNSAVED} if an update has happened
		 * 
		 * @return a document listener that updates tab changes
		 */
		private DocumentListener getDocumentListener() {
			return new DocumentListener() {
				
				@Override
				public void removeUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
				
				@Override
				public void insertUpdate(DocumentEvent e) {
					changedUpdate(e);
				}
				
				@Override
				public void changedUpdate(DocumentEvent e) {
					changed = true;
					tabs.setIconAt(tabs.getSelectedIndex(), Icons.UNSAVED);
				}
			};
		}
		
		@Override
		public String getName() {
			if (filePath == null) {
				return flp.getString("untitled");
			} else {
				return filePath.getFileName().toString();
			}
		}
		
		/**
		 * Returns true if the editor document has had a change since it was
		 * last saved. False otherwise
		 * 
		 * @return the changed state of the editor document
		 */
		public boolean isChanged() {
			return changed;
		}
		
		/**
		 * Sets the changed state of the editor document to the specified value
		 * and also updates the tab icon.
		 * 
		 * @param changed the changed state to be set
		 */
		public void setChanged(boolean changed) {
			this.changed = changed;
			tabs.setIconAt(tabs.getSelectedIndex(), changed ? Icons.UNSAVED : Icons.SAVED);
		}

		/**
		 * Returns the path of the file that is currently opened.
		 * 
		 * @return the path of the file that is currently opened
		 */
		public Path getFilePath() {
			return filePath;
		}

		/**
		 * Sets the path of the file that is currently opened to the specified
		 * <tt>filePath</tt>.
		 * 
		 * @param filePath path of the file to be set
		 */
		public void setFilePath(Path filePath) {
			this.filePath = filePath;
		}
	}

}
