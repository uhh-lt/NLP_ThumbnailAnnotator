package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
public abstract class Entity implements Serializable {
    @Id
    private String id;
}
