package nlp.floschne.thumbnailAnnotator.db.entity;


import jdk.nashorn.internal.ir.annotations.Reference;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
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
