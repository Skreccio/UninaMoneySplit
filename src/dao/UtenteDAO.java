package dao;

import entity.Utente;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtenteDAO implements UtenteDAOInterface {

    @Override
    public Utente login(String email, String password) throws SQLException {
        String sql = "SELECT matricola, nome, cognome, email FROM Utente " +
                "WHERE email = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Utente(
                            rs.getString("matricola"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email")
                    );
                }
                return null;
            }
        }
    }

    @Override
    public List<Utente> cercaUtenti(String testo) throws SQLException {
        String sql = "SELECT matricola, nome, cognome, email FROM Utente " +
                "WHERE nome ILIKE ? OR cognome ILIKE ? OR matricola ILIKE ?";

        List<Utente> risultati = new ArrayList<>();
        String pattern = "%" + testo + "%";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    risultati.add(new Utente(
                            rs.getString("matricola"),
                            rs.getString("nome"),
                            rs.getString("cognome"),
                            rs.getString("email")
                    ));
                }
            }
        }
        return risultati;
    }
}