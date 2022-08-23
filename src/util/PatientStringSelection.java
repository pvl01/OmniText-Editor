package util;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * An extension of {@code StringSelection} that waits until a sufficient amount of time has passed
 * for the system clipboard to be ready.
 */
public class PatientStringSelection extends StringSelection {

  /**
   * Creates a {@code Transferable} capable of transferring the specified {@code String}.
   *
   * @param data the string to be transferred
   */
  public PatientStringSelection(String data) {
    super(data);
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    super.lostOwnership(clipboard, contents);
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
