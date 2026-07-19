package util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;

public class UIStyle {

    private UIStyle() {}

    // Applica lo stile "bottone primario" (blu, angoli arrotondati) tramite le proprietà di FlatLaf
    public static void accentua(JButton bottone) {
        bottone.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #2563EB; foreground: #FFFFFF; " +
                        "focusedBackground: #1D4ED8; hoverBackground: #1D4ED8; borderWidth: 0");
    }
}