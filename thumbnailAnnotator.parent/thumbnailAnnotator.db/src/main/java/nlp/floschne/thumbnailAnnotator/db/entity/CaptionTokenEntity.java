package nlp.floschne.thumbnailAnnotator.db.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("caption_token_entity")
public class CaptionTokenEntity extends Entity {

    @Indexed
    private String value;
    private String type;

    private List<String> posTags;
    private List<String> tokens;
}
