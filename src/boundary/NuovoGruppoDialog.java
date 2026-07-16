package boundary;

import control.GruppoController;
import control.InvitoController;
import entity.Gruppo;
import entity.Utente;

import javax.swing.*;
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
        listaUtenti.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) {
                    onAggiungiSelezionati();
                }
            }
        });

        // Doppio click su un utente già selezionato: lo rimuove
        listaSelezionati.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
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
}