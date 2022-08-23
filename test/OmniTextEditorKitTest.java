import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import javax.swing.Action;
import javax.swing.text.EditorKit;
import javax.swing.text.StyledEditorKit.AlignmentAction;
import javax.swing.text.html.HTMLEditorKit;
import org.junit.Before;
import org.junit.Test;
import text.OmniTextEditorKit;
import text.OmniTextEditorKit.CreateAction;
import text.OmniTextEditorKit.FontColorAction;
import text.OmniTextEditorKit.OpenAction;
import text.OmniTextEditorKit.PasteWithoutFormattingAction;
import text.OmniTextEditorKit.SaveAction;
import text.OmniTextEditorKit.SaveAsAction;
import text.OmniTextEditorKit.SpellCheckAction;
import text.OmniTextEditorKit.TextHighlightColorAction;

/**
 * Contains tests for {@code OmniTextEditorKit}.
 */
public class OmniTextEditorKitTest {

  private EditorKit editorKit;

  /**
   * Re-initializes the test variables.
   */
  @Before
  public void setUp() {
    editorKit = new OmniTextEditorKit();
  }

  /**
   * Ensures that the returned array of actions contains all of the built-in actions.
   */
  @Test
  public void testGetActions() {
    List<Class<? extends Action>> expectedClasses = new ArrayList<>();
    Stream.of(new HTMLEditorKit().getActions()).forEach(a -> expectedClasses.add(a.getClass()));
    expectedClasses.add(CreateAction.class);
    expectedClasses.add(OpenAction.class);
    expectedClasses.add(SaveAction.class);
    expectedClasses.add(SaveAsAction.class);
    expectedClasses.add(PasteWithoutFormattingAction.class);
    expectedClasses.add(TextHighlightColorAction.class);
    expectedClasses.add(FontColorAction.class);
    expectedClasses.add(AlignmentAction.class);
    expectedClasses.add(AlignmentAction.class);
    expectedClasses.add(AlignmentAction.class);
    expectedClasses.add(SpellCheckAction.class);
    List<Class<? extends Action>> actualClasses = new ArrayList<>();
    Stream.of(editorKit.getActions()).forEach(a -> actualClasses.add(a.getClass()));
    assertEquals(expectedClasses, actualClasses);
  }
}