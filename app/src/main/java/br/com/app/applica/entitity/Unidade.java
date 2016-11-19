package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by felipe on 15/11/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Unidade {
    private String _id;
    private String nome;
    private String cnpj;
    private String razao_social;
    private String telefone;
    private String email;
    private String website;
    private Localizacao location;
    private List<String> vacinas;

    public Unidade() {
    }

    public Unidade(String _id, String nome, String cnpj, String razao_social, Localizacao location, List<String> vacinas) {
        this._id = _id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.razao_social = razao_social;
        this.location = location;
        this.vacinas = vacinas;
    }

    public Unidade(String _id, String nome, String cnpj, String razao_social, String telefone, String email, String website, Localizacao location, List<String> vacinas) {
        this._id = _id;
        this.nome = nome;
        this.cnpj = cnpj;
        this.razao_social = razao_social;
        this.telefone = telefone;
        this.email = email;
        this.website = website;
        this.location = location;
        this.vacinas = vacinas;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getRazao_social() {
        return razao_social;
    }

    public void setRazao_social(String razao_social) {
        this.razao_social = razao_social;
    }

    public Localizacao getLocation() {
        return location;
    }

    public void setLocation(Localizacao location) {
        this.location = location;
    }

    public List<String> getVacinas() {
        return vacinas;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setVacinas(List<String> vacinas) {
        this.vacinas = vacinas;
    }
}
