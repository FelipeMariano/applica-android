package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

/**
 * Created by felipe on 29/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cardeneta {
    private String _id;
    private String nome;
    private String sobrenome;
    private String sexo;
    private String dt_nasc;

    private static RestTemplate restTemplate = new RestTemplate();
    private static HttpHeaders requestHeaders = new HttpHeaders();

    public Cardeneta() {
        setRestConfig();
    }

    public Cardeneta(String nome, String sobrenome, String sexo){
        setRestConfig();
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.sexo = sexo;
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

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getDt_nasc() {
        return dt_nasc;
    }

    public void setDt_nasc(String dt_nasc) {
        this.dt_nasc = dt_nasc;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public void setAuthToken(String authToken){
        requestHeaders.add("x-access-token", authToken);
        System.out.println(authToken);
    }

    private static void setRestConfig(){
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public static Cardeneta load(HttpHeaders requestHeaders, String id){
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/cardenetas/" + id;
        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
        ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Cardeneta.class);
        System.out.println("LOAD: " + result.getBody());
        return (Cardeneta) result.getBody();
    }
}
