package boundary;

import control.InvitoController;
import control.MainController;
import entity.Invito;
import entity.StatoInvito;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class InvitiPanel extends JPanel {

    private JPanel panel1;
    private JLabel labelTitolo;
    private JTabbedPane tabbedPane;

    private final MainController mainController;
    private final InvitoController invitoController = new InvitoController();

    private JPanel listaRicevutiPanel;
    private JPanel listaInviatiPanel;
    private JPanel listaStoricoPanel;

    public InvitiPanel(MainController mainController) {
        this.mainController = mainController;

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);
        util.UIStyle.titoloPagina(labelTitolo);
        tabbedPane.removeAll(); // elimina la tab "Untitled" generata dal designer

        costruisciTabRicevuti();
        costruisciTabInviati();
        costruisciTabStorico();

        caricaInviti();
    }

    private void costruisciTabRicevuti() {
        listaRicevutiPanel = new JPanel();
        listaRicevutiPanel.setLayout(new BoxLayout(listaRicevutiPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listaRicevutiPanel);
        scroll.setBorder(null);
        tabbedPane.addTab("Ricevuti", scroll);
    }

    private void costruisciTabInviati() {
        listaInviatiPanel = new JPanel();
        listaInviatiPanel.setLayout(new BoxLayout(listaInviatiPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listaInviatiPanel);
        scroll.setBorder(null);
        tabbedPane.addTab("Inviati", scroll);
    }

    private void costruisciTabStorico() {
        listaStoricoPanel = new JPanel();
        listaStoricoPanel.setLayout(new BoxLayout(listaStoricoPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(listaStoricoPanel);
        scroll.setBorder(null);
        tabbedPane.addTab("Storico", scroll);
    }

    public void caricaInviti() {
        caricaRicevuti();
        caricaInviati();
        caricaStorico();
    }

    // ---------- RICEVUTI (solo in attesa, con pulsanti azione) ----------

    private void caricaRicevuti() {
        listaRicevutiPanel.removeAll();

        try {
            List<Invito> inviti = invitoController.getInvitiRicevuti(
                    mainController.getUtenteLoggato().getMatricola());

            if (inviti.isEmpty()) {
                listaRicevutiPanel.add(messaggioVuoto("Non hai inviti pendenti al momento."));
            } else {
                for (Invito invito : inviti) {
                    listaRicevutiPanel.add(creaCardRicevuto(invito));
                    listaRicevutiPanel.add(Box.createVerticalStrut(10));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nel caricamento degli inviti ricevuti:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }

        listaRicevutiPanel.revalidate();
        listaRicevutiPanel.repaint();
    }

    private JPanel creaCardRicevuto(Invito invito) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel testo = new JPanel();
        testo.setLayout(new BoxLayout(testo, BoxLayout.Y_AXIS));

        JLabel nomeGruppo = new JLabel(invito.getNomeGruppo());
        nomeGruppo.setFont(nomeGruppo.getFont().deriveFont(Font.BOLD, 14f));

        JLabel mittente = new JLabel("Invito da " + invito.getNomeMittente() + " " + invito.getCognomeMittente());
        mittente.setForeground(Color.GRAY);

        testo.add(nomeGruppo);
        testo.add(mittente);
        card.add(testo, BorderLayout.WEST);

        JPanel pulsanti = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton rifiuta = new JButton("Rifiuta");
        rifiuta.addActionListener(e -> onRifiuta(invito));

        JButton accetta = new JButton("Accetta");
        accetta.addActionListener(e -> onAccetta(invito));

        pulsanti.add(rifiuta);
        pulsanti.add(accetta);
        card.add(pulsanti, BorderLayout.EAST);

        return card;
    }

    private void onAccetta(Invito invito) {
        try {
            invitoController.accettaInvito(invito.getIdInvito());
            caricaInviti();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nell'accettazione dell'invito:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRifiuta(Invito invito) {
        try {
            invitoController.rifiutaInvito(invito.getIdInvito());
            caricaInviti();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nel rifiuto dell'invito:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- INVIATI (quelli che ho spedito io) ----------

    private void caricaInviati() {
        listaInviatiPanel.removeAll();

        try {
            List<Invito> inviti = invitoController.getInvitiInviati(
                    mainController.getUtenteLoggato().getMatricola());

            if (inviti.isEmpty()) {
                listaInviatiPanel.add(messaggioVuoto("Non hai ancora inviato nessun invito."));
            } else {
                for (Invito invito : inviti) {
                    listaInviatiPanel.add(creaCardConStato(
                            invito.getNomeGruppo(),
                            "A " + invito.getNomeDestinatario() + " " + invito.getCognomeDestinatario(),
                            invito.getStato()));
                    listaInviatiPanel.add(Box.createVerticalStrut(10));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nel caricamento degli inviti inviati:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }

        listaInviatiPanel.revalidate();
        listaInviatiPanel.repaint();
    }

    // ---------- STORICO (tutti i ricevuti, ogni stato) ----------

    private void caricaStorico() {
        listaStoricoPanel.removeAll();

        try {
            List<Invito> inviti = invitoController.getStoricoRicevuti(
                    mainController.getUtenteLoggato().getMatricola());

            if (inviti.isEmpty()) {
                listaStoricoPanel.add(messaggioVuoto("Nessun invito ricevuto finora."));
            } else {
                for (Invito invito : inviti) {
                    listaStoricoPanel.add(creaCardConStato(
                            invito.getNomeGruppo(),
                            "Da " + invito.getNomeMittente() + " " + invito.getCognomeMittente(),
                            invito.getStato()));
                    listaStoricoPanel.add(Box.createVerticalStrut(10));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nel caricamento dello storico:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }

        listaStoricoPanel.revalidate();
        listaStoricoPanel.repaint();
    }

    // ---------- Componenti condivisi ----------

    private JPanel creaCardConStato(String titolo, String sottotitolo, StatoInvito stato) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel testo = new JPanel();
        testo.setLayout(new BoxLayout(testo, BoxLayout.Y_AXIS));

        JLabel labelTitolo = new JLabel(titolo);
        labelTitolo.setFont(labelTitolo.getFont().deriveFont(Font.BOLD, 14f));

        JLabel labelSottotitolo = new JLabel(sottotitolo);
        labelSottotitolo.setForeground(Color.GRAY);

        testo.add(labelTitolo);
        testo.add(labelSottotitolo);
        card.add(testo, BorderLayout.WEST);

        JLabel labelStato = new JLabel(testoStato(stato));
        labelStato.setFont(labelStato.getFont().deriveFont(Font.BOLD));
        labelStato.setForeground(coloreStato(stato));
        card.add(labelStato, BorderLayout.EAST);

        return card;
    }

    private String testoStato(StatoInvito stato) {
        return switch (stato) {
            case inAttesa -> "In attesa";
            case accettato -> "Accettato";
            case rifiutato -> "Rifiutato";
        };
    }

    private Color coloreStato(StatoInvito stato) {
        return switch (stato) {
            case inAttesa -> Color.GRAY;
            case accettato -> new Color(0, 150, 0);
            case rifiutato -> Color.RED;
        };
    }

    private JLabel messaggioVuoto(String testo) {
        JLabel label = new JLabel(testo);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

}