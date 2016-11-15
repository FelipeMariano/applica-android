package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by felipe on 15/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Localizacao {
    private String type;
    private String address;
    private List<Float> coordinates;

    public Localizacao() {

    }

    public Localizacao(String type, String address, List<Float> coordinates) {
        this.type = type;
        this.address = address;
        this.coordinates = coordinates;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Float> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Float> coordinates) {
        this.coordinates = coordinates;
    }
}
