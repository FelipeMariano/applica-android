package br.com.app.applica.entitity;

/**
 * Created by felipe on 17/11/16.
 */
public class Pendencia {
    private String _id;
    private User origem;
    private User destino;
    private Cardeneta cardeneta;

    public Pendencia() {
    }

    public Pendencia(String _id, User origem, User destino, Cardeneta cardeneta) {
        this._id = _id;
        this.origem = origem;
        this.destino = destino;
        this.cardeneta = cardeneta;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User getOrigem() {
        return origem;
    }

    public void setOrigem(User origem) {
        this.origem = origem;
    }

    public User getDestino() {
        return destino;
    }

    public void setDestino(User destino) {
        this.destino = destino;
    }

    public Cardeneta getCardeneta() {
        return cardeneta;
    }

    public void setCardeneta(Cardeneta cardeneta) {
        this.cardeneta = cardeneta;
    }

    public String getEmailOrigem(){
        return origem.getEmail();
    }

    public String getEmailDestino(){
        return destino.getEmail();
    }
}
