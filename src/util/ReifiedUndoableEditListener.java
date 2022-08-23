package util;

import javax.swing.Action;
import javax.swing.event.UndoableEditListener;

/**
 * An extension of {@code UndoableEditListener} that reifies undo and redo edits as {@code Action}
 * instances.
 */
public interface ReifiedUndoableEditListener extends UndoableEditListener {

  /**
   * Returns the {@code Action} associated with undoing edits.
   */
  Action getUndoAction();

  /**
   * Returns the {@code Action} associated with redoing edits.
   */
  Action getRedoAction();
}
