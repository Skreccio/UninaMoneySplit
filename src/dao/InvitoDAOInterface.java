package dao;

import entity.Invito;
import entity.StatoInvito;

import java.sql.SQLException;
import java.util.List;

public interface InvitoDAOInterface {
    List<Invito> getInvitiInviati(String matricolaMittente) throws SQLException;
    void inserisciInvito(String matricolaMittente, String matricolaDestinatario, int idGruppo) throws SQLException;
    List<Invito> getInvitiByDestinatario(String matricolaDestinatario) throws SQLException;
    void aggiornaStato(int idInvito, StatoInvito nuovoStato) throws SQLException;
    List<Invito> getStoricoRicevuti(String matricolaDestinatario) throws SQLException;
}