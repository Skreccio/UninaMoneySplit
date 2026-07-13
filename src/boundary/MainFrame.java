package boundary;

import control.MainController;
import entity.Utente;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Il contenitore post-login.
 *
 * A sinistra la sidebar (fissa). A destra il contentPanel, che cambia contenuto
 * a ogni navigazione. Questa classe NON sa cosa mostrare: lo decide il MainController.
 * Lei sa solo COME mostrarlo.
 *
 * Scritta interamente a codice (niente .form): la sidebar ha un badge dinamico,
 * uno spazio elastico e una voce attiva che cambia colore, cose che il GUI Designer
 * non gestisce bene.
 */
public class MainFrame extends JFrame {

    private final MainController mainController;

    private JPanel contentPanel;          // l'area che cambia
    private JLabel labelBadgeInviti;      // il "2" accanto a Inviti

    private VoceMenu voceGruppi;
    private VoceMenu voceInviti;
    private VoceMenu voceReport;

    public MainFrame(Utente utenteLoggato) {
        setTitle("UninaMoneySplit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setMinimumSize(new Dimension(820, 520));
        setLocationRelativeTo(null);

        // 1) Il Control nasce PRIMA della grafica: i pulsanti devono poterlo chiamare.
        this.mainController = new MainController(this, utenteLoggato);

        // 2) Costruisco la finestra
        JPanel radice = new JPanel(new BorderLayout());
        radice.add(costruisciSidebar(utenteLoggato), BorderLayout.WEST);

        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Theme.SFONDO);
        radice.add(contentPanel, BorderLayout.CENTER);

        setContentPane(radice);

        // 3) Schermata iniziale
        mainController.aggiornaBadgeInviti();
        mainController.mostraGruppi();
    }

    // ==================== SIDEBAR ====================

    private JPanel costruisciSidebar(Utente utente) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(Theme.CARD);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDO),   // linea di separazione a destra
                BorderFactory.createEmptyBorder(18, 14, 14, 14)));

        // --- logo ---
        JPanel logo = new JPanel();
        logo.setLayout(new BoxLayout(logo, BoxLayout.X_AXIS));
        logo.setOpaque(false);
        logo.add(new PallinoEuro());
        logo.add(Box.createHorizontalStrut(8));
        JLabel nomeApp = new JLabel("MoneySplit");
        nomeApp.setFont(Theme.TITOLO_CARD);
        nomeApp.setForeground(Theme.VERDE);
        logo.add(nomeApp);
        allineaSinistra(logo);
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(22));

        // --- dati utente ---
        JLabel nome = new JLabel(utente.getNome() + " " + utente.getCognome());
        nome.setFont(Theme.GRASSETTO);
        nome.setForeground(Theme.TESTO);
        allineaSinistra(nome);
        sidebar.add(nome);

        JLabel email = new JLabel(utente.getEmail());
        email.setFont(Theme.PICCOLO);
        email.setForeground(Theme.TESTO_LIEVE);
        allineaSinistra(email);
        sidebar.add(email);
        sidebar.add(Box.createVerticalStrut(24));

        // --- voci di navigazione ---
        // Ogni voce chiama un metodo del MainController. La finestra non decide nulla.
        voceGruppi = new VoceMenu("I miei gruppi", () -> mainController.mostraGruppi());
        voceInviti = new VoceMenu("Inviti",        () -> mainController.mostraInviti());
        voceReport = new VoceMenu("Report",        () -> mainController.mostraReport());

        labelBadgeInviti = voceInviti.getBadge();

        sidebar.add(voceGruppi);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(voceInviti);
        sidebar.add(Box.createVerticalStrut(2));
        sidebar.add(voceReport);

        // --- molla: spinge il Logout in fondo ---
        sidebar.add(Box.createVerticalGlue());

        JPanel separatore = new JPanel();
        separatore.setBackground(Theme.BORDO);
        separatore.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sidebar.add(separatore);
        sidebar.add(Box.createVerticalStrut(8));

        VoceMenu voceLogout = new VoceMenu("Logout", this::onLogout);
        sidebar.add(voceLogout);

        return sidebar;
    }

    private void allineaSinistra(JComponent c) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        // In un BoxLayout verticale i componenti si allungherebbero all'infinito:
        // blocchiamo l'altezza massima a quella preferita.
        c.setMaximumSize(new Dimension(Integer.MAX_VALUE, c.getPreferredSize().height));
    }

    // ==================== METODI CHIAMATI DAL CONTROLLER ====================

    /** Sostituisce il contenuto dell'area di destra. */
    public void setContentPanel(JPanel nuovoPanel) {
        contentPanel.removeAll();
        contentPanel.add(nuovoPanel, BorderLayout.CENTER);
        contentPanel.revalidate();   // ricalcola il layout
        contentPanel.repaint();      // ridisegna i pixel
    }

    /** Accende o spegne il pallino con il numero di inviti pendenti. */
    public void aggiornaBadgeInviti(int numeroInviti) {
        if (numeroInviti > 0) {
            labelBadgeInviti.setText(String.valueOf(numeroInviti));
            labelBadgeInviti.setVisible(true);
        } else {
            labelBadgeInviti.setVisible(false);
        }
    }

    /** Colora di blu la voce di menu attiva. */
    public void evidenziaVoceMenu(String voce) {
        voceGruppi.setAttiva("gruppi".equals(voce));
        voceInviti.setAttiva("inviti".equals(voce));
        voceReport.setAttiva("report".equals(voce));
    }

    /** Canale unico per segnalare un errore all'utente. */
    public void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Attenzione", JOptionPane.WARNING_MESSAGE);
    }

    // ==================== LOGOUT ====================

    private void onLogout() {
        int scelta = JOptionPane.showConfirmDialog(this,
                "Vuoi uscire dal tuo account?", "Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (scelta == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
        }
    }

    // ==================== COMPONENTI PRIVATI ====================

    /**
     * Una voce cliccabile della sidebar: testo a sinistra, badge opzionale a destra.
     * Sa cambiare aspetto quando e' attiva o quando ci passi sopra col mouse.
     */
    private static class VoceMenu extends JPanel {

        private final JLabel testo;
        private final JLabel badge;
        private boolean attiva = false;

        VoceMenu(String etichetta, Runnable azione) {
            setLayout(new BorderLayout());
            setOpaque(true);
            setBackground(Theme.CARD);
            setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
            setAlignmentX(Component.LEFT_ALIGNMENT);

            testo = new JLabel(etichetta);
            testo.setFont(Theme.NORMALE);
            testo.setForeground(Theme.TESTO_LIEVE);
            add(testo, BorderLayout.WEST);

            badge = new JLabel();
            badge.setFont(Theme.PICCOLO);
            badge.setForeground(Theme.BLU);
            badge.setVisible(false);
            add(badge, BorderLayout.EAST);

            addMouseListener(new MouseAdapter() {
                @Override public void mouseClicked(MouseEvent e) {
                    azione.run();   // <-- qui parte la navigazione
                }
                @Override public void mouseEntered(MouseEvent e) {
                    if (!attiva) setBackground(Theme.SFONDO);
                }
                @Override public void mouseExited(MouseEvent e) {
                    if (!attiva) setBackground(Theme.CARD);
                }
            });
        }

        JLabel getBadge() {
            return badge;
        }

        void setAttiva(boolean attiva) {
            this.attiva = attiva;
            setBackground(attiva ? Theme.BLU_SFONDO : Theme.CARD);
            testo.setForeground(attiva ? Theme.BLU : Theme.TESTO_LIEVE);
            testo.setFont(attiva ? Theme.GRASSETTO : Theme.NORMALE);
        }
    }

    /** Il pallino verde con la "E" di euro del logo. Disegnato a mano. */
    private static class PallinoEuro extends JComponent {

        PallinoEuro() {
            Dimension d = new Dimension(26, 26);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Theme.VERDE);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

            g2.setColor(Color.WHITE);
            g2.setFont(Theme.GRASSETTO);
            String s = "\u20AC";
            int x = (getWidth()  - g2.getFontMetrics().stringWidth(s)) / 2;
            int y = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 2;
            g2.drawString(s, x, y);

            g2.dispose();
        }
    }
}