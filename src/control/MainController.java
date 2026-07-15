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

    // TEMPORANEO: GruppiPanel e InvitiPanel non sono ancora stati creati.

    public void showPanel(String nomePanel) {
        if (nomePanel.equals("gruppi")) {
            boundary.GruppiPanel gruppiPanel = new boundary.GruppiPanel();
            gruppiPanel.caricaGruppi(utenteLoggato, this);
            mainFrame.setContentPanel(gruppiPanel.getRootPanel());
            return;
        }

        JPanel placeholder = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Pannello: " + nomePanel, SwingConstants.CENTER);
        label.setFont(label.getFont().deriveFont(20f));
        placeholder.add(label, BorderLayout.CENTER);

        mainFrame.setContentPanel(placeholder);
     }
    }
