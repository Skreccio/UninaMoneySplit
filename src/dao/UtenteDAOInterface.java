package dao;

import entity.Utente;

import java.sql.SQLException;
import java.util.List;

public interface UtenteDAOInterface {
    void registraUtente(String matricola, String nome, String cognome, String email, String password) throws SQLException;
    Utente login(String email, String password) throws SQLException;
    List<Utente> cercaUtenti(String testo) throws SQLException;
}
