package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * Created by felipe on 30/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aplicacao {
    public String _id;
    public Date data;
    public String local;
    public String lote;

    public Aplicacao() {
    }

    public Aplicacao(String _id) {
        this._id = _id;
    }

    public Aplicacao(String _id, Date data, String local, String lote) {
        this._id = _id;
        this.data = data;
        this.local = local;
        this.lote = lote;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }
}
