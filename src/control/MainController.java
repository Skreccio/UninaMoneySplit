package control;

import boundary.MainFrame;
import entity.Utente;

import javax.swing.*;
import java.awt.*;

public class MainController {

    private final MainFrame mainFrame;
    private final Utente utenteLoggato;

    public MainController(MainFrame mainFrame, Utente utenteLoggato) {
        this.mainFrame = mainFrame;
        this.utenteLoggato = utenteLoggato;
    }

    public Utente getUtenteLoggato() {
        return utenteLoggato;
    }

    public void showPanel(String nomePanel) {
        switch (nomePanel) {
            case "gruppi" -> mainFrame.setContentPanel(new boundary.GruppiPanel(this));
            case "inviti" -> mainFrame.setContentPanel(placeholderInviti());
            default -> throw new IllegalArgumentException("Pannello sconosciuto: " + nomePanel);
        }
    }

    // TEMPORANEO: InvitiPanel non è ancora stato creato.
    private JPanel placeholderInviti() {
        JPanel placeholder = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Pannello: inviti", SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(20f));
        placeholder.add(label, BorderLayout.CENTER);
        return placeholder;
    }
}
