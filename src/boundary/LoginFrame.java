package boundary;

import control.LoginController;
import entity.Utente;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    private JPanel panelLogin;
    private JTextField campoEmail;
    private JPasswordField campoPassword;
    private JButton accediButton;
    private JLabel labelInsertEmail;
    private JLabel labelInsertPassword;
    private JLabel labelBentornato;
    private JLabel labelAccedi;

    private final LoginController loginController = new LoginController();

    public LoginFrame() {
        setTitle("UninaMoneySplit - Login");
        setContentPane(panelLogin);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(false);
        setLocationRelativeTo(null);
        accediButton.addActionListener(this::onLogin);
    }

    private void onLogin(ActionEvent e) {
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            mostraErrore("Inserisci email e password");
            return;
        }

        try {
            Utente utente = loginController.autentica(email, password);

            if (utente == null) {
                mostraErrore("Email o password non corretti");
                return;
            }

            dispose();
            SwingUtilities.invokeLater(() -> new MainFrame(utente).setVisible(true));

        } catch (SQLException ex) {
            mostraErrore("Errore di connessione al database");
            ex.printStackTrace();
        }
    }

    public void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }
}