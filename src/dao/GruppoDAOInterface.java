package dao;

import entity.Gruppo;
import entity.SaldoUtente;

import java.sql.SQLException;
import java.util.List;

public interface GruppoDAOInterface {
    int inserisciGruppo(Gruppo gruppo) throws SQLException;
    List<Gruppo> getGruppiByUtente(String matricola) throws SQLException;
    List<SaldoUtente> getSaldiByGruppo(int idGruppo) throws SQLException;
}
