package br.com.app.applica.entitity;

/**
 * Created by felipe on 29/10/16.
 */
public class Auth {
    private String success;
    private String message;
    private String token;
    private String user_id;

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    @Override
    public String toString(){
        return this.token;
    }
}
