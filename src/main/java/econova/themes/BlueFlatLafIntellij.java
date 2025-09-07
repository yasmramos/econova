package econova.themes;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class BlueFlatLafIntellij extends FlatIntelliJLaf {

    public static final String NAME = "BlueFlatLafIntellij";

    public static boolean setup() {
        return setup(new BlueFlatLafIntellij());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, BlueFlatLafIntellij.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
