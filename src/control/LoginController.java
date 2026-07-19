package control;

import dao.UtenteDAO;
import dao.UtenteDAOInterface;
import entity.Utente;

import java.sql.SQLException;

public class LoginController {

    private final UtenteDAOInterface utenteDAO = new UtenteDAO();

    public Utente autentica(String email, String password) throws SQLException {
        return utenteDAO.login(email, password);
    }
    public void registra(String matricola, String nome, String cognome, String email, String password) throws SQLException {
        utenteDAO.registraUtente(matricola, nome, cognome, email, password);
    }
}
