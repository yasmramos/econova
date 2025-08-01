package com.univsoftdev.econova;

import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.system.Form;
import jakarta.inject.Singleton;
import raven.modal.Drawer;
import raven.modal.drawer.menu.MenuValidation;

@Singleton
public class MyMenuValidation extends MenuValidation {

    public static void setUser(User user) {
        MyMenuValidation.user = user;
    }

    public static User user;

    @Override
    public boolean menuValidation(int[] index) {
        return validation(index);
    }

    private static boolean checkMenu(int[] index, int[] indexHide) {
        if (index.length == indexHide.length) {
            for (int i = 0; i < index.length; i++) {
                if (index[i] != indexHide[i]) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    public static boolean validation(Class<? extends Form> itemClass) {
        int[] index = Drawer.getMenuIndexClass(itemClass);
        if (index == null) {
            return false;
        }
        return validation(index);
    }

    public static boolean validation(int[] index) {
        if (user == null) {
            return false;
        }
//        if (user.getRole() == ModelUser.Role.ADMIN) {
//            return true;
//        }

        boolean status
                // `Modal`
                = checkMenu(index, new int[]{2, 0})
                // `Components`->`Toast`
                && checkMenu(index, new int[]{2, 1})
                // `Forms`->`Responsive Layout`
                && checkMenu(index, new int[]{1, 2});

        return status;
    }
}
