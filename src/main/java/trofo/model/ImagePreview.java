package trofo.model;

import javax.swing.*;
import java.io.Serializable;

/**
 * Created by arosot on 14/04/2017.
 */
public class ImagePreview implements Serializable {

    private ImageIcon preview;
    private ImageIcon thumb;

    public ImagePreview(ImageIcon preview, ImageIcon thumb) {
        this.preview = preview;
        this.thumb = thumb;
    }

    public ImageIcon getPreview() {
        return preview;
    }

    public void setPreview(ImageIcon preview) {
        this.preview = preview;
    }

    public ImageIcon getThumb() {
        return thumb;
    }

    public void setThumb(ImageIcon thumb) {
        this.thumb = thumb;
    }
}
