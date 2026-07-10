package dao;

import entity.ReportGruppo;
import entity.SaldoUtente;
import util.DBConnection;

import java.sql.*;
import java.util.List;

public class ReportDAO implements ReportDAOInterface {

    private final GruppoDAOInterface gruppoDAO = new GruppoDAO();

    @Override
    public ReportGruppo getReportByGruppo(int idGruppo) throws SQLException {

        int numeroSpese;
        double importoTotale;
        double importoComune;
        double importoPersonale;

        String sql = "SELECT " +
                "COUNT(*) FILTER (WHERE tipologia IN ('spesaComune','spesaIndividuale')) AS num_spese, " +
                "COALESCE(SUM(importototale) FILTER (WHERE tipologia IN ('spesaComune','spesaIndividuale')), 0) AS importo_totale, " +
                "COALESCE(SUM(importototale) FILTER (WHERE tipologia = 'spesaComune'), 0) AS importo_comune, " +
                "COALESCE(SUM(importototale) FILTER (WHERE tipologia = 'spesaIndividuale'), 0) AS importo_personale " +
                "FROM Spesa WHERE id_gruppo = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idGruppo);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                numeroSpese = rs.getInt("num_spese");
                importoTotale = rs.getDouble("importo_totale");
                importoComune = rs.getDouble("importo_comune");
                importoPersonale = rs.getDouble("importo_personale");
            }
        }

        List<SaldoUtente> saldi = gruppoDAO.getSaldiByGruppo(idGruppo);

        return new ReportGruppo(numeroSpese, importoTotale, importoComune, importoPersonale, saldi);
    }
}