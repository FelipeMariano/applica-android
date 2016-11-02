package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by felipe on 30/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Vacina {
    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}
