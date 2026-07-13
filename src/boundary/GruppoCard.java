package boundary;

import entity.Gruppo;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/**
 * La scheda di UN gruppo dentro GruppiPanel.
 *
 * E' un componente riutilizzabile: GruppiPanel ne crea una per ogni gruppo,
 * dentro un ciclo. Riceve solo dei DATI gia' pronti e un'AZIONE da eseguire
 * al click: non chiama controller, non tocca il database. E' un pezzo di vetrina.
 */
public class GruppoCard extends JPanel {

    public GruppoCard(Gruppo gruppo,
                      boolean proprietario,
                      int numeroSpese,
                      double saldoUtente,
                      Consumer<Gruppo> alClick) {

        setLayout(new BorderLayout());
        setBackground(Theme.CARD);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDO, 1, true),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setAlignmentX(Component.LEFT_ALIGNMENT);
        // In un BoxLayout verticale, senza questo la card si allungherebbe a tutta l'altezza.
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        // --- colonna di testo ---
        JPanel testo = new JPanel();
        testo.setLayout(new BoxLayout(testo, BoxLayout.Y_AXIS));
        testo.setOpaque(false);

        // riga 1: nome del gruppo + eventuale etichetta "proprietario"
        JPanel riga1 = new JPanel();
        riga1.setLayout(new BoxLayout(riga1, BoxLayout.X_AXIS));
        riga1.setOpaque(false);
        riga1.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titolo = new JLabel(gruppo.getNome());
        titolo.setFont(Theme.TITOLO_CARD);
        titolo.setForeground(Theme.TESTO);
        riga1.add(titolo);

        if (proprietario) {
            riga1.add(Box.createHorizontalStrut(10));
            JLabel chip = new JLabel("proprietario");
            chip.setFont(Theme.PICCOLO);
            chip.setForeground(Theme.TESTO_LIEVE);
            chip.setOpaque(true);
            chip.setBackground(Theme.SFONDO);
            chip.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            riga1.add(chip);
        }
        testo.add(riga1);
        testo.add(Box.createVerticalStrut(5));

        // riga 2: "3 partecipanti - 12 spese"
        JLabel meta = new JLabel(
                gruppo.getNumPartecipanti() + " partecipanti \u00B7 " + numeroSpese + " spese");
        meta.setFont(Theme.PICCOLO);
        meta.setForeground(Theme.TESTO_LIEVE);
        meta.setAlignmentX(Component.LEFT_ALIGNMENT);
        testo.add(meta);
        testo.add(Box.createVerticalStrut(5));

        // riga 3: "Il tuo saldo: +34,50 EUR"  (verde o rosso)
        JPanel riga3 = new JPanel();
        riga3.setLayout(new BoxLayout(riga3, BoxLayout.X_AXIS));
        riga3.setOpaque(false);
        riga3.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel etichettaSaldo = new JLabel("Il tuo saldo:  ");
        etichettaSaldo.setFont(Theme.PICCOLO);
        etichettaSaldo.setForeground(Theme.TESTO_LIEVE);
        riga3.add(etichettaSaldo);

        JLabel valoreSaldo = new JLabel(Theme.euro(saldoUtente));
        valoreSaldo.setFont(Theme.GRASSETTO);
        valoreSaldo.setForeground(Theme.coloreSaldo(saldoUtente));   // <-- palette semantica
        riga3.add(valoreSaldo);

        testo.add(riga3);
        add(testo, BorderLayout.WEST);

        // --- interazione ---
        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                alClick.accept(gruppo);          // <-- la card non sa cosa succedera'. Lo decide chi l'ha creata.
            }
            @Override public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BLU, 1, true),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)));
            }
            @Override public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDO, 1, true),
                        BorderFactory.createEmptyBorder(14, 16, 14, 16)));
            }
        });
    }
}