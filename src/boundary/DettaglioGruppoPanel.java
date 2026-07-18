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

        // FIX: il designer ha annidato la tabella dentro pannelloSaldi per errore.
        // Li separiamo qui via codice, senza dover intervenire sul file .form.
        pannelloSaldi.remove(TabellaSpese);          // toglie la scrollpane da dove non deve stare
        contenutoPanel.remove(pannelloSaldi);        // stacca temporaneamente pannelloSaldi
        contenutoPanel.setLayout(new BorderLayout());
        contenutoPanel.add(pannelloSaldi, BorderLayout.NORTH);   // i saldi vanno sopra
        contenutoPanel.add(TabellaSpese, BorderLayout.CENTER);   // la tabella occupa il resto
        contenutoPanel.revalidate();

        String descrizione = gruppo.getDescrizioneGruppo();
        if (descrizione != null && !descrizione.isBlank()) {
            labelNomeGruppo.setText("<html><b>" + gruppo.getNome() + "</b><br>"
                    + "<span style='font-size:11px;color:gray;'>" + descrizione + "</span></html>");
            headerPanel.setPreferredSize(new Dimension(headerPanel.getPreferredSize().width, 60));
        } else {
            labelNomeGruppo.setText(gruppo.getNome());
        }

        headerPanel.revalidate();
        headerPanel.repaint();
        revalidate();
        repaint();


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

        // Dettaglio bilaterale: solo per gli altri, non per la propria card
        String matricolaLoggato = mainController.getUtenteLoggato().getMatricola();
        if (!saldo.getMatricola().equals(matricolaLoggato)) {
            try {
                double ioDevo = gruppoController.getDebitoVerso(matricolaLoggato, saldo.getMatricola(), gruppo.getIdGruppo());
                double luiDeve = gruppoController.getDebitoVerso(saldo.getMatricola(), matricolaLoggato, gruppo.getIdGruppo());

                JLabel labelDettaglio;
                if (ioDevo > 0) {
                    labelDettaglio = new JLabel(String.format("Devi: %.2f €", ioDevo));
                    labelDettaglio.setForeground(Color.RED);
                } else if (luiDeve > 0) {
                    labelDettaglio = new JLabel(String.format("Ti deve: %.2f €", luiDeve));
                    labelDettaglio.setForeground(new Color(0, 150, 0));
                } else {
                    labelDettaglio = new JLabel("In pari");
                    labelDettaglio.setForeground(Color.GRAY);
                }
                labelDettaglio.setFont(labelDettaglio.getFont().deriveFont(11f));
                card.add(labelDettaglio);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

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
                    case saldo -> "Saldo";
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
        NuovaSpesaDialog dialog = new NuovaSpesaDialog(
                (JFrame) SwingUtilities.getWindowAncestor(this),
                gruppo,
                mainController.getUtenteLoggato().getMatricola());
        dialog.setVisible(true);

        if (dialog.isSpesaCreata()) {
            caricaDati(); // ricarica saldi e storico dopo il salvataggio
        }
    }

    private void onVediReport() {
        mainController.mostraReport(gruppo);
    }

    // Azzera saldo: chiede a chi (tra gli altri partecipanti) rimborsare e quanto
    private void onAzzeraSaldo() {
        try {
            List<SaldoUtente> saldi = gruppoController.getSaldi(gruppo.getIdGruppo());
            String matricolaUtente = mainController.getUtenteLoggato().getMatricola();

            List<SaldoUtente> altriPartecipanti = saldi.stream()
                    .filter(s -> !s.getMatricola().equals(matricolaUtente))
                    .toList();

            if (altriPartecipanti.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Non ci sono altri partecipanti nel gruppo");
                return;
            }

            JComboBox<SaldoUtente> comboAltro = new JComboBox<>(altriPartecipanti.toArray(new SaldoUtente[0]));
            JTextField campoImporto = new JTextField();

            JPanel form = new JPanel(new GridLayout(2, 2, 5, 5));
            form.add(new JLabel("Salda con:"));
            form.add(comboAltro);
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

            SaldoUtente altro = (SaldoUtente) comboAltro.getSelectedItem();

            // Guardiamo il debito SPECIFICO tra queste due persone, non il saldo aggregato
            double ioDevoALui = gruppoController.getDebitoVerso(matricolaUtente, altro.getMatricola(), gruppo.getIdGruppo());
            double luiDeveAMe = gruppoController.getDebitoVerso(altro.getMatricola(), matricolaUtente, gruppo.getIdGruppo());

            String matricolaDebitore;
            String matricolaCreditore;

            if (ioDevoALui > 0) {
                matricolaDebitore = matricolaUtente;
                matricolaCreditore = altro.getMatricola();
            } else if (luiDeveAMe > 0) {
                matricolaDebitore = altro.getMatricola();
                matricolaCreditore = matricolaUtente;
            } else {
                JOptionPane.showMessageDialog(this,
                        "Non c'è nessun debito da saldare con " + altro.getNomeUtente());
                return;
            }

            spesaController.saldaDebito(matricolaDebitore, matricolaCreditore, gruppo.getIdGruppo(), importo);
            caricaDati();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nella saldatura:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }


}