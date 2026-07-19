package dao;

import entity.ReportGruppo;

import java.sql.SQLException;

public interface ReportDAOInterface {
    ReportGruppo getReportByGruppo(int idGruppo) throws SQLException;
}
