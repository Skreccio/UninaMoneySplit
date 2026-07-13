package boundary;

import control.MainController;
import entity.Utente;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel panel1;
    private JPanel menuPanel;
    private JPanel contentPanel;
    private JLabel labelNomeUtente;
    private JLabel labelEmailUtente;
    private JButton bottoneGruppi;
    private JButton bottoneInviti;
    private JLabel labelBadgeInviti;
    private JButton bottoneLogout;

    private MainController mainController;

    public MainFrame(Utente utenteLoggato) {
        setTitle("UninaMoneySplit");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 650);
        setLocationRelativeTo(null);

        this.mainController = new MainController(this, utenteLoggato);

        labelNomeUtente.setText(utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
        labelEmailUtente.setText(utenteLoggato.getEmail());

        bottoneGruppi.addActionListener(e -> mainController.showPanel("gruppi"));
        bottoneInviti.addActionListener(e -> mainController.showPanel("inviti"));
        bottoneLogout.addActionListener(e -> onLogout());

        mainController.showPanel("gruppi");
    }

    public void setContentPanel(JPanel nuovoPanel) {
        contentPanel.removeAll();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(nuovoPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void aggiornaBadgeInviti(int numeroInviti) {
        if (numeroInviti > 0) {
            labelBadgeInviti.setText(String.valueOf(numeroInviti));
            labelBadgeInviti.setVisible(true);
        } else {
            labelBadgeInviti.setVisible(false);
        }
    }

    private void onLogout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}