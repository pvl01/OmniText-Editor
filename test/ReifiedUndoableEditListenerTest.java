import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import org.junit.Before;
import org.junit.Test;
import util.ReifiedUndoManager;
import util.ReifiedUndoableEditListener;

/**
 * Contains tests for {@code ReifiedUndoableEditListener}.
 */
public class ReifiedUndoableEditListenerTest {

  private static final String before = "before text";
  private ReifiedUndoableEditListener undoListener;
  private Document testDoc;

  /**
   * Re-initializes the test variables.
   */
  @Before
  public void setUp() {
    undoListener = new ReifiedUndoManager();
    testDoc = new DefaultStyledDocument();
    insertTextAtStart(before);
    testDoc.addUndoableEditListener(undoListener);
  }

  /**
   * Inserts text into the start of {@code testDoc}, overwriting its contents.
   *
   * @param text the text to insert
   */
  private void insertTextAtStart(String text) {
    try {
      testDoc.insertString(0, text, new SimpleAttributeSet());
    } catch (BadLocationException e) {
      fail(e.getMessage());
    }
  }

  /**
   * Gets the text at the start of {@code testDoc}.
   *
   * @param length the number of characters to retrieve >= 0
   * @return the text
   */
  private String getTextAtStart(int length) {
    try {
      return testDoc.getText(0, length);
    } catch (BadLocationException e) {
      fail(e.getMessage());
      return e.getMessage();
    }
  }

  /**
   * Ensures that text edits can be undone and redone.
   */
  @Test
  public void testUndoRedoFunctionality() {
    assertEquals(before, getTextAtStart(before.length()));
    String after = "after text";
    insertTextAtStart(after);
    assertEquals(after, getTextAtStart(after.length()));
    undoListener.getUndoAction().actionPerformed(null);
    assertEquals(before, getTextAtStart(before.length()));
    undoListener.getRedoAction().actionPerformed(null);
    assertEquals(after, getTextAtStart(after.length()));
  }

  /**
   * Ensures that the undo and redo actions are enabled at the correct times.
   */
  @Test
  public void testUndoRedoEnabled() {
    assertFalse(undoListener.getUndoAction().isEnabled());
    assertFalse(undoListener.getRedoAction().isEnabled());
    insertTextAtStart(before);
    assertTrue(undoListener.getUndoAction().isEnabled());
    assertFalse(undoListener.getRedoAction().isEnabled());
    undoListener.getUndoAction().actionPerformed(null);
    assertFalse(undoListener.getUndoAction().isEnabled());
    assertTrue(undoListener.getRedoAction().isEnabled());
    insertTextAtStart(before);
    insertTextAtStart(before);
    undoListener.getUndoAction().actionPerformed(null);
    assertTrue(undoListener.getUndoAction().isEnabled());
    assertTrue(undoListener.getRedoAction().isEnabled());
  }
}