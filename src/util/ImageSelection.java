package util;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

/**
 * A {@code Transferable} which implements the capability required to transfer a {@code String}.
 * This {@code Transferable} properly supports {@code DataFlavor.imageFlavor}.
 */
public class ImageSelection implements Transferable, ClipboardOwner {

  private final BufferedImage image;

  public ImageSelection(BufferedImage image) {
    this.image = image;
  }

  @Override
  public DataFlavor[] getTransferDataFlavors() {
    return new DataFlavor[]{DataFlavor.imageFlavor};
  }

  @Override
  public boolean isDataFlavorSupported(DataFlavor flavor) {
    return DataFlavor.imageFlavor.equals(flavor);
  }

  @Override
  public Object getTransferData(DataFlavor flavor)
      throws UnsupportedFlavorException {
    if (isDataFlavorSupported(flavor)) {
      return image;
    } else {
      throw new UnsupportedFlavorException(flavor);
    }
  }

  @Override
  public void lostOwnership(Clipboard clipboard, Transferable contents) {
    // Do nothing.
  }
}
