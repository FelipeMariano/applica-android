package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.List;

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
    private List<Aplicacao> ListaAplicacoes;

    private static RestTemplate restTemplate = new RestTemplate();
    private static HttpHeaders requestHeaders = new HttpHeaders();
    public Cardeneta() {
        setRestConfig();
    }

    public Cardeneta(String nome, String sobrenome, String sexo) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.sexo = sexo;
    }

    public Cardeneta(String nome, String sobrenome, String sexo, String dt_nasc){
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.sexo = sexo;
        this.dt_nasc = dt_nasc;
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

    public List<Aplicacao> getListaAplicacoes() {
        return ListaAplicacoes;
    }

    public void setListaAplicacoes(List<Aplicacao> listaAplicacoes) {
        ListaAplicacoes = listaAplicacoes;
    }

    public void setAuthToken(String authToken){
        requestHeaders.add("x-access-token", authToken);
        System.out.println(authToken);
    }

    @Override
    public String toString(){
        return this.nome + " " + this.sobrenome;
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

    public List<Aplicacao> loadAplicacoes(HttpHeaders requestHeaders){
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/cardenetas/" + get_id() + "/aplicacoes";

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

        ResponseEntity<List<Aplicacao>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Aplicacao>>() {
        });



        this.ListaAplicacoes = result.getBody();

        return this.ListaAplicacoes;
    }

    public void createAplicacao(HttpHeaders requestHeaders, Aplicacao aplicacao){
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/cardenetas/" + get_id() + "/aplicacoes";

        LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
        _map.put("data", aplicacao.getData());
        _map.put("vacina", aplicacao.getVacina());
        _map.put("dose", aplicacao.getDose());
        //_map.put("local", aplicacao.get);

        StringWriter _writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(_writer, _map);
        }catch(Exception e){

        }


        HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);
        ResponseEntity<Aplicacao> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Aplicacao.class);

        System.out.println(result.getBody());

    }
}
