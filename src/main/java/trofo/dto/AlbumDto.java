package trofo.dto;

/**
 * Created by arosot on 29/10/2017.
 */
public class AlbumDto {

    int category;
    String name;

    public AlbumDto(int category, String name) {
        this.category = category;
        this.name = name;
    }

    public int getCategory() {
        return category;
    }

    @Override
    public String toString() {
        return category + " - " + name;
    }
}
