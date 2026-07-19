package control;

import dao.GruppoDAO;
import dao.GruppoDAOInterface;
import dao.PartecipazioneDAO;
import dao.PartecipazioneDAOInterface;
import entity.Gruppo;
import entity.SaldoUtente;

import java.sql.SQLException;
import java.util.List;

public class GruppoController {

    private final GruppoDAOInterface gruppoDAO = new GruppoDAO();
    private final PartecipazioneDAOInterface partecipazioneDAO = new PartecipazioneDAO();

    public Gruppo creaGruppo(String nome, String descrizione, String matricolaCreatore) throws SQLException {
        Gruppo gruppo = new Gruppo(0, nome, descrizione, matricolaCreatore);
        int idGruppo = gruppoDAO.inserisciGruppo(gruppo);

        Gruppo gruppoCreato = new Gruppo(idGruppo, nome, descrizione, matricolaCreatore);
        gruppoCreato.setNumPartecipanti(1);
        return gruppoCreato;
    }

    public List<Gruppo> getGruppiUtente(String matricola) throws SQLException {
        return gruppoDAO.getGruppiByUtente(matricola);
    }

    public List<SaldoUtente> getSaldi(int idGruppo) throws SQLException {
        return gruppoDAO.getSaldiByGruppo(idGruppo);
    }

    public double getDebitoVerso(String matricolaDebitore, String matricolaCreditore, int idGruppo) throws SQLException {
        return partecipazioneDAO.getDebitoVerso(matricolaDebitore, matricolaCreditore, idGruppo);
    }
    public void esciDalGruppo(String matricola, int idGruppo) throws SQLException {
        partecipazioneDAO.eliminaPartecipazione(matricola, idGruppo);
    }
}
