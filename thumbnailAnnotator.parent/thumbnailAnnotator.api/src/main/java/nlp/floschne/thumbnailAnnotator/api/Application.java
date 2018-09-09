package nlp.floschne.thumbnailAnnotator.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"nlp.floschne.thumbnailAnnotator.api", "nlp.floschne.thumbnailAnnotator.db"})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}