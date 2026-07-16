package entity;

public class SaldoUtente {
    private String matricola;
    private String nomeUtente;   // nome + cognome già concatenati, pronto per la UI
    private double credito;
    private double debito;
    private double saldo;

    public SaldoUtente(String matricola, String nomeUtente, double credito, double debito, double saldo) {
        this.matricola = matricola;
        this.nomeUtente = nomeUtente;
        this.credito = credito;
        this.debito = debito;
        this.saldo = saldo;
    }

    public String getMatricola() { return matricola; }
    public String getNomeUtente() { return nomeUtente; }
    public double getCredito() { return credito; }
    public double getDebito() { return debito; }
    public double getSaldo() { return saldo; }
    @Override
    public String toString() {
        return nomeUtente;
    }
}