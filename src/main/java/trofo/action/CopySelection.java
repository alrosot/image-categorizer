package trofo.action;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import trofo.model.SelectionRepository;

import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by arosot on 29/10/2017.
 */
@Component
public class CopySelection implements AlbumAction {

    @Autowired
    private SelectionRepository selectionRepository;

    @Override
    public String toString() {
        return "Copy temp folder";
    }

    @Override
    public void execute(int category) throws IOException {
        File tempFolder = Files.createTempDir();

        selectionRepository.findByCategory(category).forEach(selection -> {
            File source = new File(selection.getFile());
            File dest = new File(tempFolder.getAbsolutePath() + "/" + source.getName());
            System.out.println("Copying " + source + " to " + dest);
            try {
                FileUtils.copyFile(source, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        System.out.println();
        Desktop.getDesktop().open(tempFolder);


    }
}
