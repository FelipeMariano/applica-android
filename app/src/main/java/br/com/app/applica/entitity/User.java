package br.com.app.applica.entitity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;

/**
 * Created by felipe on 29/10/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private String id;
    private String authToken;

    private String email;
    private String password;

    private HashSet<String> cardenetas;

    //Control::

    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders requestHeaders = new HttpHeaders();

    public User(String email, String password) {
        this.email = email;
        this.password = password;

        setRestConfig();

    }

    public User(){
        setRestConfig();
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

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    private void setRestConfig(){
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
    }

    public HashSet<String> getCardenetas() {
        return cardenetas;
    }

    public void setCardenetas(HashSet<String> cardenetas) {
        this.cardenetas = cardenetas;
    }

    public void authenticateAndGetToken() {
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/authenticate";

        LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
        _map.put("email", this.getEmail());
        _map.put("password", this.getPassword());

        StringWriter _writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(_writer, _map);
        }catch(Exception e){

        }

        HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

        ResponseEntity<Auth> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);

        Auth body = result.getBody();

        setAuthToken(body.getToken());
        setId(body.getUser_id());

        System.out.println("-------> " + result.getBody());
    }

    public void load(){
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + getId();

        requestHeaders.add("x-access-token", getAuthToken());

        HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
        ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, this.getClass());

        System.out.println("LOAD: " + result.getBody());
    }

    @Override
    public String toString(){
        return this.email.toString();
    }
}
