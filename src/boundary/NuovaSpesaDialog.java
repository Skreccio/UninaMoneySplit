package boundary;

import control.GruppoController;
import control.SpesaController;
import entity.Gruppo;
import entity.SaldoUtente;
import entity.Spesa;
import entity.TipoSpesa;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class NuovaSpesaDialog extends JDialog {
    private JPanel panel1;
    private JPanel formPanel;
    private JLabel labelDescrizione;
    private JLabel labelImporto;
    private JTextField campoImporto;
    private JTextField campoDescrizione;
    private JLabel labelTipoSpesa;
    private JComboBox comboTipo;
    private JLabel labelPagante;
    private JComboBox comboPagante;
    private JPanel pannelloBottoniSpesa;
    private JButton bottoneSalva;
    private JButton bottoneAnnulla;
    private JPanel pannelloQuota;

    private final GruppoController gruppoController = new GruppoController();
    private final SpesaController spesaController = new SpesaController();

    private final Gruppo gruppo;
    private final String matricolaUtenteLoggato;
    private boolean spesaCreata = false;

    // Etichette leggibili mostrate all'utente, mappate poi sull'enum vero
    private static final String LABEL_COMUNE = "Comune";
    private static final String LABEL_PERSONALE = "Personale";

    public NuovaSpesaDialog(JFrame parent, Gruppo gruppo, String matricolaUtenteLoggato) {
        super(parent, "Nuova spesa", true);
        this.gruppo = gruppo;
        this.matricolaUtenteLoggato = matricolaUtenteLoggato;

        setContentPane(panel1);
        setSize(450, 400);
        setLocationRelativeTo(parent);

        comboTipo.addItem(LABEL_COMUNE);
        comboTipo.addItem(LABEL_PERSONALE);

        caricaPartecipanti();

        // Aggiorna l'anteprima ogni volta che cambia tipo o importo
        comboTipo.addItemListener(e -> aggiornaAnteprimaQuota());
        campoImporto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                aggiornaAnteprimaQuota();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                aggiornaAnteprimaQuota();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                aggiornaAnteprimaQuota();
            }
        });

        bottoneAnnulla.addActionListener(e -> dispose());
        bottoneSalva.addActionListener(e -> onSalva());

        aggiornaAnteprimaQuota(); // stato iniziale
    }

    // Riempie la combo "Pagante" con i partecipanti del gruppo, preselezionando l'utente loggato
    private void caricaPartecipanti() {
        try {
            List<SaldoUtente> saldi = gruppoController.getSaldi(gruppo.getIdGruppo());

            for (SaldoUtente utente : saldi) {
                comboPagante.addItem(utente);
                if (utente.getMatricola().equals(matricolaUtenteLoggato)) {
                    comboPagante.setSelectedItem(utente);
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nel caricamento dei partecipanti");
            ex.printStackTrace();
        }
    }

    // Mostra a quanto ammonta la quota a testa se la spesa è comune; nasconde tutto se è personale
    private void aggiornaAnteprimaQuota() {
        pannelloQuota.removeAll();

        String tipoSelezionato = (String) comboTipo.getSelectedItem();

        if (LABEL_COMUNE.equals(tipoSelezionato)) {
            double importo = leggiImportoSicuro();
            int numPartecipanti = gruppo.getNumPartecipanti();

            double quota = spesaController.calcolaRipartizioneEqua(importo, numPartecipanti);

            JLabel labelQuota = new JLabel(String.format(
                    "Quota a testa (%d partecipanti): %.2f €", numPartecipanti, quota));
            pannelloQuota.add(labelQuota);
        } else {
            JLabel labelInfo = new JLabel("Spesa a carico esclusivo del pagante");
            pannelloQuota.add(labelInfo);
        }

        pannelloQuota.revalidate();
        pannelloQuota.repaint();
    }

    // Legge l'importo dal campo senza mai lanciare eccezioni: usato solo per l'anteprima live
    private double leggiImportoSicuro() {
        try {
            return Double.parseDouble(campoImporto.getText().trim().replace(",", "."));
        } catch (NumberFormatException ex) {
            return 0.0;
        }
    }

    private boolean validaInput() {
        return !campoDescrizione.getText().trim().isEmpty()
                && leggiImportoSicuro() > 0
                && comboPagante.getSelectedItem() != null;
    }

    private void onSalva() {
        if (!validaInput()) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci una descrizione e un importo valido (maggiore di zero)",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String descrizione = campoDescrizione.getText().trim();
        double importo = leggiImportoSicuro();
        SaldoUtente pagante = (SaldoUtente) comboPagante.getSelectedItem();
        String tipoSelezionato = (String) comboTipo.getSelectedItem();

        TipoSpesa tipologia = LABEL_COMUNE.equals(tipoSelezionato)
                ? TipoSpesa.spesaComune
                : TipoSpesa.spesaIndividuale;

        // idSpesa e dataSpesa non servono in inserimento: li assegna il database
        Spesa nuovaSpesa = new Spesa(0, tipologia, descrizione, importo,
                null, pagante.getMatricola(), gruppo.getIdGruppo());

        try {
            spesaController.aggiungiSpesa(nuovaSpesa);
            spesaCreata = true;
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nella registrazione della spesa:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSpesaCreata() {
        return spesaCreata;
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
        formPanel = new JPanel();
        formPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(formPanel, BorderLayout.NORTH);
        final JLabel label1 = new JLabel();
        label1.setText("Descrizione");
        formPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Importo");
        formPanel.add(label2, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoImporto = new JTextField();
        formPanel.add(campoImporto, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        campoDescrizione = new JTextField();
        formPanel.add(campoDescrizione, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("Tipo spesa");
        formPanel.add(label3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboTipo = new JComboBox();
        formPanel.add(comboTipo, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Pagante");
        formPanel.add(label4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        comboPagante = new JComboBox();
        formPanel.add(comboPagante, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        pannelloBottoniSpesa = new JPanel();
        pannelloBottoniSpesa.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel1.add(pannelloBottoniSpesa, BorderLayout.SOUTH);
        bottoneSalva = new JButton();
        bottoneSalva.setText("Salva");
        pannelloBottoniSpesa.add(bottoneSalva);
        bottoneAnnulla = new JButton();
        bottoneAnnulla.setText("Annulla");
        pannelloBottoniSpesa.add(bottoneAnnulla);
        pannelloQuota = new JPanel();
        pannelloQuota.setLayout(new BorderLayout(0, 0));
        panel1.add(pannelloQuota, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}