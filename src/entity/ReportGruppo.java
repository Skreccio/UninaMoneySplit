package entity;

import java.util.List;

public class ReportGruppo {
    private int numeroSpese;
    private double importoTotale;
    private double importoComune;
    private double importoPersonale;
    private List<SaldoUtente> saldiUtenti;

    public ReportGruppo(int numeroSpese, double importoTotale, double importoComune,
                        double importoPersonale, List<SaldoUtente> saldiUtenti) {
        this.numeroSpese = numeroSpese;
        this.importoTotale = importoTotale;
        this.importoComune = importoComune;
        this.importoPersonale = importoPersonale;
        this.saldiUtenti = saldiUtenti;
    }

    public int getNumeroSpese() { return numeroSpese; }
    public double getImportoTotale() { return importoTotale; }
    public double getImportoComune() { return importoComune; }
    public double getImportoPersonale() { return importoPersonale; }
    public List<SaldoUtente> getSaldiUtenti() { return saldiUtenti; }
}
