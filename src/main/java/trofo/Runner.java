package trofo;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by arosot on 14/04/2017.
 */
@SpringBootApplication
@EnableJpaRepositories
public class Runner {

    public static void main(String[] args) {
        new SpringApplicationBuilder(Runner.class).headless(false).run(args);
    }
}
