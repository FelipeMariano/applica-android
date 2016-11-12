package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by felipe on 30/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aplicacao {
    private String _id;
    private String vacina;
    private String data;
    private String dose;
    private Boolean efetivada;

    public Aplicacao(){

    }

    public Aplicacao(String _id, String vacina, String data, String dose, Boolean efetivada) {
        this._id = _id;
        this.vacina = vacina;
        this.data = data;
        this.dose = dose;
        this.efetivada = efetivada;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getEfetivada() {
        return efetivada;
    }

    public void setEfetivada(Boolean efetivada) {
        this.efetivada = efetivada;
    }

    public String getVacina() {
        return vacina;
    }

    public void setVacina(String vacina) {
        this.vacina = vacina;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFormattedData(){
        if(this.data != null) {
            String strDate = this.data.toString();
            String separetedDate[] = strDate.substring(0, 10).split("-");

            return separetedDate[2] + "/" + separetedDate[1] + "/" + separetedDate[0];
        }

        return "";
    }

}
