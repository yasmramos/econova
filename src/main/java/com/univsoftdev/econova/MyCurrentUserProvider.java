package com.univsoftdev.econova;

import com.univsoftdev.econova.config.model.User;
import io.ebean.DB;
import io.ebean.config.CurrentUserProvider;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MyCurrentUserProvider implements CurrentUserProvider {

    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();
    private static final User SYSTEM_USER = createSystemUser();

    public static void setUser(User user) {
        currentUser.set(user);
    }

    public static void clearUser() {
        currentUser.remove();
    }
    
    public static User getUser(){
        return currentUser.get();
    }
    
    @Override
    public User currentUser() {
        return getCurrentUser();
    }

    private User getCurrentUser() {
        try {
            User cUser = getUser();
            if (cUser == null) {
                log.warn("No user found in the current session. Returning system user.");
                return SYSTEM_USER;
            }

            return DB.find(User.class)
                    .where()
                    .eq("userName", cUser.getUserName())
                    .findOneOrEmpty().orElse(SYSTEM_USER);
        } catch (Exception e) {
            log.error("Error retrieving current user: {}", e.getMessage(), e);
            return SYSTEM_USER;
        }
    }
    
    private static User createSystemUser() {
        User systemUser = new User("system", "System User");
        systemUser.setAdminSistema(true);
        systemUser.setActivo(true);
        return systemUser;
    }
}
