package dao;

import util.DBConnection;

import java.sql.*;

public class PartecipazioneDAO implements PartecipazioneDAOInterface {

    @Override
    public void inserisciPartecipazione(String matricola, int idGruppo, boolean ruolo) throws SQLException {
        String sql = "INSERT INTO Partecipazione (ruolo, matricola, id_gruppo) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setBoolean(1, ruolo);
            ps.setString(2, matricola);
            ps.setInt(3, idGruppo);
            ps.executeUpdate();
        }
    }

    @Override
    public double getSaldo(String matricola, int idGruppo) throws SQLException {
        String sql = "SELECT calcola_saldo(?, ?) AS saldo";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);
            ps.setInt(2, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble("saldo");
            }
        }
    }

    @Override
    public boolean isPartecipante(String matricola, int idGruppo) throws SQLException {
        String sql = "SELECT 1 FROM Partecipazione WHERE matricola = ? AND id_gruppo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);
            ps.setInt(2, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}