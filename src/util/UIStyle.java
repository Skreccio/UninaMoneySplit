package util;

import com.formdev.flatlaf.FlatClientProperties;

import javax.swing.*;
import java.awt.Font;
import java.awt.Image;
import java.awt.Window;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UIStyle {

    private UIStyle() {}

    public static void accentua(JButton bottone) {
        bottone.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #2563EB; foreground: #FFFFFF; " +
                        "focusedBackground: #1D4ED8; hoverBackground: #1D4ED8; borderWidth: 0");
    }

    public static void arrotonda(JPanel card) {
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 15; background: #FFFFFF; border: 12,15,12,15,#E2E8F0,1,15");
    }

    public static void titoloPagina(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
    }

    public static void applicaIcona(Window finestra) {
        List<Image> icone = new ArrayList<>();
        for (int size : new int[]{16, 32, 48, 64, 128, 256}) {
            URL risorsa = UIStyle.class.getResource("/icon_" + size + ".png");
            if (risorsa != null) {
                icone.add(new ImageIcon(risorsa).getImage());
            }
        }
        if (!icone.isEmpty()) {
            finestra.setIconImages(icone);
        }
    }
}
