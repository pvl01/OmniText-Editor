package text;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.Action;
import javax.swing.JTextPane;

import javax.swing.KeyStroke;
import util.ReifiedUndoManager;
import util.ReifiedUndoableEditListener;

/**
 * An extended version of {@code JTextPane} that contains the features specified by {@code
 * OmniTextEditorKit} and {@code ReifiedUndoManager}.
 */
public class OmniTextPane extends JTextPane {

  /**
   * Creates a new {@code OmniTextPane} with a default margin and size.
   */
  public OmniTextPane() {
    super();
    setMargin(new Insets(5, 5, 5, 5));
    setPreferredSize(new Dimension(750, 750));
    setEditorKit(new OmniTextEditorKit());
    overrideSystemPasteBinding();
    addReifiedUndoableEditListener();
  }

  /**
   * Overrides the CTRL+V binding to use the {@code PasteWithoutFormattingAction} specified in
   * {@code OmniTextEditorKit}.
   */
  private void overrideSystemPasteBinding() {
    getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK),
        getActionMap().get(OmniTextEditorKit.PASTE_WITHOUT_FORMATTING_ACTION));
  }

  /**
   * Links a {@code ReifiedUndoableEditListener} object to this text pane's document.
   */
  private void addReifiedUndoableEditListener() {
    ReifiedUndoableEditListener undoListener = new ReifiedUndoManager();
    getDocument().addUndoableEditListener(undoListener);
    getActionMap().put(ReifiedUndoManager.UNDO_ACTION, undoListener.getUndoAction());
    getActionMap().put(ReifiedUndoManager.REDO_ACTION, undoListener.getRedoAction());
  }

  @Override
  public Action[] getActions() {
    List<Action> actions = new ArrayList<>();
    Stream.of(getActionMap().allKeys()).forEach(k -> actions.add(getActionMap().get(k)));
    return actions.toArray(new Action[0]);
  }
}
