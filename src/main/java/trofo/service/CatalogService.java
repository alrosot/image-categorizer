package trofo.service;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trofo.model.Selection;
import trofo.model.SelectionRepository;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by arosot on 29/10/2017.
 */

@Service
public class CatalogService {


    @Autowired
    private SelectionRepository selectionRepository;

    @Autowired
    private CategoryService categoryService;

    public void generate() throws IOException {
        File htmlFile = File.createTempFile("categ", ".html");
        htmlFile.deleteOnExit();
        Set<Integer> categories = selectionRepository.findCategory();

        FileWriter fw = new FileWriter(htmlFile);

        fw.append("<html><header><link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0-beta.2/css/bootstrap.min.css\" integrity=\"sha384-PsH8R72JQ3SOdhVi3uxftmaW6Vc51MKb0q5P2rRUpPvrszuE4W1povHYgTpBfshb\" crossorigin=\"anonymous\">" +
                "</header><body>"
        );

        for (Integer categortId : categories) {

            final java.util.List<Selection> byCategory = selectionRepository.findByCategory(categortId);
            fw.append("<h2>" + categortId + " (" + byCategory.size() + ")</h2>");

            byCategory.stream().forEach(selection -> {
                try {
                    fw.append(getDivForImage(selection));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }

        fw.append("</body></html>");
        fw.close();

        Desktop.getDesktop().open(htmlFile);

    }

    @NotNull
    private String getDivForImage(Selection selection) {

        return "<div><img src=\"" + selection.getFile() + "\"class=\"img-thumbnail\" width=300px/>" +
                selection.getFile() +
                "</div>";
    }
}
