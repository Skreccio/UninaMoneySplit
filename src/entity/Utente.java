package entity;

public class Utente {
    private String matricola;
    private String nome;
    private String cognome;
    private String email;

    public Utente(String matricola, String nome, String cognome, String email) {
        this.matricola = matricola;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
    }

    public String getMatricola() {
        return matricola;
    }
    public String getNome() {
        return nome;
    }
    public String getCognome() {
        return cognome;
    }
    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return nome + " " + cognome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utente altro = (Utente) o;
        return matricola.equals(altro.matricola);
    }

    @Override
    public int hashCode() {
        return matricola.hashCode();
    }
}
