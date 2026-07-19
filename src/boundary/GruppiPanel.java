package boundary;

import control.GruppoController;
import control.MainController;
import entity.Gruppo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class GruppiPanel extends JPanel {
    private JPanel panel1;
    private JPanel headerPanel;
    private JLabel labelTitolo;
    private JButton bottoneNuovoGruppo;
    private JPanel listaGruppiPanel;

    private final MainController mainController;
    private final GruppoController gruppoController = new GruppoController();

    public GruppiPanel(MainController mainController) {
        this.mainController = mainController;

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);

        listaGruppiPanel.setLayout(new BoxLayout(listaGruppiPanel, BoxLayout.Y_AXIS));

        bottoneNuovoGruppo.addActionListener(e -> onNuovoGruppo());
        util.UIStyle.accentua(bottoneNuovoGruppo);
        caricaGruppi();
        util.UIStyle.titoloPagina(labelTitolo);
    }

    public void caricaGruppi() {
        listaGruppiPanel.removeAll();

        try {
            java.util.List<Gruppo> gruppi = gruppoController.getGruppiUtente(
                    mainController.getUtenteLoggato().getMatricola());

            if (gruppi.isEmpty()) {
                JLabel vuoto = new JLabel("Non fai ancora parte di nessun gruppo. Creane uno!");
                vuoto.setAlignmentX(Component.LEFT_ALIGNMENT);
                listaGruppiPanel.add(vuoto);
            } else {
                for (Gruppo gruppo : gruppi) {
                    listaGruppiPanel.add(creaGruppoCard(gruppo));
                    listaGruppiPanel.add(Box.createVerticalStrut(10));
                }
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dei gruppi");
            ex.printStackTrace();
        }

        listaGruppiPanel.revalidate();
        listaGruppiPanel.repaint();
    }

    private JPanel creaGruppoCard(Gruppo gruppo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        util.UIStyle.arrotonda(card);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JLabel labelNome = new JLabel(gruppo.getNome());
        labelNome.setFont(labelNome.getFont().deriveFont(Font.BOLD, 15f));

        JLabel labelInfo = new JLabel(gruppo.getNumPartecipanti() + " partecipanti");
        labelInfo.setForeground(Color.GRAY);

        card.add(labelNome);
        card.add(labelInfo);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onSelezionaGruppo(gruppo);
            }
        });
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return card;
    }

    private void onSelezionaGruppo(Gruppo gruppo) {
        mainController.mostraDettaglioGruppo(gruppo);
    }

    private void onNuovoGruppo() {
        NuovoGruppoDialog dialog = new NuovoGruppoDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                mainController.getUtenteLoggato().getMatricola());
        dialog.setVisible(true);

        if (dialog.isGruppoCreato()) {
            caricaGruppi();
        }
    }
}
