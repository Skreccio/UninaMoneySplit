package dao;

import entity.Gruppo;
import entity.SaldoUtente;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GruppoDAO implements GruppoDAOInterface {

    @Override
    public int inserisciGruppo(Gruppo gruppo) throws SQLException {
        String call = "{call crea_gruppo(?, ?, ?)}";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(call)) {

            cs.setString(1, gruppo.getNome());
            cs.setString(2, gruppo.getDescrizioneGruppo());
            cs.setString(3, gruppo.getMatricolaCreatore());
            cs.execute();
        }

        String sql = "SELECT id_gruppo FROM Gruppo WHERE matricola_creatore = ? " +
                "ORDER BY id_gruppo DESC LIMIT 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, gruppo.getMatricolaCreatore());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_gruppo");
                }
                throw new SQLException("Creazione gruppo fallita: nessun ID recuperato");
            }
        }
    }

    @Override
    public List<Gruppo> getGruppiByUtente(String matricola) throws SQLException {
        String sql = "SELECT g.id_gruppo, g.nome, g.descrizionegruppo, g.matricola_creatore " +
                "FROM Gruppo g " +
                "JOIN Partecipazione p ON p.id_gruppo = g.id_gruppo " +
                "WHERE p.matricola = ? " +
                "ORDER BY g.nome";

        List<Gruppo> gruppi = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricola);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Gruppo g = new Gruppo(
                            rs.getInt("id_gruppo"),
                            rs.getString("nome"),
                            rs.getString("descrizionegruppo"),
                            rs.getString("matricola_creatore")
                    );
                    g.setNumPartecipanti(calcolaNumPartecipanti(conn, g.getIdGruppo()));
                    gruppi.add(g);
                }
            }
        }
        return gruppi;
    }

    private int calcolaNumPartecipanti(Connection conn, int idGruppo) throws SQLException {
        String sql = "SELECT calcola_num_partecipanti(?) AS num"; // <-- aggiunto underscore
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idGruppo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("num");
            }
        }
    }

    @Override
    public List<SaldoUtente> getSaldiByGruppo(int idGruppo) throws SQLException {
        String sql = "SELECT p.matricola, u.nome, u.cognome " +
                "FROM Partecipazione p " +
                "JOIN Utente u ON u.matricola = p.matricola " +
                "WHERE p.id_gruppo = ? " +
                "ORDER BY p.datapartecipazione ASC";

        List<SaldoUtente> saldi = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String matricola = rs.getString("matricola");
                    String nomeCompleto = rs.getString("nome") + " " + rs.getString("cognome");

                    double credito = calcolaValore(conn, "calcola_credito", matricola, idGruppo);
                    double debito = calcolaValore(conn, "calcola_debito", matricola, idGruppo);
                    double saldo = calcolaValore(conn, "calcola_saldo", matricola, idGruppo);

                    saldi.add(new SaldoUtente(matricola, nomeCompleto, credito, debito, saldo));
                }
            }
        }
        return saldi;
    }

    private double calcolaValore(Connection conn, String nomeFunzione,
                                 String matricola, int idGruppo) throws SQLException {
        String sql = "SELECT " + nomeFunzione + "(?, ?) AS valore";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, matricola);
            ps.setInt(2, idGruppo);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble("valore");
            }
        }
    }
}