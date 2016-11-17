package br.com.app.applica.entitity;

import android.util.Xml;

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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private List<String> pendings;


    private List<Cardeneta> listaCardenetas;

    //Control:
    final String xmlFile = "UserData";


    private RestTemplate restTemplate = new RestTemplate();
    private HttpHeaders requestHeaders = new HttpHeaders();


    public User(String email, String password, List<String> pendings) {
        this.email = email;
        this.password = password;
        this.pendings = pendings;
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

    public List<Cardeneta> getListaCardenetas() {
        return listaCardenetas;
    }

    public void setListaCardenetas(List<Cardeneta> listaCardenetas) {
        this.listaCardenetas = listaCardenetas;
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

    public void setRestConfig(){
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        requestHeaders.add("x-access-token", getAuthToken());
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

        requestHeaders.add("x-access-token", body.getToken());

        setAuthToken(body.getToken());
        setId(body.getUser_id());

        System.out.println("-------> " + result.getBody());
    }

    public HttpHeaders getRequestHeaders(){
        return this.requestHeaders;
    }

    public void load(){
        try {
            String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + getId();

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);
            ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, this.getClass());

            System.out.println("LOAD: " + result);
        }catch(Exception e){
            System.out.println("USER AUTHENTICATION ERROR: " + e);
        }
    }

    public void loadCardenetas(){
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + getId() + "/cardenetas";
        System.out.println(url);
        try {

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

            ResponseEntity<List<Cardeneta>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Cardeneta>>() {
            });

            this.listaCardenetas = result.getBody();
            System.out.println(listaCardenetas);
        }catch(Exception e){
            System.out.println("AUTHENTICATION ERROR: " + e);
            User user = new User();
        }

    }

    public Cardeneta createCardeneta(Cardeneta cardeneta){
        String url = "http://applica-ihc.44fs.preview.openshiftapps.com/api/users/" + getId() + "/cardenetas";

        LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
        _map.put("nome", cardeneta.getNome());
        _map.put("sobrenome", cardeneta.getSobrenome());
        _map.put("sexo", cardeneta.getSexo());
        _map.put("dt_nasc", cardeneta.getDt_nasc());

        StringWriter _writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();

        try {
            mapper.writeValue(_writer, _map);
        }catch(Exception e){

        }


        HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);
        ResponseEntity<Cardeneta> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Cardeneta.class);

        return result.getBody();
    }

    @Override
    public String toString(){
        return this.email.toString();
    }

   public void writeUserDataLocally(FileOutputStream fos, FileOutputStream fileos){
        this.storageUserData(fos, fileos);
   }

    public void readUserDataLocally(FileInputStream fis){
        readUserData("userData", fis);
    }

    private void readUserData(String xmlFile, FileInputStream fis){
            ArrayList<String> userData = new ArrayList<String>();
            String data = null;
            try{

                InputStreamReader isr = new InputStreamReader(fis);

                char[] inputBuffer = new char[fis.available()];

                isr.read(inputBuffer);
                data = new String(inputBuffer);
                isr.close();
                fis.close();

            }catch(Exception e){

            }


            XmlPullParserFactory factory = null;

            try {
                factory = XmlPullParserFactory.newInstance();
            }catch(Exception e ){

            }

            factory.setNamespaceAware(true);
            XmlPullParser xpp = null;
            int eventType = 0;
            try{
                xpp = factory.newPullParser();
                xpp.setInput(new StringReader(data));
                eventType = xpp.getEventType();
            }catch(Exception e){

            }


            while(eventType != XmlPullParser.END_DOCUMENT){
                String dataPointer = "";
                switch(eventType){
                    case XmlPullParser.START_DOCUMENT:
                        System.out.println("Start document");
                        break;
                    case XmlPullParser.START_TAG:
                        dataPointer = xpp.getName();
                        System.out.println("Start tag " + xpp.getName());
                        break;
                    case XmlPullParser.END_TAG:
                        System.out.println("End tag " + xpp.getName());
                        break;
                    case XmlPullParser.TEXT:
                        userData.add(xpp.getText());
                        break;
                }

                try{
                    eventType = xpp.next();
                }catch(Exception e){

                }

            }

        try{

            if(userData.size() > 0) {
                requestHeaders.add("x-access-token", userData.get(3));

                this.setId(userData.get(0));
                this.setAuthToken(userData.get(3));
            }
        }catch (Exception e ){

        }

    }

    private void storageUserData(FileOutputStream fos, FileOutputStream fileos){
        XmlSerializer xmlSerializer = Xml.newSerializer();
        StringWriter writer = new StringWriter();
        try {
            xmlSerializer.setOutput(writer);
            xmlSerializer.startDocument("UTF-8", true);
            xmlSerializer.startTag(null, "userData");

            xmlSerializer.startTag(null, "id");
            xmlSerializer.text(this.id);
            xmlSerializer.endTag(null, "id");

            xmlSerializer.startTag(null, "email");
            xmlSerializer.text(this.email);
            xmlSerializer.endTag(null, "email");

            xmlSerializer.startTag(null, "password");
            xmlSerializer.text(this.password);
            xmlSerializer.endTag(null, "password");

            xmlSerializer.startTag(null, "x-access-token");
            xmlSerializer.text(this.authToken);
            xmlSerializer.endTag(null, "x-access-token");

            xmlSerializer.endTag(null, "userData");
            xmlSerializer.endDocument();
            xmlSerializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        }catch(Exception e){
            System.out.println("----> ERROR TO SAVE USER: " + e);
        }
        System.out.println("USER SAVED");

    }



}
