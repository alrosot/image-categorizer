package trofo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trofo.model.Selection;
import trofo.model.SelectionRepository;

import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private SelectionRepository selectionRepository;

    private Map<Integer, String> categoryNames = new HashMap<>();

    public CategoryService() {
        categoryNames.put(1, "1 - D. Dirce/D. Maria");
        categoryNames.put(2, "2 - Familia do Miguel");
        categoryNames.put(3, "3 - Mostrar parentes");
    }

    public String getSelectedCategories(String file) {
        Collection<Selection> selections = selectionRepository.findByFile(file);

        Set<String> categories = new TreeSet<String>();
        for (Selection selection : selections) {
            String e = categoryNames.get(selection.getCategory());
            if (e == null) {
                e = Integer.toString(selection.getCategory());
            }
            categories.add(e);
        }

        return categories.toString();
    }
}
