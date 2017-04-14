package trofo.model;

import javax.persistence.Entity;

/**
 * Created by arosot on 15/04/2017.
 */
@Entity
public class Selection {

    private String file;
    private int categoy;

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getCategoy() {
        return categoy;
    }

    public void setCategoy(int categoy) {
        this.categoy = categoy;
    }
}
