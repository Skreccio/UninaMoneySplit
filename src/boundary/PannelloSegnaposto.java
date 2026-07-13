package boundary;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;

/**
 * TEMPORANEO. Tiene il posto delle schermate non ancora scritte,
 * cosi' il programma compila e gira mentre le costruiamo una alla volta.
 *
 * Nota: prima questo codice stava dentro MainController, cioe' in un Control.
 * Un Control non deve costruire componenti grafici: l'abbiamo spostato qui,
 * nella Boundary, dove i JPanel sono di casa.
 *
 * Da cancellare quando tutte le schermate saranno pronte.
 */
public class PannelloSegnaposto extends JPanel {

    public PannelloSegnaposto(String titolo, String sottotitolo) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Theme.SFONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        JLabel t = new JLabel(titolo);
        t.setFont(Theme.TITOLO_PAGINA);
        t.setForeground(Theme.TESTO);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(t);

        add(Box.createVerticalStrut(8));

        JLabel s = new JLabel(sottotitolo);
        s.setFont(Theme.NORMALE);
        s.setForeground(Theme.TESTO_LIEVE);
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(s);

        add(Box.createVerticalGlue());
    }
}