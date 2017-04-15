package trofo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by arosot on 15/04/2017.
 */
@Entity
public class Selection {

    @Id
    @GeneratedValue
    private Long id;

    private String file;
    private int category;

    public Selection(String file, int category) {
        this.file = file;
        this.category = category;
    }

    public Selection() {
    }

    public Long getId() {
        return id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
