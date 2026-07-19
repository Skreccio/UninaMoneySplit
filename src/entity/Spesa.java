package entity;

import java.time.LocalDate;

public class Spesa {
    private int idSpesa;
    private TipoSpesa tipologia;
    private String descrizioneSpesa;
    private double importoTotale;
    private LocalDate dataSpesa;
    private String matricolaPagante;
    private int idGruppo;
    private String nomePagante;
    private String cognomePagante;

    public Spesa(int idSpesa, TipoSpesa tipologia, String descrizioneSpesa, double importoTotale,
                 LocalDate dataSpesa, String matricolaPagante, int idGruppo) {
        this.idSpesa = idSpesa;
        this.tipologia = tipologia;
        this.descrizioneSpesa = descrizioneSpesa;
        this.importoTotale = importoTotale;
        this.dataSpesa = dataSpesa;
        this.matricolaPagante = matricolaPagante;
        this.idGruppo = idGruppo;
    }

    public int getIdSpesa() { return idSpesa; }
    public TipoSpesa getTipologia() { return tipologia; }
    public String getDescrizioneSpesa() { return descrizioneSpesa; }
    public double getImportoTotale() { return importoTotale; }
    public LocalDate getDataSpesa() { return dataSpesa; }
    public String getMatricolaPagante() { return matricolaPagante; }
    public int getIdGruppo() { return idGruppo; }

    public String getNomePagante() { return nomePagante; }
    public void setNomePagante(String nomePagante) { this.nomePagante = nomePagante; }

    public String getCognomePagante() { return cognomePagante; }
    public void setCognomePagante(String cognomePagante) { this.cognomePagante = cognomePagante; }
}
