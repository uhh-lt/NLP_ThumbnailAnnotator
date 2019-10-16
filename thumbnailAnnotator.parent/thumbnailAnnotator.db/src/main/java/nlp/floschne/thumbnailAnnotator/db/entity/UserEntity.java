package nlp.floschne.thumbnailAnnotator.db.entity;


import lombok.*;
import org.springframework.data.annotation.Reference;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user_entity")
public class UserEntity extends Entity {

    @NonNull
    @Indexed
    private String username;

    //TODO this should be saved as e.g. SHA256 -> open an issue!
    @NonNull
    private String password;

    @Indexed
    private String accessKey;

    @Reference
    private List<CaptionTokenEntity> captionTokenEntities;
}
