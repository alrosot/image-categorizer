package trofo.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by arosot on 15/04/2017.
 */
public class PersistentStorage {

    static {
        try {
            String dbName = "categorizer-" + System.currentTimeMillis();
            connection = DriverManager.getConnection("jdbc:hsqldb:file:" + dbName + ";shutdown=true", "SA", "");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Connection connection = null;


}
