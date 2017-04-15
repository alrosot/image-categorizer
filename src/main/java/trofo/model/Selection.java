package trofo.model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by arosot on 15/04/2017.
 */
@Entity
public class Selection {

    @Id
    private Long id;

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
