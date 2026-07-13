package control;

import boundary.GruppiPanel;
import boundary.MainFrame;
import boundary.PannelloSegnaposto;
import entity.Gruppo;
import entity.Utente;

import java.sql.SQLException;

/**
 * Il NAVIGATORE dell'applicazione.
 *
 * E' l'unico Control con uno stato: si ricorda chi e' loggato e qual e' l'ultimo
 * gruppo aperto. Il suo lavoro e' decidere QUALE pannello mostrare; non costruisce
 * componenti grafici (per questo non importa piu' javax.swing).
 */
public class MainController {

    private final MainFrame mainFrame;
    private final Utente utenteLoggato;

    // Serve solo per contare gli inviti pendenti e aggiornare il badge.
    private final InvitoController invitoController = new InvitoController();

    // L'ultimo gruppo aperto. Serve alla voce "Report" del menu, che e' relativa a un gruppo.
    private Gruppo gruppoCorrente;

    public MainController(MainFrame mainFrame, Utente utenteLoggato) {
        this.mainFrame = mainFrame;
        this.utenteLoggato = utenteLoggato;
    }

    public Utente getUtenteLoggato() {
        return utenteLoggato;
    }

    public Gruppo getGruppoCorrente() {
        return gruppoCorrente;
    }

    // ============ NAVIGAZIONE ============
    // Un metodo per ogni destinazione. Ogni metodo porta con se' i parametri
    // che quella destinazione richiede: per questo non usiamo piu' showPanel(String).

    public void mostraGruppi() {
        mainFrame.evidenziaVoceMenu("gruppi");
        mainFrame.setContentPanel(new GruppiPanel(this));
    }

    public void mostraInviti() {
        mainFrame.evidenziaVoceMenu("inviti");
        mainFrame.setContentPanel(new PannelloSegnaposto("Inviti", "Lo costruiamo nello step 3."));
    }

    public void mostraDettaglioGruppo(Gruppo gruppo) {
        this.gruppoCorrente = gruppo;                     // me lo ricordo, servira' al Report
        mainFrame.evidenziaVoceMenu("gruppi");
        mainFrame.setContentPanel(new PannelloSegnaposto(
                gruppo.getNome(), "Il dettaglio del gruppo arriva nello step 4."));
    }

    /** Voce "Report" del menu: mostra il report dell'ultimo gruppo aperto. */
    public void mostraReport() {
        if (gruppoCorrente == null) {
            mainFrame.mostraErrore("Apri prima un gruppo: il report riguarda un singolo gruppo.");
            return;
        }
        mostraReport(gruppoCorrente);
    }

    /** Pulsante "Vedi report del gruppo" dentro il dettaglio. */
    public void mostraReport(Gruppo gruppo) {
        this.gruppoCorrente = gruppo;
        mainFrame.evidenziaVoceMenu("report");
        mainFrame.setContentPanel(new PannelloSegnaposto(
                "Report \u00B7 " + gruppo.getNome(), "Il grafico JFreeChart arriva nello step 6."));
    }

    // ============ SERVIZI TRASVERSALI ============

    /** Ricalcola quanti inviti pendenti ha l'utente e aggiorna il pallino sul menu. */
    public void aggiornaBadgeInviti() {
        try {
            int pendenti = invitoController.getInvitiRicevuti(utenteLoggato.getMatricola()).size();
            mainFrame.aggiornaBadgeInviti(pendenti);
        } catch (SQLException e) {
            mainFrame.aggiornaBadgeInviti(0);   // niente badge, ma non blocchiamo l'app
        }
    }
}