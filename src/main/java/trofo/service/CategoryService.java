package trofo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trofo.dto.AlbumDto;
import trofo.model.Selection;
import trofo.model.SelectionRepository;

import java.util.*;

@Service
public class CategoryService {

    @Autowired
    private SelectionRepository selectionRepository;

    private Map<Integer, String> categoryNames = new HashMap<>();

    public CategoryService() {
        categoryNames.put(1, "Parentes Miguel");
        categoryNames.put(2, "Nosso");
        categoryNames.put(3, "porta retrato");
        categoryNames.put(0, "APAGAR");
    }

    public List<AlbumDto> getCategoriesInUse(){
        List<AlbumDto> albuns = new ArrayList<>();
        selectionRepository.findCategory().stream().forEach(integer -> albuns.add(new AlbumDto(integer, categoryNames.get(integer))));
        return albuns;
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
