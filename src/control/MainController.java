package control;

import boundary.MainFrame;
import boundary.InvitiPanel;
import boundary.GruppiPanel;
import entity.Utente;

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
            case "gruppi" -> mainFrame.setContentPanel(new GruppiPanel(this));
            case "inviti" -> mainFrame.setContentPanel(new InvitiPanel(this));
            default -> throw new IllegalArgumentException("Pannello sconosciuto: " + nomePanel);
        }
    }
}