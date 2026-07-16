package boundary;

import control.GruppoController;
import control.InvitoController;
import entity.Gruppo;
import entity.Utente;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class NuovoGruppoDialog extends JDialog {

    private JLabel labelNome;
    private JTextField campoNome;
    private JLabel labelDescrizione;
    private JTextField campoDescrizione;
    private JLabel labelRicerca;
    private JTextField campoRicerca;
    private JList<Utente> listaUtenti;
    private JList<Utente> listaSelezionati;
    private JButton bottoneCrea;
    private JButton bottoneAnnulla;
    private JPanel panel1;

    private final GruppoController gruppoController = new GruppoController();
    private final InvitoController invitoController = new InvitoController();

    private final String matricolaCreatore;
    private boolean gruppoCreato = false;

    // Insieme persistente delle selezioni, sopravvive ai cambi di filtro nella ricerca.
    // LinkedHashSet mantiene l'ordine di inserimento ed evita duplicati automaticamente.
    private final Set<Utente> selezionati = new LinkedHashSet<>();

    public NuovoGruppoDialog(JFrame parent, String matricolaCreatore) {
        super(parent, "Nuovo gruppo", true);
        this.matricolaCreatore = matricolaCreatore;

        setContentPane(panel1);
        setSize(500, 650);
        setLocationRelativeTo(parent);

        listaUtenti.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        campoRicerca.addCaretListener(e -> onCercaUtenti());

        // Doppio click su un risultato di ricerca: lo aggiunge alla selezione
        listaUtenti.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onAggiungiSelezionati();
                }
            }
        });

        // Doppio click su un utente già selezionato: lo rimuove
        listaSelezionati.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onRimuoviSelezionato();
                }
            }
        });

        bottoneAnnulla.addActionListener(e -> dispose());
        bottoneCrea.addActionListener(e -> onCrea());
    }

    private void onCercaUtenti() {
        String testo = campoRicerca.getText().trim();

        if (testo.isEmpty()) {
            listaUtenti.setListData(new Utente[0]);
            return;
        }

        try {
            List<Utente> risultati = invitoController.cercaUtenti(testo);
            risultati.removeIf(u -> u.getMatricola().equals(matricolaCreatore));
            // Non ripropone chi è già stato selezionato, evita confusione visiva
            risultati.removeIf(selezionati::contains);

            listaUtenti.setListData(new Vector<>(risultati));

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Errore nella ricerca utenti", "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Sposta gli utenti evidenziati nei risultati di ricerca dentro l'insieme "selezionati"
    private void onAggiungiSelezionati() {
        List<Utente> daAggiungere = listaUtenti.getSelectedValuesList();
        selezionati.addAll(daAggiungere);
        aggiornaListaSelezionati();
        onCercaUtenti(); // rifiltra, cosi' chi e' stato appena selezionato sparisce dai risultati
    }

    private void onRimuoviSelezionato() {
        Utente daRimuovere = listaSelezionati.getSelectedValue();
        if (daRimuovere != null) {
            selezionati.remove(daRimuovere);
            aggiornaListaSelezionati();
        }
    }

    private void aggiornaListaSelezionati() {
        listaSelezionati.setListData(new Vector<>(selezionati));
    }

    private boolean validaInput() {
        return !campoNome.getText().trim().isEmpty();
    }

    private void onCrea() {
        if (!validaInput()) {
            JOptionPane.showMessageDialog(this, "Il nome del gruppo è obbligatorio", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nome = campoNome.getText().trim();
        String descrizione = campoDescrizione.getText().trim();

        try {
            Gruppo nuovoGruppo = gruppoController.creaGruppo(nome, descrizione, matricolaCreatore);

            for (Utente utente : selezionati) {
                invitoController.inviaInvito(matricolaCreatore, utente.getMatricola(), nuovoGruppo.getIdGruppo());
            }

            gruppoCreato = true;
            dispose();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore nella creazione del gruppo:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isGruppoCreato() {
        return gruppoCreato;
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
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(8, 4, new Insets(0, 0, 0, 0), -1, -1));
        final com.intellij.uiDesigner.core.Spacer spacer1 = new com.intellij.uiDesigner.core.Spacer();
        panel1.add(spacer1, new com.intellij.uiDesigner.core.GridConstraints(0, 3, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        labelDescrizione = new JLabel();
        labelDescrizione.setText("Descrizione");
        panel1.add(labelDescrizione, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        labelRicerca = new JLabel();
        labelRicerca.setText("Invita utenti registrati");
        panel1.add(labelRicerca, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoRicerca = new JTextField();
        panel1.add(campoRicerca, new com.intellij.uiDesigner.core.GridConstraints(3, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JScrollPane scrollPane1 = new JScrollPane();
        panel1.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(4, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listaUtenti = new JList();
        scrollPane1.setViewportView(listaUtenti);
        bottoneCrea = new JButton();
        bottoneCrea.setText("Crea e invita");
        panel1.add(bottoneCrea, new com.intellij.uiDesigner.core.GridConstraints(7, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bottoneAnnulla = new JButton();
        bottoneAnnulla.setText("Annulla");
        panel1.add(bottoneAnnulla, new com.intellij.uiDesigner.core.GridConstraints(7, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoDescrizione = new JTextField();
        panel1.add(campoDescrizione, new com.intellij.uiDesigner.core.GridConstraints(2, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        labelNome = new JLabel();
        labelNome.setText("Nome del gruppo");
        panel1.add(labelNome, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        campoNome = new JTextField();
        panel1.add(campoNome, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Utenti selezionati");
        panel1.add(label1, new com.intellij.uiDesigner.core.GridConstraints(5, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JScrollPane scrollPane2 = new JScrollPane();
        panel1.add(scrollPane2, new com.intellij.uiDesigner.core.GridConstraints(6, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        listaSelezionati = new JList();
        scrollPane2.setViewportView(listaSelezionati);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}