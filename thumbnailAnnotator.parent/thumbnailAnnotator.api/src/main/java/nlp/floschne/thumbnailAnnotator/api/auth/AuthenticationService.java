package nlp.floschne.thumbnailAnnotator.api.auth;

import lombok.extern.log4j.Log4j;
import nlp.floschne.thumbnailAnnotator.api.dto.AccessKeyDTO;
import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Dummy Authentication Service.. This will later be replaced by Spring Oauth2 + JWT Authorization
 */
@Service
@Log4j
public class AuthenticationService {
    private static final String DUMMY_USER = "DUMMY";
    private static final String DUMMY_PASSWORD = "b5a2c96250612366ea272ffac6d9744aaf4b45aacd96aa7cfcb931ee3b558259"; // sha256("dummy")

    private Set<String> activeSessions;

    private DBService dbService;

    @Autowired
    public AuthenticationService(DBService dbService) {
        this.activeSessions = new HashSet<>();
        this.dbService = dbService;
        this.registerUser(DUMMY_USER, DUMMY_PASSWORD);

        log.info("Authentication Service ready!");
    }

    public AccessKeyDTO login(@NotNull String user, @NotNull String pw) {
        if (this.dbService.checkPassword(user, pw)) {
            this.activeSessions.add(dbService.getUserByUsername(user).getAccessKey());
            log.info("User " + user + " logged in successfully!");
            return new AccessKeyDTO(dbService.getUserByUsername(user).getAccessKey());
        }
        return null;
    }

    public boolean registerUser(@NotNull String username, @NotNull String pw) {
        UserEntity user = this.dbService.registerUser(username, pw);
        if (user != null)
            log.info("Registered new User < " + username + " | " + pw + ">!");
        else
            log.warn("Cannot register User <" + username + "> because it already exists!");
        return user != null;
    }

    public boolean logout(@NotNull String uuid) {
        log.info("User " + uuid + " logged out successfully!");
        return this.activeSessions.remove(uuid);
    }

    public boolean isActive(@NotNull String uuid) {
        return this.activeSessions.contains(uuid);
    }
}
