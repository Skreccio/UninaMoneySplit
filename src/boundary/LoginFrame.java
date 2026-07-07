package boundary;

import control.LoginController;
import entity.Utente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.SQLException;

public class LoginFrame extends JFrame {

    // Componenti generati dal GUI Designer (nomi devono combaciare col .form)
    private JPanel panel1;
    private JTextField campoEmail;
    private JPasswordField campoPassword;
    private JButton accediButton;

    // Il controller che gestisce la logica di autenticazione
    private final LoginController loginController = new LoginController();

    public LoginFrame() {
        setTitle("UninaMoneySplit - Login");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // centra la finestra sullo schermo

        accediButton.addActionListener(this::onLogin);
    }

    private void onLogin(ActionEvent e) {
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        // Validazione lato GUI: evitiamo di interrogare il DB per input palesemente vuoti
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

            System.out.println("Login riuscito: " + utente);
            // Prossimo step: aprire MainFrame passandogli l'utente loggato
            dispose(); // chiude questa finestra

        } catch (SQLException ex) {
            mostraErrore("Errore di connessione al database");
            ex.printStackTrace(); // utile in fase di sviluppo per vedere lo stack completo in console
        }
    }

    public void mostraErrore(String messaggio) {
        JOptionPane.showMessageDialog(this, messaggio, "Errore", JOptionPane.ERROR_MESSAGE);
    }

}
