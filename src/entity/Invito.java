package entity;

import java.time.LocalDate;

public class Invito {
    private int idInvito;
    private StatoInvito stato;
    private LocalDate dataInvito;
    private String matricolaMittente;
    private String matricolaDestinatario;
    private int idGruppo;

    private String nomeMittente;
    private String cognomeMittente;
    private String nomeGruppo;

    public Invito(int idInvito, StatoInvito stato, LocalDate dataInvito,
                  String matricolaMittente, String matricolaDestinatario, int idGruppo) {
        this.idInvito = idInvito;
        this.stato = stato;
        this.dataInvito = dataInvito;
        this.matricolaMittente = matricolaMittente;
        this.matricolaDestinatario = matricolaDestinatario;
        this.idGruppo = idGruppo;
    }

    public int getIdInvito() { return idInvito; }
    public StatoInvito getStato() { return stato; }
    public LocalDate getDataInvito() { return dataInvito; }
    public String getMatricolaMittente() { return matricolaMittente; }
    public String getMatricolaDestinatario() { return matricolaDestinatario; }
    public int getIdGruppo() { return idGruppo; }

    public String getNomeMittente() { return nomeMittente; }
    public void setNomeMittente(String nomeMittente) { this.nomeMittente = nomeMittente; }

    public String getCognomeMittente() { return cognomeMittente; }
    public void setCognomeMittente(String cognomeMittente) { this.cognomeMittente = cognomeMittente; }

    public String getNomeGruppo() { return nomeGruppo; }
    public void setNomeGruppo(String nomeGruppo) { this.nomeGruppo = nomeGruppo; }
}