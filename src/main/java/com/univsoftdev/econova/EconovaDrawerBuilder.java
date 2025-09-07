package com.univsoftdev.econova;

import com.univsoftdev.econova.core.AppContext;
import com.univsoftdev.econova.core.Injector;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.UserContext;
import com.univsoftdev.econova.core.config.AppConfig;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.system.FormManager;
import jakarta.validation.constraints.NotNull;
import java.lang.reflect.InvocationTargetException;
import raven.modal.drawer.DrawerPanel;
import raven.modal.drawer.menu.*;
import raven.modal.drawer.renderer.DrawerStraightDotLineStyle;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.footer.SimpleFooterData;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import raven.extras.AvatarIcon;

import javax.swing.*;
import lombok.extern.slf4j.Slf4j;
import raven.modal.drawer.item.MenuItem;
import raven.modal.drawer.simple.footer.LightDarkButtonFooter;
import raven.modal.drawer.simple.header.SimpleHeader;

@Slf4j
public class EconovaDrawerBuilder extends SimpleDrawerBuilder {

    private final int SHADOW_SIZE = 12;
    private static EconovaDrawerBuilder instance;
    private User user;
    

    private EconovaDrawerBuilder() {
        super(createSimpleMenuOption());
        LightDarkButtonFooter lightDarkButtonFooter = (LightDarkButtonFooter) getFooter();
        lightDarkButtonFooter.addModeChangeListener(isDarkMode -> {
            // event for light dark mode changed
        });
    }

    public static EconovaDrawerBuilder getInstance() {
        if (instance == null) {
            instance = new EconovaDrawerBuilder();
        }
        return instance;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        boolean updateMenuItem = this.user == null || this.user.getRoles() != user.getRoles();

        this.user = user;

        // set user to menu validation
        MyMenuValidation.setUser(user);
        UserContext.get().setUser(user);
        // setup drawer header
        SimpleHeader header = (SimpleHeader) getHeader();
        SimpleHeaderData data = header.getSimpleHeaderData();
        AvatarIcon icon = (AvatarIcon) data.getIcon();
        String iconName = "avatar_male.svg";

        icon.setIcon(new FlatSVGIcon("econova/drawer/image/" + iconName, 100, 100));
        data.setTitle(user.getUserName());
        data.setDescription(user.getEmail());
        header.setSimpleHeaderData(data);

        if (updateMenuItem) {
            rebuildMenu();
        }
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        final AvatarIcon icon = new AvatarIcon(getClass().getResource("/econova/drawer/image/profile.png"), 50, 50, 3.5f);
        icon.setType(AvatarIcon.Type.MASK_SQUIRCLE);
        icon.setBorder(2, 2);

        changeAvatarIconBorderColor(icon);

        UIManager.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals("lookAndFeel")) {
                changeAvatarIconBorderColor(icon);
            }
        });

        return new SimpleHeaderData()
                .setIcon(icon)
                .setTitle("")
                .setDescription("");
    }

    private void changeAvatarIconBorderColor(@NotNull AvatarIcon icon) {
        icon.setBorderColor(new AvatarIcon.BorderColor(UIManager.getColor("Component.accentColor"), 0.7f));
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle("Econova")
                .setDescription("Version " + AppConfig.getAppVersion());
    }

    @NotNull
    public static MenuOption createSimpleMenuOption() {

        final MenuOption simpleMenuOption = new MenuOption();
        final MenuItem[] items = MenuFactory.buildMenuItems();

        simpleMenuOption.setMenuStyle(new MenuStyle() {
            @Override
            public void styleMenu(JComponent component) {
                component.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
            }

            @Override
            public void styleMenuItem(JButton menu, int[] index, boolean isMainItem) {
                if (isMainItem) {
                    menu.putClientProperty(FlatClientProperties.STYLE, ""
                            + "selectedForeground:$Component.accentColor;"
                            + "selectedBackground:null;");
                }
            }
        });
        simpleMenuOption.getMenuStyle().setDrawerLineStyleRenderer(new DrawerStraightDotLineStyle());
        simpleMenuOption.setMenuItemAutoSelectionMode(MenuOption.MenuItemAutoSelectionMode.SELECT_SUB_MENU_LEVEL);
        simpleMenuOption.addMenuEvent((MenuAction action, int[] index) -> {
            final Class<?> itemClass = action.getItem().getItemClass();
            int i = index[0];
            if (i == 11) {
                action.consume();
                FormManager.showAbout();
                return;
            } else if (i == 12) {
                action.consume();
                FormManager.logout();
                return;
            }
            if (itemClass == null || !Form.class.isAssignableFrom(itemClass)) {
                action.consume();
                return;
            }
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Form> formClass = (Class<? extends Form>) itemClass;
                //FormManager.showForm(AllForms.getForm(formClass));
                Form form = formClass.getDeclaredConstructor().newInstance();
                FormManager.showForm(form);
            } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                log.error(ex.getMessage());
                ex.printStackTrace();
            }
        });
        simpleMenuOption.setMenus(items)
                .setBaseIconPath("econova/drawer/icon")
                .setIconScale(0.45f);
        return simpleMenuOption;
    }

    @Override
    public int getDrawerWidth() {
        return 270 + SHADOW_SIZE;
    }

    @Override
    public int getDrawerCompactWidth() {
        return 80 + SHADOW_SIZE;
    }

    @Override
    public int getOpenDrawerAt() {
        return 1000;
    }

    @Override
    public boolean openDrawerAtScale() {
        return false;
    }

    @Override
    public void build(@NotNull DrawerPanel drawerPanel) {
        drawerPanel.putClientProperty(FlatClientProperties.STYLE, getDrawerBackgroundStyle());
    }

    @NotNull
    private static String getDrawerBackgroundStyle() {
        return "[light]background:tint($Panel.background,20%);"
                + "[dark]background:tint($Panel.background,5%);";
    }
}
