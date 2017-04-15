package trofo.service;

import org.mapdb.DBMaker;
import trofo.model.ImagePreview;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
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

    public void rotateLeft(String path) {
        rotate(path, -1.5708);
    }

    public void rotateRight(String path) {
        rotate(path, 1.5708);
    }

    private void rotate(String path, double theta) {

        final ImagePreview imagePreview = getImagePreview(path);
        imagePreview.setPreview(rotate(imagePreview.getPreview(), theta));
        imagePreview.setThumb(rotate(imagePreview.getThumb(), theta));
        sheetMap.put(path, imagePreview);
    }

    public static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }

    private ImageIcon rotate(ImageIcon imageIcon, double theta) {
        AffineTransform transform = new AffineTransform();
        final Image image = imageIcon.getImage();
        final BufferedImage src = toBufferedImage(image);

        transform.rotate(theta, imageIcon.getIconWidth() / 2.0, imageIcon.getIconHeight() / 2.0);
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

        return new ImageIcon(op.filter(src, null));
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
