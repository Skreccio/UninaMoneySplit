package boundary;

import control.LoginController;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class RegistrazioneDialog extends JDialog {

    private JPanel panel1;
    private JLabel labelMatricola;
    private JTextField campoMatricola;
    private JLabel labelNome;
    private JTextField campoNome;
    private JLabel labelCognome;
    private JTextField campoCognome;
    private JLabel labelEmail;
    private JTextField campoEmail;
    private JLabel labelPassword;
    private JPasswordField campoPassword;
    private JButton bottoneAnnulla;
    private JButton bottoneRegistrati;

    private final LoginController loginController = new LoginController();
    private boolean registrazioneCompletata = false;

    public RegistrazioneDialog(JFrame parent) {
        super(parent, "Crea account", true);

        setContentPane(panel1);
        setSize(420, 420);
        setLocationRelativeTo(parent);

        bottoneAnnulla.addActionListener(e -> dispose());
        bottoneRegistrati.addActionListener(e -> onRegistrati());
    }

    private boolean validaInput() {
        return !campoMatricola.getText().trim().isEmpty()
                && !campoNome.getText().trim().isEmpty()
                && !campoCognome.getText().trim().isEmpty()
                && !campoEmail.getText().trim().isEmpty()
                && campoPassword.getPassword().length > 0;
    }

    private void onRegistrati() {
        if (!validaInput()) {
            JOptionPane.showMessageDialog(this,
                    "Compila tutti i campi obbligatori", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String matricola = campoMatricola.getText().trim();
        String nome = campoNome.getText().trim();
        String cognome = campoCognome.getText().trim();
        String email = campoEmail.getText().trim();
        String password = new String(campoPassword.getPassword());

        try {
            loginController.registra(matricola, nome, cognome, email, password);
            JOptionPane.showMessageDialog(this,
                    "Registrazione completata! Ora puoi accedere.", "Successo", JOptionPane.INFORMATION_MESSAGE);
            registrazioneCompletata = true;
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nella registrazione:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isRegistrazioneCompletata() {
        return registrazioneCompletata;
    }

}