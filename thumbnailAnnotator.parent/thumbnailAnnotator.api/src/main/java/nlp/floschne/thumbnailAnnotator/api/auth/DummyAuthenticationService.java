package nlp.floschne.thumbnailAnnotator.api.auth;

import nlp.floschne.thumbnailAnnotator.api.dto.AccessKeyDTO;
import nlp.floschne.thumbnailAnnotator.db.entity.UserEntity;
import nlp.floschne.thumbnailAnnotator.db.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Dummy Authentication Service.. This will later be replaced by Spring Oauth2 + JWT Authorization
 */
@Service
public class DummyAuthenticationService {
    private static final String DUMMY_USER = "DUMMY";
    private static final String DUMMY_PASSWORD = "dummy";

    private Set<String> activeSessions;

    private DBService dbService;

    @Autowired
    public DummyAuthenticationService(DBService dbService) {
        this.activeSessions = new HashSet<>();
        this.dbService = dbService;
        this.dbService.registerUser(DUMMY_USER, DUMMY_PASSWORD);
    }

    public AccessKeyDTO login(String user, String pw) {
        if (this.dbService.checkPassword(user, pw)) {
            this.activeSessions.add(dbService.getUserByUsername(user).getAccessKey());
            return new AccessKeyDTO(dbService.getUserByUsername(user).getAccessKey());
        }
        return null;
    }

    public AccessKeyDTO registerUser(String username, String pw) {
        UserEntity user = this.dbService.registerUser(username, pw);
        return new AccessKeyDTO(user.getAccessKey());
    }

    public boolean logout(String uuid) {
        return this.activeSessions.remove(uuid);
    }

    public boolean isActive(String uuid) {
        return this.activeSessions.contains(uuid);
    }
}
