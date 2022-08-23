package text;

import com.inet.jortho.FileUserDictionary;
import com.inet.jortho.SpellChecker;
import com.inet.jortho.SpellCheckerOptions;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JColorChooser;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import util.ImageSelection;
import util.PatientStringSelection;

/**
 * An extension of {@code HTMLEditorKit} that provides a collection of user-friendly text editing
 * features.
 */
public class OmniTextEditorKit extends HTMLEditorKit {

  /**
   * Creates a window to edit a new document.
   */
  public static class CreateAction extends AbstractAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public CreateAction() {
      super(CREATE_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      OmniText.main(new String[0]);
    }
  }

  /**
   * Opens the selected file in the current window.
   */
  public static class OpenAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public OpenAction() {
      super(OPEN_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      File oldFile = fileChooser.getSelectedFile();
      int selection = fileChooser.showOpenDialog(new JFrame());
      if (selection == JFileChooser.APPROVE_OPTION) {
        File newFile = fileChooser.getSelectedFile();
        if (SaveOpenUtil.correctExtension(newFile)) {
          Path path = Path.of(newFile.toString());
          try (BufferedReader reader = Files.newBufferedReader(path)) {
            component.read(reader, component.getDocument());
          } catch (IOException ex) {
            JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Open Failed",
                JOptionPane.ERROR_MESSAGE);
          }
        } else {
          fileChooser.setSelectedFile(oldFile);
          JOptionPane.showMessageDialog(new JFrame(), "Must select file of type html",
              "Open Failed", JOptionPane.ERROR_MESSAGE);
        }

      }
    }
  }


  /**
   * Saves content from the associated text component as the last selected file. If it doesn't
   * exist, delegates to a {@code SaveAsAction} object.
   */
  public static class SaveAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public SaveAction() {
      super(SAVE_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      File file = fileChooser.getSelectedFile();
      if (file == null) {
        new SaveAsAction().actionPerformed(e);
      } else {
        JTextComponent editor = getEditor(e);
        SaveOpenUtil.safeHTMLWrite(editor, file);
      }
    }
  }

  /**
   * Saves content from the associated text component as the selected file.
   */
  public static class SaveAsAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public SaveAsAction() {
      super(SAVE_AS_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent editor = getEditor(e);
      int selection = fileChooser.showSaveDialog(new JFrame());
      if (selection == JFileChooser.APPROVE_OPTION) {
        SaveOpenUtil.safeHTMLWrite(editor, fileChooser.getSelectedFile());
      }
    }
  }

  /**
   * Helps with checking HTML file extensions and writing to files with .html extension
   */
  public static class SaveOpenUtil {

    /**
     * Checks if the given file has an html extension.
     *
     * @param f The file to check
     * @return true if file extension is .html, false otherwise
     */
    public static boolean correctExtension(File f) {
      String ext = "";
      String s = f.getName();
      int i = s.lastIndexOf('.');

      if (i > 0 && i < s.length() - 1) {
        ext = s.substring(i + 1).toLowerCase();
      }

      return ext.equals("html");
    }

    /**
     * Writes bytes from given editor to the given file with an html extension.
     *
     * @param editor editor to get data from
     * @param f      file to write to
     */
    public static void safeHTMLWrite(JTextComponent editor, File f) {
      try {
        if (correctExtension(f)) {
          Files.write(Path.of(f.toString()),
              editor.getText().getBytes());
        } else {
          Files.write(Path.of(f.toString() + ".html"),
              editor.getText().getBytes());
        }
      } catch (IOException ex) {
        JOptionPane.showMessageDialog(new JFrame(), ex.getMessage(), "Save Failed",
            JOptionPane.ERROR_MESSAGE);
      }
    }
  }

  /**
   * Pastes the contents of the system clipboard into the selected region, removing its original
   * formatting.
   */
  public static class PasteWithoutFormattingAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public PasteWithoutFormattingAction() {
      super(PASTE_WITHOUT_FORMATTING_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      Transferable contents = clipboard.getContents(Optional.empty());
      try {
        if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
          setClipboard((String) clipboard.getData(DataFlavor.stringFlavor));
        } else if (contents.isDataFlavorSupported(DataFlavor.imageFlavor)) {
          setClipboard((BufferedImage) clipboard.getData(DataFlavor.imageFlavor));
        } else if (contents.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
          @SuppressWarnings("unchecked")
          List<File> files = (List<File>) clipboard.getData(DataFlavor.javaFileListFlavor);
          setClipboard(files);
        }
      } catch (UnsupportedFlavorException | IOException ex) {
        ex.printStackTrace();
      }
      getTextComponent(e).paste();
    }

    /**
     * Sets the current contents of the clipboard to the specified string and registers the
     * specified clipboard as the owner of the new contents.
     *
     * @param s the string to be set
     */
    private static void setClipboard(String s) {
      PatientStringSelection contents = new PatientStringSelection(s);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);
    }

    /**
     * Sets the current contents of the clipboard to the specified image and registers the specified
     * clipboard as the owner of the new contents.
     *
     * @param i the image to be set
     */
    private static void setClipboard(BufferedImage i) {
      ImageSelection contents = new ImageSelection(i);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(contents, contents);
    }

    /**
     * Sets the current contents of the clipboard to the specified list of files and registers the
     * specified clipboard as the owner of the new contents.
     *
     * @param files the files to be set
     */
    private static void setClipboard(List<File> files) {
      // TODO
    }
  }

  /**
   * Sets the text highlight color based on a {@code JColorChooser} dialog selection.
   */
  public static class TextHighlightColorAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public TextHighlightColorAction() {
      super(TEXT_HIGHLIGHT_COLOR_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // Get the editor and document
      JEditorPane editor = getEditor(e);
      HTMLDocument document = getHTMLDocument(editor);

      // Prompt user to select color
      Optional<Color> selectedColor = Optional.ofNullable(JColorChooser
          .showDialog(editor, "Color Chooser", editor.getBackground()));
      // If color is selected, continue and set c to color
      selectedColor.ifPresent(c -> {
        // Get offset and length of selected region
        int offset = editor.getSelectionStart();
        int length = editor.getSelectionEnd() - editor.getSelectionStart();

        // Convert chosen color to a hexadecimal representation
        String hex = String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());

        // Insert temporary element with highlight in order to copy the span attribute
        JEditorPane tempEditor = new JEditorPane();
        tempEditor.setDocument(new HTMLDocument());
        tempEditor.setEditorKit(new OmniTextEditorKit());
        HTMLDocument tempDoc = getHTMLDocument(tempEditor);

        try {
          tempDoc.insertBeforeStart(tempDoc.getCharacterElement(0),
              "<span style=\"background-color:" + hex + "\">a</span>");
        } catch (BadLocationException | IOException ex) {
          ex.printStackTrace();
        }

        MutableAttributeSet attributes = new SimpleAttributeSet();
        attributes.addAttributes(tempDoc.getCharacterElement(0)
            .getAttributes().copyAttributes());

        // Set the selected region to have the new attributes (old attributes + highlight)
        document.setCharacterAttributes(offset, length, attributes, false);
      });
    }
  }

  /**
   * Sets the font color based on a {@code JColorChooser} dialog selection.
   */
  public static class FontColorAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public FontColorAction() {
      super(FONT_COLOR_ACTION);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      new ForegroundAction("Color",
          JColorChooser.showDialog(getEditor(e), "Color Chooser", Color.BLACK))
          .actionPerformed(e);
    }
  }

  /**
   * Checks the spelling of a text component.
   */
  public static class SpellCheckAction extends HTMLTextAction {

    /**
     * Creates this object with the appropriate identifier.
     */
    public SpellCheckAction() {
      super(SPELL_CHECK_ACTION);
      try {
        registerSpellChecker();
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
    }

    /**
     * Registers the dictionary to be used by the spell checker.
     *
     * @throws MalformedURLException if no protocol is specified, or an unknown protocol is found,
     *                               or spec is null, or the parsed URL fails to comply with the
     *                               specific syntax of the associated protocol.
     */
    private void registerSpellChecker() throws MalformedURLException {
      SpellChecker.setUserDictionaryProvider(new FileUserDictionary());
      SpellChecker.registerDictionaries(new URL("file:resources/dictionaries.cnf"), "en");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      JTextComponent component = getTextComponent(e);
      SpellChecker.register(component);
      SpellCheckerOptions options = new SpellCheckerOptions();
      options.setCaseSensitive(true);
      options.setSuggestionsLimitMenu(10);
      component.setComponentPopupMenu(SpellChecker.createCheckerPopup(options));
    }
  }

  /**
   * The bold action identifier used by this editor kit. Refers to the default used by {@code
   * BoldAction}.
   */
  public static final String BOLD_ACTION = new BoldAction().getValue(Action.NAME).toString();

  /**
   * The italic action identifier used by this editor kit. Refers to the default used by {@code
   * ItalicAction}.
   */
  public static final String ITALIC_ACTION = new ItalicAction().getValue(Action.NAME).toString();

  /**
   * The underline action identifier. Refers to the default used by {@code UnderlineAction}.
   */
  public static final String UNDERLINE_ACTION = new UnderlineAction().getValue(Action.NAME)
      .toString();

  /**
   * The create action identifier.
   */
  public static final String CREATE_ACTION = "create-action";

  /**
   * The open action identifier.
   */
  public static final String OPEN_ACTION = "open-action";

  /**
   * The save action identifier.
   */
  public static final String SAVE_ACTION = "save-action";

  /**
   * The save as action identifier.
   */
  public static final String SAVE_AS_ACTION = "save-as-action";

  /**
   * The paste without formatting action identifier.
   */
  public static final String PASTE_WITHOUT_FORMATTING_ACTION = "paste-without-formatting-action";

  /**
   * The text highlight color action identifier.
   */
  public static final String TEXT_HIGHLIGHT_COLOR_ACTION = "text-highlight-color-action";

  /**
   * The font color action identifier.
   */
  public static final String FONT_COLOR_ACTION = "font-color-action";

  /**
   * The left align action identifier.
   */
  public static final String LEFT_ALIGN_ACTION = "Left Align";

  /**
   * The center align action identifier.
   */
  public static final String CENTER_ALIGN_ACTION = "Center Align";

  /**
   * The right align action identifier.
   */
  public static final String RIGHT_ALIGN_ACTION = "Right Align";

  /**
   * The spell check action identifier.
   */
  public static final String SPELL_CHECK_ACTION = "Spell Check";

  private static final Action[] defaultActions = {
      new CreateAction(),
      new OpenAction(),
      new SaveAction(),
      new SaveAsAction(),
      new PasteWithoutFormattingAction(),
      new TextHighlightColorAction(),
      new FontColorAction(),
      new AlignmentAction(LEFT_ALIGN_ACTION, StyleConstants.ALIGN_LEFT),
      new AlignmentAction(CENTER_ALIGN_ACTION, StyleConstants.ALIGN_CENTER),
      new AlignmentAction(RIGHT_ALIGN_ACTION, StyleConstants.ALIGN_RIGHT),
      new SpellCheckAction()
  };

  private static final JFileChooser fileChooser = createJFileChooser();

  /**
   * Creates an {@code OmniTextEditorKit} with a custom stylesheet.
   */
  public OmniTextEditorKit() {
    super();
  }

  /**
   * Creates the file chooser for this editor kit.
   *
   * @return the file chooser
   */
  private static JFileChooser createJFileChooser() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setFileFilter(new FileNameExtensionFilter("HTML Documents (*.html)", "html"));
    return fileChooser;
  }

  @Override
  public Action[] getActions() {
    List<Action> actions = new ArrayList<>();
    Stream.of(super.getActions(), defaultActions).flatMap(Stream::of).forEach(actions::add);
    return actions.toArray(new Action[0]);
  }
}
