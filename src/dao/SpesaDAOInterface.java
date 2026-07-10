package dao;

import entity.Spesa;

import java.sql.SQLException;
import java.util.List;

public interface SpesaDAOInterface {
    void inserisciSpesa(Spesa spesa) throws SQLException;
    void saldaDebito(String matricolaDebitore, String matricolaCreditore, int idGruppo, double importo) throws SQLException;
    List<Spesa> getSpeseByGruppo(int idGruppo) throws SQLException;
}