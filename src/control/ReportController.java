package control;

import dao.ReportDAO;
import dao.ReportDAOInterface;
import entity.ReportGruppo;

import java.sql.SQLException;

public class ReportController {

    private final ReportDAOInterface reportDAO = new ReportDAO();

    public ReportGruppo getReportGruppo(int idGruppo) throws SQLException {
        return reportDAO.getReportByGruppo(idGruppo);
    }
}
