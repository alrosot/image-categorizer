package trofo;

import trofo.form.Main;

import javax.swing.*;
import java.awt.*;

/**
 * Created by arosot on 14/04/2017.
 */
public class Runner {

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Categorizer");
        frame.setContentPane(new Main().$$$getRootComponent$$$());
        frame.pack();
        final Dimension minimumSize = new Dimension();
        minimumSize.setSize(1350,1200);
        frame.setMinimumSize(minimumSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
