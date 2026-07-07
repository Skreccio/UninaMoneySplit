package control;

import dao.UtenteDAO;
import entity.Utente;

import java.sql.SQLException;

public class LoginController {

    private final UtenteDAO utenteDAO = new UtenteDAO();

    public Utente autentica(String email, String password) throws SQLException {
        return utenteDAO.login(email, password);
    }
}