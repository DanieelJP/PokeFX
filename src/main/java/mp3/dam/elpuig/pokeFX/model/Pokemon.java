package mp3.dam.elpuig.pokeFX.model;

public class Pokemon {
    private int id;
    private String nombre;
    private String tipoPrimario;
    private String tipoSecundario;
    private int generacion;
    private int hp;
    private int ataque;
    private int defensa;
    private int ataqueEspecial;
    private int defensaEspecial;
    private int velocidad;
    private boolean esLegendario;

    public Pokemon(int id, String nombre, String tipoPrimario, String tipoSecundario, 
                  int generacion, int hp, int ataque, int defensa, 
                  int ataqueEspecial, int defensaEspecial, int velocidad, 
                  boolean esLegendario) {
        this.id = id;
        this.nombre = nombre;
        this.tipoPrimario = tipoPrimario;
        this.tipoSecundario = tipoSecundario;
        this.generacion = generacion;
        this.hp = hp;
        this.ataque = ataque;
        this.defensa = defensa;
        this.ataqueEspecial = ataqueEspecial;
        this.defensaEspecial = defensaEspecial;
        this.velocidad = velocidad;
        this.esLegendario = esLegendario;
    }

    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getTipoPrimario() { return tipoPrimario; }
    public String getTipoSecundario() { return tipoSecundario; }
    public int getGeneracion() { return generacion; }
    public int getHp() { return hp; }
    public int getAtaque() { return ataque; }
    public int getDefensa() { return defensa; }
    public int getAtaqueEspecial() { return ataqueEspecial; }
    public int getDefensaEspecial() { return defensaEspecial; }
    public int getVelocidad() { return velocidad; }
    public boolean isEsLegendario() { return esLegendario; }
    
    public int getStatTotal() {
        return hp + ataque + defensa + ataqueEspecial + defensaEspecial + velocidad;
    }

    @Override
    public String toString() {
        return String.format("#%d %s", id, nombre);
    }
} 