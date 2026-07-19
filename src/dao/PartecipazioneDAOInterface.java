package dao;

import java.sql.SQLException;

public interface PartecipazioneDAOInterface {
    void inserisciPartecipazione(String matricola, int idGruppo, boolean ruolo) throws SQLException;
    double getSaldo(String matricola, int idGruppo) throws SQLException;
    boolean isPartecipante(String matricola, int idGruppo) throws SQLException;
    void eliminaPartecipazione(String matricola, int idGruppo) throws SQLException;
    double getDebitoVerso(String matricolaDebitore, String matricolaCreditore, int idGruppo) throws SQLException;
}