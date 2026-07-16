package boundary;

import control.GruppoController;
import control.MainController;
import control.SpesaController;
import entity.Gruppo;
import entity.SaldoUtente;
import entity.Spesa;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class DettaglioGruppoPanel extends JPanel {
    private JPanel panel1;
    private JPanel headerPanel;
    private JLabel labelNomeGruppo;
    private JPanel pannelloBottoni;
    private JButton bottoneNuovaSpesa;
    private JButton bottoneAzzeraSaldo;
    private JButton bottoneReport;
    private JPanel contenutoPanel;
    private JPanel pannelloSaldi;
    private JTable tabellaSpese;
    private JScrollPane TabellaSpese;

    private final MainController mainController;
    private final Gruppo gruppo;
    private final GruppoController gruppoController = new GruppoController();
    private final SpesaController spesaController = new SpesaController();

    public DettaglioGruppoPanel(MainController mainController, Gruppo gruppo) {
        this.mainController = mainController;
        this.gruppo = gruppo;

        setLayout(new BorderLayout());
        add(panel1, BorderLayout.CENTER);

        labelNomeGruppo.setText(gruppo.getNome());

        pannelloSaldi.setLayout(new BoxLayout(pannelloSaldi, BoxLayout.X_AXIS));

        configuraTabella();

        bottoneNuovaSpesa.addActionListener(e -> onNuovaSpesa());
        bottoneAzzeraSaldo.addActionListener(e -> onAzzeraSaldo());
        bottoneReport.addActionListener(e -> onVediReport());

        caricaDati();
    }

    // Imposta le colonne della tabella e l'ordinamento per data decrescente
    private void configuraTabella() {
        DefaultTableModel model = new DefaultTableModel(
                new Object[]{"Data", "Descrizione", "Tipo", "Pagante", "Importo"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabella di sola lettura
            }
        };
        tabellaSpese.setModel(model);

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        tabellaSpese.setRowSorter(sorter);
        sorter.setSortKeys(List.of(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
    }

    public void caricaDati() {
        caricaSaldi();
        caricaSpese();
    }

    private void caricaSaldi() {
        pannelloSaldi.removeAll();

        try {
            List<SaldoUtente> saldi = gruppoController.getSaldi(gruppo.getIdGruppo());

            for (SaldoUtente saldo : saldi) {
                pannelloSaldi.add(creaSaldoCard(saldo));
                pannelloSaldi.add(Box.createHorizontalStrut(10));
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dei saldi");
            ex.printStackTrace();
        }

        pannelloSaldi.revalidate();
        pannelloSaldi.repaint();
    }

    private JPanel creaSaldoCard(SaldoUtente saldo) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        JLabel labelNome = new JLabel(saldo.getNomeUtente());
        labelNome.setFont(labelNome.getFont().deriveFont(Font.BOLD, 14f));

        JLabel labelSaldo = new JLabel(String.format("%.2f €", saldo.getSaldo()));
        labelSaldo.setForeground(saldo.getSaldo() >= 0 ? new Color(0, 150, 0) : Color.RED);

        card.add(labelNome);
        card.add(labelSaldo);

        return card;
    }

    private void caricaSpese() {
        DefaultTableModel model = (DefaultTableModel) tabellaSpese.getModel();
        model.setRowCount(0);

        try {
            List<Spesa> spese = spesaController.getStoricoSpese(gruppo.getIdGruppo());

            for (Spesa spesa : spese) {
                String tipo = switch (spesa.getTipologia()) {
                    case spesaComune -> "Comune";
                    case spesaIndividuale -> "Personale";
                    case saldo -> "Saldatura";
                };

                model.addRow(new Object[]{
                        spesa.getDataSpesa(),
                        spesa.getDescrizioneSpesa(),
                        tipo,
                        spesa.getNomePagante() + " " + spesa.getCognomePagante(),
                        String.format("%.2f €", spesa.getImportoTotale())
                });
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento delle spese");
            ex.printStackTrace();
        }
    }

    private void onNuovaSpesa() {
        JOptionPane.showMessageDialog(this,
                "Dialog Nuova spesa in arrivo",
                "In sviluppo", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onVediReport() {
        JOptionPane.showMessageDialog(this,
                "ReportPanel in arrivo",
                "In sviluppo", JOptionPane.INFORMATION_MESSAGE);
    }

    // Azzera saldo: chiede a chi (tra gli altri partecipanti) rimborsare e quanto
    private void onAzzeraSaldo() {
        try {
            List<SaldoUtente> saldi = gruppoController.getSaldi(gruppo.getIdGruppo());
            String matricolaUtente = mainController.getUtenteLoggato().getMatricola();

            // Non ha senso saldare un debito con se stessi: li togliamo dalla scelta
            List<SaldoUtente> altriPartecipanti = saldi.stream()
                    .filter(s -> !s.getMatricola().equals(matricolaUtente))
                    .toList();

            if (altriPartecipanti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non ci sono altri partecipanti nel gruppo");
                return;
            }

            JComboBox<SaldoUtente> comboCreditore = new JComboBox<>(altriPartecipanti.toArray(new SaldoUtente[0]));
            JTextField campoImporto = new JTextField();

            JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
            form.add(new JLabel("Salda con:"));
            form.add(comboCreditore);
            form.add(new JLabel("Importo:"));
            form.add(campoImporto);

            int scelta = JOptionPane.showConfirmDialog(this, form, "Azzera saldo",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (scelta != JOptionPane.OK_OPTION) {
                return;
            }

            double importo;
            try {
                importo = Double.parseDouble(campoImporto.getText().trim().replace(",", "."));
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Importo non valido", "Errore", JOptionPane.ERROR_MESSAGE);
                return;
            }

            SaldoUtente creditore = (SaldoUtente) comboCreditore.getSelectedItem();

            spesaController.saldaDebito(matricolaUtente, creditore.getMatricola(),
                    gruppo.getIdGruppo(), importo);

            caricaDati(); // ricarica saldi e storico spese dopo la saldatura

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nella saldatura:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout(0, 0));
        panel1.add(headerPanel, BorderLayout.NORTH);
        labelNomeGruppo = new JLabel();
        labelNomeGruppo.setText("Nome gruppo");
        headerPanel.add(labelNomeGruppo, BorderLayout.WEST);
        pannelloBottoni = new JPanel();
        pannelloBottoni.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        headerPanel.add(pannelloBottoni, BorderLayout.EAST);
        bottoneNuovaSpesa = new JButton();
        bottoneNuovaSpesa.setText("Nuova spesa");
        pannelloBottoni.add(bottoneNuovaSpesa);
        bottoneAzzeraSaldo = new JButton();
        bottoneAzzeraSaldo.setText("Azzera saldo");
        pannelloBottoni.add(bottoneAzzeraSaldo);
        bottoneReport = new JButton();
        bottoneReport.setText("Vedi report");
        pannelloBottoni.add(bottoneReport);
        contenutoPanel = new JPanel();
        contenutoPanel.setLayout(new BorderLayout(0, 0));
        panel1.add(contenutoPanel, BorderLayout.CENTER);
        pannelloSaldi = new JPanel();
        pannelloSaldi.setLayout(new BorderLayout(0, 0));
        contenutoPanel.add(pannelloSaldi, BorderLayout.CENTER);
        TabellaSpese = new JScrollPane();
        pannelloSaldi.add(TabellaSpese, BorderLayout.CENTER);
        tabellaSpese = new JTable();
        TabellaSpese.setViewportView(tabellaSpese);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}