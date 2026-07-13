package boundary;

import control.GruppoController;
import control.MainController;
import control.SpesaController;
import entity.Gruppo;
import entity.SaldoUtente;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.sql.SQLException;
import java.util.List;

/**
 * La schermata "I miei gruppi" (mock-up 2.3).
 *
 * SCHEMA DI OGNI BOUNDARY (lo stesso di LoginFrame):
 *   1. istanzia i Control che le servono
 *   2. costruisce la grafica
 *   3. chiede i dati ai Control
 *   4. disegna i dati
 *   5. cattura le SQLException e le mostra all'utente
 *
 * Nessun DAO. Nessuna query. Nessun calcolo di quote.
 */
public class GruppiPanel extends JPanel {

    // 1) I Control. Come in LoginFrame: private final, creati qui dentro.
    private final GruppoController gruppoController = new GruppoController();
    private final SpesaController spesaController = new SpesaController();

    // Serve per sapere chi e' loggato e per navigare al dettaglio.
    private final MainController mainController;

    private JPanel listaCard;   // il contenitore verticale delle card

    public GruppiPanel(MainController mainController) {
        this.mainController = mainController;

        setLayout(new BorderLayout());
        setBackground(Theme.SFONDO);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        add(costruisciIntestazione(), BorderLayout.NORTH);
        add(costruisciLista(), BorderLayout.CENTER);

        caricaGruppi();   // <-- qui parte la catena verso il database
    }

    // ---------- grafica ----------

    private JPanel costruisciIntestazione() {
        JPanel intestazione = new JPanel(new BorderLayout());
        intestazione.setOpaque(false);
        intestazione.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));

        JLabel titolo = new JLabel("I miei gruppi");
        titolo.setFont(Theme.TITOLO_PAGINA);
        titolo.setForeground(Theme.TESTO);
        intestazione.add(titolo, BorderLayout.WEST);

        JButton nuovoGruppo = new JButton("+ Nuovo gruppo");
        nuovoGruppo.setFont(Theme.GRASSETTO);
        nuovoGruppo.setForeground(Theme.BLU);
        nuovoGruppo.setBackground(Theme.BLU_SFONDO);
        nuovoGruppo.setFocusPainted(false);
        nuovoGruppo.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "NuovoGruppoDialog arriva nello step 2.",
                        "In arrivo", JOptionPane.INFORMATION_MESSAGE));
        intestazione.add(nuovoGruppo, BorderLayout.EAST);

        return intestazione;
    }

    private JScrollPane costruisciLista() {
        listaCard = new JPanel();
        listaCard.setLayout(new BoxLayout(listaCard, BoxLayout.Y_AXIS));
        listaCard.setOpaque(false);

        JScrollPane scroll = new JScrollPane(listaCard);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(16);   // rotellina piu' reattiva
        return scroll;
    }

    // ---------- dati ----------

    /**
     * Chiede i gruppi al Control e crea una GruppoCard per ciascuno.
     * QUESTO e' il punto in cui una lista di lunghezza ignota diventa
     * una lista di componenti grafici: il GUI Designer non puo' farlo.
     */
    private void caricaGruppi() {
        String matricola = mainController.getUtenteLoggato().getMatricola();

        try {
            List<Gruppo> gruppi = gruppoController.getGruppiUtente(matricola);

            listaCard.removeAll();

            if (gruppi.isEmpty()) {
                listaCard.add(messaggioVuoto());
            } else {
                for (Gruppo gruppo : gruppi) {

                    // Il mio saldo in QUESTO gruppo: il Control mi da' i saldi di tutti,
                    // io cerco il mio. (Il calcolo vero lo fa la funzione SQL calcola_saldo.)
                    double mioSaldo = 0.0;
                    for (SaldoUtente s : gruppoController.getSaldi(gruppo.getIdGruppo())) {
                        if (s.getMatricola().trim().equals(matricola.trim())) {
                            mioSaldo = s.getSaldo();
                        }
                    }

                    int numeroSpese = spesaController.getStoricoSpese(gruppo.getIdGruppo()).size();
                    boolean proprietario = matricola.trim()
                            .equals(gruppo.getMatricolaCreatore().trim());

                    listaCard.add(new GruppoCard(
                            gruppo,
                            proprietario,
                            numeroSpese,
                            mioSaldo,
                            g -> mainController.mostraDettaglioGruppo(g)   // <-- il click naviga
                    ));
                    listaCard.add(Box.createVerticalStrut(12));
                }
            }

            listaCard.revalidate();
            listaCard.repaint();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Impossibile caricare i gruppi:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel messaggioVuoto() {
        JLabel vuoto = new JLabel("Non fai ancora parte di nessun gruppo. Creane uno!");
        vuoto.setFont(Theme.NORMALE);
        vuoto.setForeground(Theme.TESTO_LIEVE);
        vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
        vuoto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return vuoto;
    }
}