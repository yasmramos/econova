package com.univsoftdev.econova;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.univsoftdev.econova.config.model.User;
import com.univsoftdev.econova.core.system.Form;
import com.univsoftdev.econova.core.system.FormManager;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import raven.modal.drawer.DrawerPanel;
import raven.modal.drawer.menu.*;
import raven.modal.drawer.renderer.DrawerStraightDotLineStyle;
import raven.modal.drawer.simple.SimpleDrawerBuilder;
import raven.modal.drawer.simple.footer.SimpleFooterData;
import raven.modal.drawer.simple.header.SimpleHeaderData;
import raven.extras.AvatarIcon;

import javax.swing.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import raven.modal.drawer.item.MenuItem;
import raven.modal.drawer.simple.footer.LightDarkButtonFooter;
import raven.modal.drawer.simple.header.SimpleHeader;

public class EconovaDrawerBuilder extends SimpleDrawerBuilder {

    private final int SHADOW_SIZE = 12;
    private User currentUser;
    private static final Logger LOGGER = LoggerFactory.getLogger(EconovaDrawerBuilder.class);
    private final AppContext appContext = AppContext.getInstance();
    private static EconovaDrawerBuilder instance;
    private ModelUser user;

    private EconovaDrawerBuilder() {
        super(createSimpleMenuOption());
        LightDarkButtonFooter lightDarkButtonFooter = (LightDarkButtonFooter) getFooter();
        lightDarkButtonFooter.addModeChangeListener(isDarkMode -> {
            // event for light dark mode changed
        });
        this.currentUser = appContext.getSession().getCurrentUser();
    }

    public static EconovaDrawerBuilder getInstance() {
        if (instance == null) {
            instance = new EconovaDrawerBuilder();
        }
        return instance;
    }

    public ModelUser getUser() {
        return user;
    }

    public void setUser(ModelUser user) {
        boolean updateMenuItem = this.user == null || this.user.getRole() != user.getRole();

        this.user = user;

        // set user to menu validation
        MyMenuValidation.setUser(user);

        // setup drawer header
        SimpleHeader header = (SimpleHeader) getHeader();
        SimpleHeaderData data = header.getSimpleHeaderData();
        AvatarIcon icon = (AvatarIcon) data.getIcon();
        String iconName = user.getRole() == ModelUser.Role.ADMIN ? "avatar_male.svg" : "avatar_female.svg";

        icon.setIcon(new FlatSVGIcon("raven/modal/demo/drawer/image/" + iconName, 100, 100));
        data.setTitle(user.getUserName());
        data.setDescription(user.getMail());
        header.setSimpleHeaderData(data);

        if (updateMenuItem) {
            rebuildMenu();
        }
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        final AvatarIcon icon = new AvatarIcon(getClass().getResource("/raven/modal/demo/drawer/image/profile.png"), 50, 50, 3.5f);
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
        var context = AppContext.getInstance();
        return new SimpleFooterData()
                .setTitle("Econova")
                .setDescription("Version " + context.getVersion().toString());
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
                LOGGER.error(ex.getMessage());
                ex.printStackTrace();
            }
        });
        simpleMenuOption.setMenus(items)
                .setBaseIconPath("raven/modal/demo/drawer/icon")
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

    @Contract(pure = true)
    @NotNull
    private static String getDrawerBackgroundStyle() {
        return "[light]background:tint($Panel.background,20%);"
                + "[dark]background:tint($Panel.background,5%);";
    }
}
