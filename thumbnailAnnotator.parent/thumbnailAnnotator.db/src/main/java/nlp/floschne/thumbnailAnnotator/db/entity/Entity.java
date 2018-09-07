package nlp.floschne.thumbnailAnnotator.db.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.TimeToLive;

import java.io.Serializable;

@Data
public abstract class Entity implements Serializable {
    @TimeToLive
    private final static long TIME_TO_LIVE = 60 * 60 * 24;

    @Id
    private String id;
}
