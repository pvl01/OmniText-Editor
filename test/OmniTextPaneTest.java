import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.Action;
import javax.swing.text.JTextComponent;
import org.junit.Before;
import org.junit.Test;
import text.OmniTextEditorKit;
import text.OmniTextPane;
import util.ReifiedUndoManager;

/**
 * Contains tests for {@code OmniTextPane}.
 */
public class OmniTextPaneTest {

  private JTextComponent pane;

  /**
   * Re-initializes the test variables.
   */
  @Before
  public void setUp() {
    pane = new OmniTextPane();
  }

  /**
   * Ensures that the text pane recognizes the actions specified by {@code OmniTextEditorKit} and
   * {@code ReifiedUndoManager}.
   */
  @Test
  public void testGetActions() {
    List<Class<? extends Action>> expectedClasses = new ArrayList<>();
    Stream.of(new OmniTextEditorKit().getActions()).forEach(a -> expectedClasses.add(a.getClass()));
    expectedClasses.add(ReifiedUndoManager.UndoAction.class);
    expectedClasses.add(ReifiedUndoManager.RedoAction.class);
    List<Class<? extends Action>> actualClasses = new ArrayList<>();
    Stream.of(pane.getActions()).forEach(a -> actualClasses.add(a.getClass()));
    assertTrue(actualClasses.containsAll(expectedClasses));
  }

  /**
   * Ensures that the text pane's {@code ActionMap} contains the actions specified by {@code
   * OmniTextEditorKit} and {@code ReifiedUndoManager}.
   */
  @Test
  public void testGetActionMap() {
    List<Class<? extends Action>> expectedClasses = new ArrayList<>();
    Stream.of(new OmniTextEditorKit().getActions()).forEach(a -> expectedClasses.add(a.getClass()));
    expectedClasses.add(ReifiedUndoManager.UndoAction.class);
    expectedClasses.add(ReifiedUndoManager.RedoAction.class);
    List<Class<? extends Action>> actualClasses = new ArrayList<>();
    Stream.of(pane.getActionMap().allKeys())
        .forEach(k -> actualClasses.add(pane.getActionMap().get(k).getClass()));
    assertTrue(actualClasses.containsAll(expectedClasses));
  }
}
