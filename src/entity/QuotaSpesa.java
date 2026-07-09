package entity;

public class QuotaSpesa {
    private int idSpesa;
    private String matricolaDebitore;
    private double quotaDovuta;

    public QuotaSpesa(int idSpesa, String matricolaDebitore, double quotaDovuta) {
        this.idSpesa = idSpesa;
        this.matricolaDebitore = matricolaDebitore;
        this.quotaDovuta = quotaDovuta;
    }

    public int getIdSpesa() { return idSpesa; }
    public String getMatricolaDebitore() { return matricolaDebitore; }
    public double getQuotaDovuta() { return quotaDovuta; }
}