package dao;

import entity.Spesa;
import entity.TipoSpesa;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SpesaDAO implements SpesaDAOInterface {

    @Override
    public void inserisciSpesa(Spesa spesa) throws SQLException {
        String nomeProcedura = switch (spesa.getTipologia()) {
            case spesaComune -> "registra_spesa_comune";
            case spesaIndividuale -> "registra_spesa_individuale";
            case saldo -> throw new IllegalArgumentException(
                    "Le saldature si registrano con saldaDebito(), non inserisciSpesa()");
        };

        String sql = "CALL " + nomeProcedura + "(?::text, ?::numeric, ?::char(9), ?::integer)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, spesa.getDescrizioneSpesa());
            ps.setDouble(2, spesa.getImportoTotale());
            ps.setString(3, spesa.getMatricolaPagante());
            ps.setInt(4, spesa.getIdGruppo());
            ps.execute();
        }
    }

    @Override
    public void saldaDebito(String matricolaDebitore, String matricolaCreditore,
                            int idGruppo, double importo) throws SQLException {

        String sql = "CALL salda_debito(?, ?, ?, ?::numeric)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, matricolaDebitore);
            ps.setString(2, matricolaCreditore);
            ps.setInt(3, idGruppo);
            ps.setDouble(4, importo);
            ps.execute();
        }
    }

    @Override
    public List<Spesa> getSpeseByGruppo(int idGruppo) throws SQLException {
        String sql = "SELECT * FROM storico_spese_gruppo(?)";

        List<Spesa> spese = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Spesa s = new Spesa(
                            rs.getInt("id_spesa"),
                            TipoSpesa.valueOf(rs.getString("tipologia")),
                            rs.getString("descrizionespesa"),
                            rs.getDouble("importototale"),
                            rs.getDate("dataspesa").toLocalDate(),
                            null,
                            idGruppo
                    );
                    s.setNomePagante(rs.getString("nome_pagante"));
                    s.setCognomePagante(rs.getString("cognome_pagante"));
                    spese.add(s);
                }
            }
        }
        return spese;
    }
}
