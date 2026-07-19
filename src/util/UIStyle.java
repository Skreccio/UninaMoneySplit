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

    // Applica lo stile "bottone primario" (blu, angoli arrotondati) tramite le proprietà di FlatLaf
    public static void accentua(JButton bottone) {
        bottone.putClientProperty(FlatClientProperties.STYLE,
                "arc: 10; background: #2563EB; foreground: #FFFFFF; " +
                        "focusedBackground: #1D4ED8; hoverBackground: #1D4ED8; borderWidth: 0");
    }

    // Applica un bordo arrotondato e leggero a un pannello usato come "card"
    public static void arrotonda(JPanel card) {
        card.putClientProperty(FlatClientProperties.STYLE,
                "arc: 15; background: #FFFFFF; border: 12,15,12,15,#E2E8F0,1,15");
    }

    // Titolo di pagina: es. "I miei gruppi" nell'header di GruppiPanel
    public static void titoloPagina(JLabel label) {
        label.setFont(label.getFont().deriveFont(Font.BOLD, 20f));
    }

    // Applica l'icona dell'app (in tutte le risoluzioni disponibili) a una finestra
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