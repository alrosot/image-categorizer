package trofo.form;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trofo.model.Selection;
import trofo.model.SelectionRepository;
import trofo.service.CategoryService;
import trofo.service.ImageService;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class Application {

    @Autowired
    private SelectionRepository selectionRepository;

    @Autowired
    private CategoryService categoryService;

    private JPanel panel1;
    private JLabel mainImage;
    private JLabel nextImg1;
    private JLabel nextImg2;
    private JLabel categoriesLabel;
    private JProgressBar progressBar;
    private JButton settingsButton;
    private JCheckBox bigPreviewCheckBox;

    private List<File> images;
    private int index = 0;

    private ImageService imageService = new ImageService();

    private final Logger LOG = LoggerFactory.getLogger(Application.class);

    private final int BACKGROUND_THREADS = 8;
    ExecutorService executor = Executors.newFixedThreadPool(BACKGROUND_THREADS);

    public Application() {
        $$$setupUI$$$();
        bindKeys();

        loadImagesNames();
        settingsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new Settings().showSettings();
            }
        });
        bigPreviewCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                redraw();
            }
        });
    }

    private void bindKeys() {
        bindKey("RIGHT", () -> Application.this.nextPic());
        bindKey("LEFT", () -> Application.this.previousPic());
        bindKey("Q", () -> Application.this.rotateLeft());
        bindKey("W", () -> Application.this.rotateRight());

        for (int i = 0; i < 10; i++) {
            int numericKey = i;
            bindKey(Integer.toString(numericKey), () -> Application.this.addCategory(numericKey));
        }
    }

    private void loadImagesNames() {
        images = new ArrayList<>();
        try {
            final BufferedReader bufferedReader = new BufferedReader(new FileReader(getClass().getResource("/directories.txt").getFile()));
            String aLine;
            while ((aLine = bufferedReader.readLine()) != null) {
                images.addAll(FileUtils.listFiles(
                        new File(aLine),
                        new String[]{"jpg", "jpeg"},
                        false
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getAbsolutePath().equals("D:\\Photos\\ju\\Fotos Celular\\201010\\201010A0\\10102010643.jpg")) {
                index = i;
                break;
            }
        }

        LOG.info("Brace yourselves! You are about to see " + images.size() + " pictures!");
    }

    private void rotateRight() {
        imageService.rotateRight(images.get(index).getAbsolutePath());
        redraw();
    }

    private void rotateLeft() {
        imageService.rotateLeft(images.get(index).getAbsolutePath());
        redraw();
    }

    private void bindKey(String right, final Runnable run) {
        panel1.getInputMap().put(KeyStroke.getKeyStroke(right), right);
        panel1.getActionMap().put(right, new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        run.run();
                    }
                }
        );
    }

    private int loopIndex(int index) {
        return (index + images.size()) % images.size();
    }

    private void previousPic() {
        index = loopIndex(index - 1);
        redraw();
    }

    private void nextPic() {
        index = loopIndex(index + 1);
        redraw();
    }

    private void redraw() {
        LOG.info("Displaying image " + images.get(index));
        try {
            if (bigPreviewCheckBox.isSelected()) {
                mainImage.setIcon(imageService.getImagePreview(images.get(index).getAbsolutePath(), !bigPreviewCheckBox.isSelected()).getPreview());
            } else {
                mainImage.setIcon(imageService.getImagePreview(images.get(index).getAbsolutePath(), !bigPreviewCheckBox.isSelected()).getThumb());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            nextImg1.setIcon(imageService.getImagePreview(images.get(loopIndex(index + 1)).getAbsolutePath(), !bigPreviewCheckBox.isSelected()).getThumb());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            nextImg2.setIcon(imageService.getImagePreview(images.get(loopIndex(index + 2)).getAbsolutePath(), !bigPreviewCheckBox.isSelected()).getThumb());
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateLabel();
        updateProgressBar();

        for (int i = 0; i < BACKGROUND_THREADS; i++) {
            final int j = 3 + index + i;
            executor.execute(() -> imageService.getImagePreview(images.get(loopIndex(j)).getAbsolutePath(), !bigPreviewCheckBox.isSelected()).getThumb());
        }
    }

    private void updateProgressBar() {
        progressBar.setMaximum(images.size());
        progressBar.setValue(index);
        progressBar.repaint();
    }

    private void updateLabel() {
        categoriesLabel.setText(categoryService.getSelectedCategories(images.get(index).getAbsolutePath()));
    }

    private void createUIComponents() {
        mainImage = new JLabel();
    }

    @PostConstruct
    public void showInterface() {

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // If Nimbus is not available, you can set the GUI to another look and feel.
        }

        final JFrame frame = new JFrame("Categorizer");
        frame.setContentPane($$$getRootComponent$$$());
        frame.pack();
        final Dimension minimumSize = new Dimension(1550, 1150);
        frame.setMinimumSize(minimumSize);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        redraw();
    }

    public void addCategory(int category) {
        final String file = images.get(index).getAbsolutePath();
        final Optional<Selection> persisted = selectionRepository.findByFileAndCategory(file, category);
        if (persisted.isPresent()) {
            selectionRepository.delete(persisted.get().getId());
        } else {
            selectionRepository.saveAndFlush(new Selection(file, category));
        }
        updateLabel();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        final JSplitPane splitPane1 = new JSplitPane();
        splitPane1.setResizeWeight(0.5);
        panel1.add(splitPane1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, new Dimension(200, 200), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setFocusTraversalPolicyProvider(false);
        panel2.setMinimumSize(new Dimension(1100, 1100));
        splitPane1.setLeftComponent(panel2);
        mainImage.setText("");
        panel2.add(mainImage, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        categoriesLabel = new JLabel();
        categoriesLabel.setText("---");
        panel2.add(categoriesLabel, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        progressBar = new JProgressBar();
        panel2.add(progressBar, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.setMinimumSize(new Dimension(200, 115));
        splitPane1.setRightComponent(panel3);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder(""));
        nextImg1 = new JLabel();
        nextImg1.setText("");
        panel5.add(nextImg1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel6 = new JPanel();
        panel6.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel4.add(panel6, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel6.setBorder(BorderFactory.createTitledBorder(""));
        nextImg2 = new JLabel();
        nextImg2.setHorizontalAlignment(0);
        nextImg2.setText("");
        panel6.add(nextImg2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final JPanel panel7 = new JPanel();
        panel7.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
        panel3.add(panel7, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        settingsButton = new JButton();
        settingsButton.setFocusable(false);
        settingsButton.setText("Settings");
        panel7.add(settingsButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bigPreviewCheckBox = new JCheckBox();
        bigPreviewCheckBox.setFocusable(false);
        bigPreviewCheckBox.setText("Big preview");
        panel7.add(bigPreviewCheckBox, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
