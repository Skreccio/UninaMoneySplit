package control;

import dao.SpesaDAO;
import dao.SpesaDAOInterface;
import entity.Spesa;

import java.sql.SQLException;
import java.util.List;

public class SpesaController {

    private final SpesaDAOInterface spesaDAO = new SpesaDAO();

    public void aggiungiSpesa(Spesa spesa) throws SQLException {
        spesaDAO.inserisciSpesa(spesa);
    }

    public void saldaDebito(String matricolaDebitore, String matricolaCreditore,
                            int idGruppo, double importo) throws SQLException {
        spesaDAO.saldaDebito(matricolaDebitore, matricolaCreditore, idGruppo, importo);
    }

    public List<Spesa> getStoricoSpese(int idGruppo) throws SQLException {
        return spesaDAO.getSpeseByGruppo(idGruppo);
    }

    public double calcolaRipartizioneEqua(double importoTotale, int numPartecipanti) {
        if (numPartecipanti <= 0) {
            return 0.0;
        }
        return Math.round((importoTotale / numPartecipanti) * 100.0) / 100.0;
    }
}
