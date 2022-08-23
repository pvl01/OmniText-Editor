package text;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

/**
 * A text editor application.
 */
public class OmniText {

  public static void main(String[] args) {
    FlatLightLaf.install();
    SwingUtilities.invokeLater(new OmniTextFrame());
  }
}
