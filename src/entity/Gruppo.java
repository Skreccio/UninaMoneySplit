package entity;

public class Gruppo {
    private int idGruppo;
    private String nome;
    private String descrizioneGruppo;
    private String matricolaCreatore;
    private int numPartecipanti;

    public Gruppo(int idGruppo, String nome, String descrizioneGruppo, String matricolaCreatore) {
        this.idGruppo = idGruppo;
        this.nome = nome;
        this.descrizioneGruppo = descrizioneGruppo;
        this.matricolaCreatore = matricolaCreatore;
    }

    public int getIdGruppo() { return idGruppo; }
    public String getNome() { return nome; }
    public String getDescrizioneGruppo() { return descrizioneGruppo; }
    public String getMatricolaCreatore() { return matricolaCreatore; }

    public int getNumPartecipanti() { return numPartecipanti; }
    public void setNumPartecipanti(int numPartecipanti) { this.numPartecipanti = numPartecipanti; }

    @Override
    public String toString() {
        return nome;
    }
}