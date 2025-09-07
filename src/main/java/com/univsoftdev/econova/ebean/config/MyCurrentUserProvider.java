package com.univsoftdev.econova.ebean.config;

import com.univsoftdev.econova.core.UserContext;
import com.univsoftdev.econova.config.model.User;
import io.ebean.config.CurrentUserProvider;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCurrentUserProvider implements CurrentUserProvider {

    public Optional<User> getUser() {
        User user = UserContext.get().getUser();
        log.trace("Getting current user: {}", user);
        return Optional.ofNullable(user);
    }

    @Override
    public User currentUser() {
        return getUser().orElseGet(() -> {
            User systemUser = new User();
            systemUser.setUserName("system");
            systemUser.setFullName("System User");
            return systemUser;
        });
    }
}
