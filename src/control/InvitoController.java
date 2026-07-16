package control;

import dao.InvitoDAO;
import dao.InvitoDAOInterface;
import dao.UtenteDAO;
import dao.UtenteDAOInterface;
import entity.Invito;
import entity.StatoInvito;
import entity.Utente;

import java.sql.SQLException;
import java.util.List;

public class InvitoController {

    private final InvitoDAOInterface invitoDAO = new InvitoDAO();
    private final UtenteDAOInterface utenteDAO = new UtenteDAO();

    public void inviaInvito(String matricolaMittente, String matricolaDestinatario, int idGruppo)
            throws SQLException {
        invitoDAO.inserisciInvito(matricolaMittente, matricolaDestinatario, idGruppo);
    }

    public List<Invito> getInvitiRicevuti(String matricolaDestinatario) throws SQLException {
        return invitoDAO.getInvitiByDestinatario(matricolaDestinatario);
    }

    public void accettaInvito(int idInvito) throws SQLException {
        invitoDAO.aggiornaStato(idInvito, StatoInvito.accettato);
        // Nota: la creazione della Partecipazione è delegata al trigger trg_accetta_invito
    }

    public void rifiutaInvito(int idInvito) throws SQLException {
        invitoDAO.aggiornaStato(idInvito, StatoInvito.rifiutato);
    }

    public List<Utente> cercaUtenti(String testo) throws SQLException {
        return utenteDAO.cercaUtenti(testo);
    }
    public List<Invito> getInvitiInviati(String matricola) throws SQLException {
        return invitoDAO.getInvitiInviati(matricola);
    }
    public List<Invito> getStoricoRicevuti(String matricola) throws SQLException {
        return invitoDAO.getStoricoRicevuti(matricola);
    }
}