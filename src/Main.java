import boundary.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {

        // Il look and feel va impostato PRIMA di creare qualsiasi componente Swing:
        // i componenti chiedono al LaF il proprio aspetto nel momento in cui nascono.
        FlatLightLaf.setup();

        // Qualche ritocco globale: angoli arrotondati e componenti piu' alti.
        UIManager.put("Button.arc", 8);
        UIManager.put("Component.arc", 8);
        UIManager.put("TextComponent.arc", 8);
        UIManager.put("Component.focusWidth", 1);

        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}