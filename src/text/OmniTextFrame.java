package text;

import com.formdev.flatlaf.util.ScaledImageIcon;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.KeyStroke;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.StyledEditorKit.FontSizeAction;
import util.ReifiedUndoManager;

/**
 * A top-level window for an {@code OmniText} application.
 */
public class OmniTextFrame extends JFrame implements Runnable {

  private final JTextPane pane;

  /**
   * Constructs a new frame that is initially invisible.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   */
  public OmniTextFrame() throws HeadlessException {
    super("OmniText Editor");
    try {
      setIconImage(ImageIO.read(new File("resources/file-word.png")));
    } catch (IOException e) {
      e.printStackTrace();
    }
    pane = new OmniTextPane();
    pane.addMouseListener(new RightClickListener());
    add(new JScrollPane(pane));
    setJMenuBar(createMenuBar());
    pack();
  }

  /**
   * Creates the menu bar to be used for this text frame.
   *
   * @return the menu bar
   */
  private JMenuBar createMenuBar() {
    JMenuBar menuBar = new JMenuBar();
    menuBar.setLayout(new FlowLayout(FlowLayout.LEFT));
    menuBar.add(createFileMenu());
    menuBar.add(createEditMenu());
    menuBar.add(createFontMenu());
    menuBar.add(createFontSizeMenu());
    createTextHighlightColorButton().ifPresent(menuBar::add);
    createFontColorButton().ifPresent(menuBar::add);
    createBoldButton().ifPresent(menuBar::add);
    createItalicizeButton().ifPresent(menuBar::add);
    createUnderlineButton().ifPresent(menuBar::add);
    createLeftAlignButton().ifPresent(menuBar::add);
    createCenterAlignButton().ifPresent(menuBar::add);
    createRightAlignButton().ifPresent(menuBar::add);
    createSpellCheckButton().ifPresent(menuBar::add);
    return menuBar;
  }

  /**
   * Creates the file menu to be used for this text frame.
   *
   * @return the file menu
   */
  private JComponent createFileMenu() {
    JMenu menu = new JMenu("File");
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.CREATE_ACTION, "New")
        .setIcon("resources/file.png")
        .build());
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.OPEN_ACTION, "Open")
        .setIcon("resources/folder-open.png")
        .build());
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.SAVE_ACTION, "Save")
        .setIcon("resources/save.png")
        .build());
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.SAVE_AS_ACTION, "Save As")
        .setIcon("resources/save.png")
        .build());
    return menu;
  }

  /**
   * Creates the edit menu to be used for this text frame.
   *
   * @return the edit menu
   */
  private JComponent createEditMenu() {
    JMenu menu = new JMenu("Edit");
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.cutAction, "Cut")
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK))
        .setIcon("resources/cut.png")
        .build());
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.copyAction, "Copy")
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK))
        .setIcon("resources/copy.png")
        .build());
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.PASTE_WITHOUT_FORMATTING_ACTION, "Paste")
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK))
        .setIcon("resources/paste.png")
        .build());
    menu.addSeparator();
    menu.add(makeJMenuItemBuilder(ReifiedUndoManager.UNDO_ACTION, ReifiedUndoManager.UNDO_ACTION)
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK))
        .setIcon("resources/undo.png")
        .build());
    menu.add(makeJMenuItemBuilder(ReifiedUndoManager.REDO_ACTION, ReifiedUndoManager.REDO_ACTION)
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK))
        .setIcon("resources/redo.png")
        .build());
    menu.addSeparator();
    menu.add(makeJMenuItemBuilder(OmniTextEditorKit.selectAllAction, "Select All")
        .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK))
        .build());
    return menu;
  }

  /**
   * Creates the font menu to be used for this text frame.
   *
   * @return the font menu
   */
  private JComponent createFontMenu() {
    JComboBox<String> fontMenu = new JComboBox<>(
        GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
    fontMenu.addActionListener(
        e -> new StyledEditorKit.FontFamilyAction(
            Optional.ofNullable(fontMenu.getSelectedItem()).orElse(fontMenu.getItemAt(0))
                .toString(), fontMenu.getSelectedItem().toString()).actionPerformed(e));
    fontMenu.setRequestFocusEnabled(false);
    fontMenu.setToolTipText("Font");
    return fontMenu;
  }

  /**
   * Creates the font size menu to be used for this text frame.
   *
   * @return the font size menu
   */
  private JComponent createFontSizeMenu() {
    Integer[] defaultSizes = {8, 10, 12, 14, 16, 24, 36, 48};
    JComboBox<Integer> fontSizeMenu = new JComboBox<>(defaultSizes);
    fontSizeMenu.addActionListener(e -> {
      int size = (int) Optional.ofNullable(fontSizeMenu.getSelectedItem())
          .orElse(fontSizeMenu.getItemAt(0));
      new FontSizeAction("font-size-" + size, size).actionPerformed(e);
    });
    fontSizeMenu.setRequestFocusEnabled(false);
    fontSizeMenu.setToolTipText("Font Size");
    return fontSizeMenu;
  }

  /**
   * Creates the text highlight color button to be used for this text frame.
   *
   * @return the text highlight color button
   */
  private Optional<JButton> createTextHighlightColorButton() {
    return createButton(OmniTextEditorKit.TEXT_HIGHLIGHT_COLOR_ACTION, "resources/highlighter.png",
        "Text Highlight Color");
  }

  /**
   * Creates the font color button to be used for this text frame.
   *
   * @return the font color button
   */
  private Optional<JButton> createFontColorButton() {
    return createButton(OmniTextEditorKit.FONT_COLOR_ACTION, "resources/font.png", "Font Color");
  }

  /**
   * Creates the bold button to be used for this text frame.
   *
   * @return the bold button
   */
  private Optional<JToggleButton> createBoldButton() {
    return createToggleButton(OmniTextEditorKit.BOLD_ACTION,"resources/bold.png", "Bold");
  }

  /**
   * Creates the italicize button to be used for this text frame.
   *
   * @return the italicize button
   */
  private Optional<JToggleButton> createItalicizeButton() {
    return createToggleButton(OmniTextEditorKit.ITALIC_ACTION, "resources/italic.png",
        "Italic");
  }

  /**
   * Creates the underline button to be used for this text frame.
   *
   * @return the underline button
   */
  private Optional<JToggleButton> createUnderlineButton() {
    return createToggleButton(OmniTextEditorKit.UNDERLINE_ACTION, "resources/underline.png",
        "Underline");
  }

  /**
   * Creates the left align button to be used for this text frame.
   *
   * @return the left align button
   */
  private Optional<JToggleButton> createLeftAlignButton() {
    return createToggleButton(OmniTextEditorKit.LEFT_ALIGN_ACTION, "resources/align-left.png",
        "Align Left");
  }

  /**
   * Creates the center align button to be used for this text frame.
   *
   * @return the center align button
   */
  private Optional<JToggleButton> createCenterAlignButton() {
    return createToggleButton(OmniTextEditorKit.CENTER_ALIGN_ACTION, "resources/align-center.png",
        "Align Center");
  }

  /**
   * Creates the right align button to be used for this text frame.
   *
   * @return the right align button
   */
  private Optional<JToggleButton> createRightAlignButton() {
    return createToggleButton(OmniTextEditorKit.RIGHT_ALIGN_ACTION, "resources/align-right.png",
        "Align Right");
  }

  /**
   * Creates the spell check button to be used for this text frame.
   */
  private Optional<JButton> createSpellCheckButton() {
    return createButton(OmniTextEditorKit.SPELL_CHECK_ACTION, "resources/spell-check.png",
        "Spell Check");
  }

  /**
   * Returns this object's text pane action associated with the given key.
   *
   * @param key the key bound to the action
   * @return the action
   */
  private Optional<Action> getAction(Object key) {
    return Optional.ofNullable(pane.getActionMap().get(key));
  }

  /**
   * Creates a button with the specified action and icon.
   *
   * @param actionKey    the key bound to the action
   * @param iconFileName the icon file
   * @return the button
   */
  private Optional<JButton> createButton(String actionKey, String iconFileName, String tooltip) {
    Optional<Action> action = getAction(actionKey);
    if (action.isPresent()) {
      JButton button = new JButton(getScaledIcon(iconFileName));
      button.addActionListener(e -> action.get().actionPerformed(e));
      button.setToolTipText(tooltip);
      button.setRequestFocusEnabled(false);
      return Optional.of(button);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Creates a toggle button with the specified action and icon.
   *
   * @param actionKey    the key bound to the action
   * @param iconFileName the icon file
   * @return the button
   */
  private Optional<JToggleButton> createToggleButton(String actionKey, String iconFileName,
      String tooltip) {
    Optional<Action> action = getAction(actionKey);
    if (action.isPresent()) {
      JToggleButton button = new JToggleButton(getScaledIcon(iconFileName));
      button.addActionListener(e -> action.get().actionPerformed(e));
      button.setRequestFocusEnabled(false);
      button.setToolTipText(tooltip);
      return Optional.of(button);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Creates the icon obtained from the given file and scales it to a standard size.
   *
   * @param filename the icon file
   * @return the icon
   */
  private Icon getScaledIcon(String filename) {
    Image image = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_GRAY);
    try {
      image = ImageIO.read(new File(filename))
          .getScaledInstance(15, 15, Image.SCALE_SMOOTH);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return new ScaledImageIcon(new ImageIcon(image));
  }

  /**
   * Constructs an instance of a {@code JMenuItemBuilder} for creating a custom {@code JMenuItem}.
   *
   * @param actionKey the key bound to the action
   * @param alias     the string used to set the item's alias
   * @return the {@code JMenuItemBuilder}
   */
  private JMenuItemBuilder makeJMenuItemBuilder(Object actionKey, String alias) {
    return new JMenuItemBuilder(actionKey, alias);
  }

  /**
   * A builder class for a customizable {@code JMenuItem}.
   */
  private class JMenuItemBuilder {

    private JMenuItem item;

    /**
     * Constructs a new  {@code JMenuItemBuilder}. If the specified action is not supported by the
     * {@code OmniTextEditorKit}, it will not be added.
     *
     * @param actionKey the key bound to the action
     * @param alias     the string used to set the item's alias
     */
    private JMenuItemBuilder(Object actionKey, String alias) {
      getAction(actionKey).ifPresentOrElse(a -> {
        item = new JMenuItem(a);
        item.setText(alias);
      }, () -> item = new JMenuItem(alias));
    }

    /**
     * Sets the accelerator key of the {@code JMenuItem}.
     *
     * @param accelerator the {@code KeyStroke} which will serve as an accelerator
     * @return this builder object
     */
    private JMenuItemBuilder setAccelerator(KeyStroke accelerator) {
      item.setAccelerator(accelerator);
      return this;
    }

    /**
     * Sets the icon of the {@code JMenuItem}.
     *
     * @param filename the icon file
     * @return this builder object
     */
    private JMenuItemBuilder setIcon(String filename) {
      item.setIcon(getScaledIcon(filename));
      item.setDisabledIcon(getScaledIcon(filename)); // not grayed out
      return this;
    }

    /**
     * Builds the {@code JMenuItem}.
     *
     * @return the {@code JMenuItem}
     */
    private JMenuItem build() {
      return item;
    }
  }

  /**
   * For implementing the right click menu.
   */
  private class RightClickMenu extends JPopupMenu {

    /**
     * Creates a popup menu with cut, copy, paste, undo, redo, and select all actions.
     */
    public RightClickMenu() {
      add(makeJMenuItemBuilder(OmniTextEditorKit.cutAction, "Cut")
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK))
          .setIcon("resources/cut.png")
          .build());
      add(makeJMenuItemBuilder(OmniTextEditorKit.copyAction, "Copy")
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK))
          .setIcon("resources/copy.png")
          .build());
      add(makeJMenuItemBuilder(OmniTextEditorKit.PASTE_WITHOUT_FORMATTING_ACTION, "Paste")
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK))
          .setIcon("resources/paste.png")
          .build());
      addSeparator();
      add(makeJMenuItemBuilder(ReifiedUndoManager.UNDO_ACTION, ReifiedUndoManager.UNDO_ACTION)
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_DOWN_MASK))
          .setIcon("resources/undo.png")
          .build());
      add(makeJMenuItemBuilder(ReifiedUndoManager.REDO_ACTION, ReifiedUndoManager.REDO_ACTION)
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, KeyEvent.CTRL_DOWN_MASK))
          .setIcon("resources/redo.png")
          .build());
      addSeparator();
      add(makeJMenuItemBuilder(OmniTextEditorKit.selectAllAction, "Select All")
          .setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK))
          .build());
    }
  }

  /**
   * Opens the right click menu when the right mouse button is used.
   */
  private class RightClickListener extends MouseAdapter {

    /**
     * Opens the menu when right click is pressed.
     *
     * @param e mouse event
     */
    public void mousePressed(MouseEvent e) {
      if (e.isPopupTrigger()) {
        doPop(e);
      }
    }

    /**
     * Opens the menu when right click is released.
     *
     * @param e mouse event
     */
    public void mouseReleased(MouseEvent e) {
      if (e.isPopupTrigger()) {
        doPop(e);
      }
    }

    /**
     * Creates and shows the right click menu at the mouse location.
     *
     * @param e mouse event
     */
    private void doPop(MouseEvent e) {
      RightClickMenu menu = new RightClickMenu();
      menu.show(e.getComponent(), e.getX(), e.getY());
    }
  }

  @Override
  public void run() {
    pack();
    pane.requestFocus();
    setVisible(true);
  }
}
