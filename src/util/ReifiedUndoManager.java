package util;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

/**
 * An implementation of a {@code ReifiedUndoableEditListener} that wraps an instance of an {@code
 * UndoManager} and reifies its undo and redo edits.
 */
public class ReifiedUndoManager implements ReifiedUndoableEditListener {

  /**
   * The undo action identifier.
   */
  public static final String UNDO_ACTION = "Undo";

  /**
   * The redo action identifier.
   */
  public static final String REDO_ACTION = "Redo";

  private final UndoableEdit undoManager;
  private final UndoAction undoAction;
  private final RedoAction redoAction;

  /**
   * Creates a new {@code UndoManagerWrapper}.
   */
  public ReifiedUndoManager() {
    undoManager = new UndoManager();
    undoAction = new UndoAction();
    redoAction = new RedoAction();
  }

  /**
   * An action to undo an edit in the associated {@code JEditorPane}.
   */
  public class UndoAction extends AbstractAction {

    /**
     * Creates a new {@code UndoAction} that is initially disabled.
     */
    public UndoAction() {
      super(UNDO_ACTION);
      setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (undoManager.canUndo()) {
        undoManager.undo();
        updateUndoState();
        redoAction.updateRedoState();
      }
    }

    /**
     * Updates the state of this undo action.
     */
    private void updateUndoState() {
      if (undoManager.canUndo()) {
        setEnabled(true);
        putValue(Action.NAME, undoManager.getUndoPresentationName());
      } else {
        setEnabled(false);
        putValue(Action.NAME, UNDO_ACTION);
      }
    }
  }

  /**
   * An action to redo an edit in the associated {@code JEditorPane}.
   */
  public class RedoAction extends AbstractAction {

    /**
     * Creates a new {@code RedoAction} that is initially disabled.
     */
    public RedoAction() {
      super(REDO_ACTION);
      setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (undoManager.canRedo()) {
        undoManager.redo();
        updateRedoState();
        undoAction.updateUndoState();
      }
    }

    /**
     * Updates the state of this redo action.
     */
    private void updateRedoState() {
      if (undoManager.canRedo()) {
        setEnabled(true);
        putValue(Action.NAME, undoManager.getRedoPresentationName());
      } else {
        setEnabled(false);
        putValue(Action.NAME, REDO_ACTION);
      }
    }
  }

  @Override
  public void undoableEditHappened(UndoableEditEvent e) {
    undoManager.addEdit(e.getEdit());
    undoAction.updateUndoState();
    redoAction.updateRedoState();
  }

  @Override
  public Action getUndoAction() {
    return undoAction;
  }

  @Override
  public Action getRedoAction() {
    return redoAction;
  }
}
