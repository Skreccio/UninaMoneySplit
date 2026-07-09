package entity;

import java.time.LocalDate;

public class Partecipazione {
    private String matricola;
    private int idGruppo;
    private boolean ruolo;
    private LocalDate dataPartecipazione;

    public Partecipazione(String matricola, int idGruppo, boolean ruolo, LocalDate dataPartecipazione) {
        this.matricola = matricola;
        this.idGruppo = idGruppo;
        this.ruolo = ruolo;
        this.dataPartecipazione = dataPartecipazione;
    }

    public String getMatricola() { return matricola; }
    public int getIdGruppo() { return idGruppo; }
    public boolean isRuolo() { return ruolo; }
    public LocalDate getDataPartecipazione() { return dataPartecipazione; }
}