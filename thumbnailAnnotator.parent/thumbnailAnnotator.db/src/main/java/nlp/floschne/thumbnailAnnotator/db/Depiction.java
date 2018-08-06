package nlp.floschne.thumbnailAnnotator.db;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.List;

@Data
@EqualsAndHashCode
@RedisHash("depictions")
@NoArgsConstructor
public class Depiction {

    @Id
    private String captionTokenValue;

    private List<String> thumbnailURLs;

    public Depiction(String captionTokenValue, List<String> thumbnailURLs) {
        this.captionTokenValue = captionTokenValue;
        this.thumbnailURLs = thumbnailURLs;
    }

}
