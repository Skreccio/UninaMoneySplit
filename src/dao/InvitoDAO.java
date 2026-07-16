package dao;

import entity.Invito;
import entity.StatoInvito;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InvitoDAO implements InvitoDAOInterface {

    @Override
    public void inserisciInvito(String matricolaMittente, String matricolaDestinatario, int idGruppo)
            throws SQLException {

        String sql = "CALL invia_invito(?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaMittente);
            ps.setString(2, matricolaDestinatario);
            ps.setInt(3, idGruppo);
            ps.execute();
        }
    }

    @Override
    public List<Invito> getInvitiByDestinatario(String matricolaDestinatario) throws SQLException {
        String sql = "SELECT i.id_invito, i.stato, i.datainvito, i.matricola_mittente, " +
                "i.matricola_destinatario, i.id_gruppo, " +
                "u.nome AS nome_mittente, u.cognome AS cognome_mittente, g.nome AS nome_gruppo " +
                "FROM Invito i " +
                "JOIN Utente u ON u.matricola = i.matricola_mittente " +
                "JOIN Gruppo g ON g.id_gruppo = i.id_gruppo " +
                "WHERE i.matricola_destinatario = ? AND i.stato = 'inAttesa' " +
                "ORDER BY i.datainvito DESC";

        List<Invito> inviti = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaDestinatario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invito invito = new Invito(
                            rs.getInt("id_invito"),
                            StatoInvito.valueOf(rs.getString("stato")),
                            rs.getDate("datainvito").toLocalDate(),
                            rs.getString("matricola_mittente"),
                            rs.getString("matricola_destinatario"),
                            rs.getInt("id_gruppo")
                    );
                    invito.setNomeMittente(rs.getString("nome_mittente"));
                    invito.setCognomeMittente(rs.getString("cognome_mittente"));
                    invito.setNomeGruppo(rs.getString("nome_gruppo"));
                    inviti.add(invito);
                }
            }
        }
        return inviti;
    }

    @Override
    public void aggiornaStato(int idInvito, StatoInvito nuovoStato) throws SQLException {
        if (nuovoStato == StatoInvito.rifiutato) {
            String sql = "CALL rifiuta_invito(?)";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idInvito);
                ps.execute();
            }
        } else {
            String sql = "UPDATE Invito SET stato = ?::statoInvito WHERE id_invito = ?"; // <-- cast esplicito
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nuovoStato.name());
                ps.setInt(2, idInvito);
                ps.executeUpdate();
            }
        }
    }
    @Override
    public List<Invito> getInvitiInviati(String matricolaMittente) throws SQLException {
        String sql = "SELECT * FROM lista_inviti_inviati(?)";

        List<Invito> inviti = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaMittente);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invito invito = new Invito(
                            rs.getInt("id_invito"),
                            StatoInvito.valueOf(rs.getString("stato")),
                            rs.getDate("datainvito").toLocalDate(),
                            matricolaMittente,
                            null, // la funzione non restituisce la matricola del destinatario, solo nome/cognome
                            0     // idem per id_gruppo: non serve per questa vista di sola lettura
                    );
                    invito.setNomeDestinatario(rs.getString("nome_destinatario"));
                    invito.setCognomeDestinatario(rs.getString("cognome_destinatario"));
                    invito.setNomeGruppo(rs.getString("nome_gruppo"));
                    inviti.add(invito);
                }
            }
        }
        return inviti;
    }
    @Override
    public List<Invito> getStoricoRicevuti(String matricolaDestinatario) throws SQLException {
        String sql = "SELECT i.id_invito, i.stato, i.datainvito, i.matricola_mittente, " +
                "i.matricola_destinatario, i.id_gruppo, " +
                "u.nome AS nome_mittente, u.cognome AS cognome_mittente, g.nome AS nome_gruppo " +
                "FROM Invito i " +
                "JOIN Utente u ON u.matricola = i.matricola_mittente " +
                "JOIN Gruppo g ON g.id_gruppo = i.id_gruppo " +
                "WHERE i.matricola_destinatario = ? " +
                "ORDER BY i.datainvito DESC";

        List<Invito> inviti = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaDestinatario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Invito invito = new Invito(
                            rs.getInt("id_invito"),
                            StatoInvito.valueOf(rs.getString("stato")),
                            rs.getDate("datainvito").toLocalDate(),
                            rs.getString("matricola_mittente"),
                            rs.getString("matricola_destinatario"),
                            rs.getInt("id_gruppo")
                    );
                    invito.setNomeMittente(rs.getString("nome_mittente"));
                    invito.setCognomeMittente(rs.getString("cognome_mittente"));
                    invito.setNomeGruppo(rs.getString("nome_gruppo"));
                    inviti.add(invito);
                }
            }
        }
        return inviti;
    }
}