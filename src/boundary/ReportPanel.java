package boundary;

import control.MainController;
import control.ReportController;
import entity.Gruppo;
import entity.ReportGruppo;
import entity.SaldoUtente;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class ReportPanel extends JPanel {

    private JPanel panel1;
    private JPanel headerPanel;
    private JLabel labelTitolo;
    private JPanel contenutoPanel;
    private JPanel panelStatistiche;
    private JPanel panelGrafico;
    private JPanel panelSaldi;
    private JButton bottoneIndietro;

    private final MainController mainController;
    private final Gruppo gruppo;
    private final ReportController reportController = new ReportController();


    public ReportPanel(MainController mainController, Gruppo gruppo) {
        this.mainController = mainController;
        this.gruppo = gruppo;

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);

        labelTitolo.setText("Report · " + gruppo.getNome());

        panelStatistiche.setLayout(new BoxLayout(panelStatistiche, BoxLayout.X_AXIS));
        panelSaldi.setLayout(new BoxLayout(panelSaldi, BoxLayout.Y_AXIS));
        panelGrafico.setLayout(new BorderLayout());

        bottoneIndietro.addActionListener(e -> mainController.mostraDettaglioGruppo(gruppo));

        caricaReport();
    }

    private void caricaReport() {
        try {
            ReportGruppo report = reportController.getReportGruppo(gruppo.getIdGruppo());

            costruisciStatistiche(report);
            costruisciGrafico(report);
            costruisciSaldi(report);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nel caricamento del report:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void costruisciStatistiche(ReportGruppo report) {
        panelStatistiche.removeAll();

        panelStatistiche.add(creaCardStatistica("Spese registrate", String.valueOf(report.getNumeroSpese())));
        panelStatistiche.add(Box.createHorizontalStrut(10));
        panelStatistiche.add(creaCardStatistica("Importo totale", String.format("%.2f €", report.getImportoTotale())));
        panelStatistiche.add(Box.createHorizontalStrut(10));
        panelStatistiche.add(creaCardStatistica("Partecipanti", String.valueOf(report.getSaldiUtenti().size())));

        panelStatistiche.revalidate();
        panelStatistiche.repaint();
    }

    private JPanel creaCardStatistica(String etichetta, String valore) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel labelEtichetta = new JLabel(etichetta);
        labelEtichetta.setForeground(Color.GRAY);
        labelEtichetta.setFont(labelEtichetta.getFont().deriveFont(11f));

        JLabel labelValore = new JLabel(valore);
        labelValore.setFont(labelValore.getFont().deriveFont(Font.BOLD, 18f));

        card.add(labelEtichetta);
        card.add(labelValore);

        return card;
    }

    private void costruisciGrafico(ReportGruppo report) {
        panelGrafico.removeAll();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(report.getImportoComune(), "Importo", "Comune");
        dataset.addValue(report.getImportoPersonale(), "Importo", "Personale");

        JFreeChart chart = ChartFactory.createBarChart(
                "Importo per tipologia di spesa", "Tipo", "Euro", dataset);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(300, 100));

        panelGrafico.add(chartPanel, BorderLayout.CENTER);
        panelGrafico.revalidate();
        panelGrafico.repaint();
    }

    private void costruisciSaldi(ReportGruppo report) {
        panelSaldi.removeAll();

        JLabel titolo = new JLabel("Saldo per utente (solo spese comuni)");
        titolo.setFont(titolo.getFont().deriveFont(Font.BOLD, 13f));
        titolo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panelSaldi.add(titolo);
        panelSaldi.add(Box.createVerticalStrut(8));

        for (SaldoUtente saldo : report.getSaldiUtenti()) {
            JPanel riga = new JPanel(new BorderLayout());
            riga.setAlignmentX(Component.LEFT_ALIGNMENT);
            riga.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

            JLabel nome = new JLabel(saldo.getNomeUtente());
            JLabel valore = new JLabel(String.format("%+.2f €", saldo.getSaldo()));
            valore.setForeground(saldo.getSaldo() >= 0 ? new Color(0, 150, 0) : Color.RED);
            valore.setFont(valore.getFont().deriveFont(Font.BOLD));

            riga.add(nome, BorderLayout.WEST);
            riga.add(valore, BorderLayout.EAST);

            panelSaldi.add(riga);
        }

        panelSaldi.revalidate();
        panelSaldi.repaint();
    }
}