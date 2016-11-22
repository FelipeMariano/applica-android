package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by felipe on 17/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Pendencia {
    private String _id;
    private User user_origin;
    private User user_dest;
    private Cardeneta cardeneta;

    public Pendencia() {
    }

    public Pendencia(String _id, User user_origin, User user_dest, Cardeneta cardeneta) {
        this._id = _id;
        this.user_origin = user_origin;
        this.user_dest = user_dest;
        this.cardeneta = cardeneta;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public User getUser_origin() {
        return user_origin;
    }

    public void setUser_origin(User user_origin) {
        this.user_origin = user_origin;
    }

    public User getUser_dest() {
        return user_dest;
    }

    public void setUser_dest(User user_dest) {
        this.user_dest = user_dest;
    }

    public Cardeneta getCardeneta() {
        return cardeneta;
    }

    public void setCardeneta(Cardeneta cardeneta) {
        this.cardeneta = cardeneta;
    }

    public String getCardenetaNome(){
        return getCardeneta().getNome() + " " + getCardeneta().getSobrenome();
    }

    public String getOrigemNome(){
       User orig_user = getUser_origin();
        return orig_user.getNome() + " " + orig_user.getSobrenome();
    }
}
