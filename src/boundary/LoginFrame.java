package boundary;

import control.LoginController;
import entity.Utente;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private final LoginController loginController = new LoginController();

    private JTextField campoEmail;
    private JPasswordField campoPassword;

    public LoginFrame() {
        setTitle("UninaMoneySplit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(880, 540);
        setMinimumSize(new Dimension(720, 460));
        setLocationRelativeTo(null);

        JPanel radice = new JPanel(new GridLayout(1, 2));
        radice.add(costruisciForm());
        radice.add(costruisciPannelloLogo());
        setContentPane(radice);
    }

    private JPanel costruisciForm() {
        JPanel esterno = new JPanel(new BorderLayout());
        esterno.setBackground(Theme.CARD);

        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBackground(Theme.CARD);
        form.setBorder(BorderFactory.createEmptyBorder(0, 56, 0, 56));

        JLabel titolo = new JLabel("Bentornato");
        titolo.setFont(Theme.TITOLO_GRANDE);
        titolo.setForeground(Theme.TESTO);
        aggiungi(form, titolo);
        form.add(Box.createVerticalStrut(4));

        JLabel sottotitolo = new JLabel("Accedi al tuo account");
        sottotitolo.setFont(Theme.NORMALE);
        sottotitolo.setForeground(Theme.TESTO_LIEVE);
        aggiungi(form, sottotitolo);
        form.add(Box.createVerticalStrut(28));

        aggiungi(form, etichetta("Email istituzionale"));
        form.add(Box.createVerticalStrut(5));
        campoEmail = new JTextField();
        campoEmail.setFont(Theme.NORMALE);
        aggiungi(form, campoEmail);
        campoEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        form.add(Box.createVerticalStrut(16));

        aggiungi(form, etichetta("Password"));
        form.add(Box.createVerticalStrut(5));
        campoPassword = new JPasswordField();
        campoPassword.setFont(Theme.NORMALE);
        aggiungi(form, campoPassword);
        campoPassword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        form.add(Box.createVerticalStrut(26));

        JButton accedi = new JButton("Accedi");
        accedi.setFont(Theme.GRASSETTO);
        accedi.setForeground(Theme.BLU);
        accedi.setBackground(Theme.BLU_SFONDO);
        accedi.setFocusPainted(false);
        accedi.addActionListener(e -> onLogin());
        aggiungi(form, accedi);
        accedi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        getRootPane().setDefaultButton(accedi);

        form.add(Box.createVerticalStrut(18));

        JPanel riga = new JPanel();
        riga.setLayout(new BoxLayout(riga, BoxLayout.X_AXIS));
        riga.setOpaque(false);

        JLabel domanda = new JLabel("Non hai un account?  ");
        domanda.setFont(Theme.PICCOLO);
        domanda.setForeground(Theme.TESTO_LIEVE);
        riga.add(domanda);

        JLabel link = new JLabel("Crea un account");
        link.setFont(Theme.GRASSETTO);
        link.setForeground(Theme.BLU);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        link.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginFrame.this,
                        "Il RegistrazioneFrame arriva piu' avanti.\n" +
                                "Per ora usa un utente gia' presente nel database.",
                        "In arrivo", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        riga.add(link);

        aggiungi(form, riga);

        esterno.add(form, BorderLayout.CENTER);
        return esterno;
    }

    private JLabel etichetta(String testo) {
        JLabel l = new JLabel(testo);
        l.setFont(Theme.PICCOLO);
        l.setForeground(Theme.TESTO_LIEVE);
        return l;
    }

    private void aggiungi(JPanel contenitore, JComponent componente) {
        componente.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenitore.add(componente);
    }

    private JPanel costruisciPannelloLogo() {
        JPanel pannello = new JPanel();
        pannello.setLayout(new BoxLayout(pannello, BoxLayout.Y_AXIS));
        pannello.setBackground(Theme.VERDE_SFONDO);

        pannello.add(Box.createVerticalGlue());

        CerchioEuro cerchio = new CerchioEuro();
        cerchio.setAlignmentX(Component.CENTER_ALIGNMENT);
        pannello.add(cerchio);
        pannello.add(Box.createVerticalStrut(16));

        JLabel nome = new JLabel("UninaMoneySplit");
        nome.setFont(Theme.TITOLO_PAGINA);
        nome.setForeground(Theme.VERDE);
        nome.setAlignmentX(Component.CENTER_ALIGNMENT);
        pannello.add(nome);
        pannello.add(Box.createVerticalStrut(6));

        JLabel slogan = new JLabel("spese condivise, conti chiari");
        slogan.setFont(Theme.PICCOLO);
        slogan.setForeground(Theme.VERDE);
        slogan.setAlignmentX(Component.CENTER_ALIGNMENT);
        pannello.add(slogan);

        pannello.add(Box.createVerticalGlue());
        return pannello;
    }

    private void onLogin() {
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            mostraErrore("Inserisci email e password.");
            return;
        }

        try {
            Utente utente = loginController.autentica(email, password);

            if (utente == null) {
                mostraErrore("Email o password non corretti.");
                return;
            }

            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(utente).setVisible(true));

        } catch (SQLException ex) {
            mostraErrore("Errore di connessione al database:\n" + ex.getMessage());
        }
    }

    private void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Attenzione", JOptionPane.WARNING_MESSAGE);
    }

    private static class CerchioEuro extends JComponent {

        CerchioEuro() {
            Dimension d = new Dimension(64, 64);
            setPreferredSize(d);
            setMinimumSize(d);
            setMaximumSize(d);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            g2.setColor(Theme.VERDE);
            g2.fillOval(0, 0, getWidth(), getHeight());

            g2.setColor(Color.WHITE);
            g2.setFont(Theme.TITOLO_GRANDE);
            String s = "\u20AC";
            int x = (getWidth()  - g2.getFontMetrics().stringWidth(s)) / 2;
            int y = (getHeight() + g2.getFontMetrics().getAscent()) / 2 - 4;
            g2.drawString(s, x, y);

            g2.dispose();
        }
    }
}