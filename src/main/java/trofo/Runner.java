package trofo;

import trofo.form.Main;

import javax.swing.*;

/**
 * Created by arosot on 14/04/2017.
 */
public class Runner {

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Categorizer");
        frame.setContentPane(new Main().$$$getRootComponent$$$());
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
