package trofo.service;

import com.sun.java.swing.plaf.windows.resources.windows;
import org.mapdb.DBMaker;
import trofo.model.ImagePreview;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by arosot on 14/04/2017.
 */
public class ImageService {

    static {
        sheetMap = DBMaker.memoryDB().make()
                .hashMap("imageMap")
                .expireStoreSize(512 * 1024 * 1024) //512mb

                .expireAfterCreate()
                .createOrOpen();
    }

    private static ConcurrentMap sheetMap;

    private Queue<String> requests = new ConcurrentLinkedQueue<>();

    public ImagePreview getImagePreview(String path) {
        ImagePreview image = (ImagePreview) sheetMap.get(path);
        if (image == null) {
            image = processImage(path);
            sheetMap.put(path, image);
        }
        return image;
    }

    private ImagePreview processImage(String path) {
        final ImageIcon imageIcon = new ImageIcon(path);
        final double iconHeight = imageIcon.getIconHeight();
        final double iconWidth = imageIcon.getIconWidth();

        double ratioPreview;
        double ratioThumb;

        if (iconHeight > iconWidth) {
            ratioPreview = 1100 / iconHeight;
            ratioThumb = 200 / iconHeight;
        } else {
            ratioPreview = 1100 / iconWidth;
            ratioThumb = 200 / iconWidth;
        }


        final Image image = imageIcon.getImage();
        return new ImagePreview(new ImageIcon(image.getScaledInstance((int) (iconWidth * ratioPreview), (int) (iconHeight * ratioPreview), Image.SCALE_SMOOTH)), new ImageIcon(image.getScaledInstance((int) (iconWidth * ratioThumb), (int) (iconHeight * ratioThumb), Image.SCALE_SMOOTH)));
    }
}
