package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by felipe on 29/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String authToken;
    
    private String email;
    private String password;

    private String nome;
    private String sobrenome;
    private List<String> pendings;
    private Localizacao location;


    private List<Cardeneta> listaCardenetas;

    //Control:
    final String xmlFile = "UserData";


    public User() {
    }

    public User(String email, String password, List<String> pendings) {
        this.email = email;
        this.password = password;
        this.pendings = pendings;
    }

    public User(String id, String authToken, String email, String password, List<String> pendings, Localizacao location) {
        this.id = id;
        this.authToken = authToken;
        this.email = email;
        this.password = password;
        this.pendings = pendings;
        this.location = location;
    }

    public User(String id, String authToken, String email, String password, String nome, String sobrenome, List<String> pendings, Localizacao location) {
        this.id = id;
        this.authToken = authToken;
        this.email = email;
        this.password = password;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.pendings = pendings;
        this.location = location;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Cardeneta> getListaCardenetas() {
        return listaCardenetas;
    }

    public void setListaCardenetas(List<Cardeneta> listaCardenetas) {
        this.listaCardenetas = listaCardenetas;
    }

    public Localizacao getLocation() {
        return location;
    }

    public void setLocation(Localizacao location) {
        this.location = location;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public List<String> getPendings() {
        return pendings;
    }

    public void setPendings(List<String> pendings) {
        this.pendings = pendings;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    @Override
    public String toString(){
        return this.email.toString();
    }

    public String getFullName(){
        return getNome() + " " + getSobrenome();
    }

}
