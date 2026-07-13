package boundary;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Design System centralizzato dell'applicazione.
 * Classe utility: costruttore privato, solo costanti e metodi statici.
 */
public final class Theme {

    private Theme() { }

    // ---------- COLORI ----------
    public static final Color SFONDO      = new Color(0xF4F5F7);
    public static final Color CARD        = Color.WHITE;
    public static final Color BORDO       = new Color(0xE3E6EA);
    public static final Color TESTO       = new Color(0x1F2937);
    public static final Color TESTO_LIEVE = new Color(0x6B7280);

    public static final Color BLU         = new Color(0x2563EB);
    public static final Color BLU_SFONDO  = new Color(0xEAF1FE);

    // Palette SEMANTICA: verde = credito, rosso = debito. Sempre. Ovunque.
    public static final Color VERDE        = new Color(0x15803D);
    public static final Color VERDE_SFONDO = new Color(0xE9F4EE);   // pannello logo di Login/Registrazione
    public static final Color ROSSO        = new Color(0xDC2626);

    // ---------- FONT ----------
    private static final String FAMIGLIA = scegliFamiglia();

    public static final Font TITOLO_GRANDE = new Font(FAMIGLIA, Font.BOLD,  26);
    public static final Font TITOLO_PAGINA = new Font(FAMIGLIA, Font.BOLD,  20);
    public static final Font TITOLO_CARD   = new Font(FAMIGLIA, Font.BOLD,  15);
    public static final Font GRASSETTO     = new Font(FAMIGLIA, Font.BOLD,  13);
    public static final Font NORMALE       = new Font(FAMIGLIA, Font.PLAIN, 13);
    public static final Font PICCOLO       = new Font(FAMIGLIA, Font.PLAIN, 12);

    private static String scegliFamiglia() {
        String[] preferiti = { "Inter", "Segoe UI", "SF Pro Text", "Helvetica Neue", "Roboto" };
        List<String> disponibili = Arrays.asList(
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames());
        for (String f : preferiti) {
            if (disponibili.contains(f)) {
                return f;
            }
        }
        return Font.SANS_SERIF;
    }

    // ---------- FORMATTAZIONE ----------

    public static String euro(double valore) {
        String cifre = String.format(Locale.ITALY, "%,.2f \u20AC", Math.abs(valore));
        if (valore > 0) return "+" + cifre;
        if (valore < 0) return "\u2212" + cifre;
        return cifre;
    }

    public static Color coloreSaldo(double saldo) {
        if (saldo > 0) return VERDE;
        if (saldo < 0) return ROSSO;
        return TESTO_LIEVE;
    }
}