package br.com.app.applica.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.activity.LoginActivity;
import br.com.app.applica.entitity.Auth;
import br.com.app.applica.entitity.User;
import br.com.app.applica.util.AutoAddTextWatcher;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
    private MainNavActivity navActivity;
    private User USER;
    private User USER_TEMP;
    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private Boolean IS_TO_EDIT = false;
    private Boolean IS_TO_EDIT_PASSWORD = false;

    private TextView email;
    private TextView nome;
    private TextView sobrenome;
    private TextView dtNasc;
    private TextView newPassword;
    private Spinner sexo;
    private Button btnSave;
    private Button btnEditPass;

    public MyProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        navActivity = (MainNavActivity) getActivity();

        View profileView = inflater.inflate(R.layout.fragment_my_profile, container, false);

        btnEditPass = (Button) profileView.findViewById(R.id.edit_password);
        View profilePass = profileView.findViewById(R.id.profile_password);

        profilePass.setVisibility(View.GONE);
        USER_TEMP = navActivity.CURRENT_USER;
        btnEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               toggleEditPassword();
            }
        });

        btnEditPass.setVisibility(View.GONE);

        final EditText date = (EditText) profileView.findViewById(R.id.profile_dt_nasc);

       date.addTextChangedListener(new AutoAddTextWatcher(date, "/", 2, 4));

        setHasOptionsMenu(true);
        loadUser(profileView);

        return profileView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        mMenu = menu;
        mMenuInflater = inflater;
        navActivity.getMenuInflater().inflate(R.menu.edit_menu, menu);
    }

    private void toggleOptionsMenu(){
        mMenu.clear();
        if(IS_TO_EDIT)
            navActivity.getMenuInflater().inflate(R.menu.close_menu, mMenu);
        else
            navActivity.getMenuInflater().inflate(R.menu.edit_menu, mMenu);

    }

    private void toggleEditPassword(){
        View editPass = navActivity.findViewById(R.id.profile_password);

        if(IS_TO_EDIT_PASSWORD){
            IS_TO_EDIT_PASSWORD = false;
            editPass.setVisibility(View.GONE);
            btnEditPass.setTextColor(getResources().getColor(R.color.colorWhite));
            btnEditPass.setBackground(getResources().getDrawable(R.drawable.mybutton));
        }else{
            IS_TO_EDIT_PASSWORD = true;
            editPass.setVisibility(View.VISIBLE);
            btnEditPass.setTextColor(getResources().getColor(R.color.black_overlay));
            btnEditPass.setBackground(getResources().getDrawable(R.drawable.mybutton_default));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_edit:
                IS_TO_EDIT = true;
                System.out.println("SET!");
                toggleOptionsMenu();
                setToEdit();
                return true;
            case R.id.action_close:
                setPattern();
                IS_TO_EDIT = false;
                toggleOptionsMenu();
                IS_TO_EDIT_PASSWORD = true;
                toggleEditPassword();
                setToView();
        }
        return false;
    }

    private void setPattern(){
        nome.setText(USER_TEMP.getNome());
        sobrenome.setText(USER_TEMP.getSobrenome());
        dtNasc.setText(USER_TEMP.getFormattedData());

        TextView errorNome = (TextView) navActivity.findViewById(R.id.error_profile_nome);
        TextView errorSobrenome = (TextView) navActivity.findViewById(R.id.error_profile_sobrenome);
        TextView errorDtNasc = (TextView) navActivity.findViewById(R.id.error_profile_dt_nasc);
        TextView errorNewPass = (TextView) navActivity.findViewById(R.id.error_new_password);
        TextView errorNewPassRepeat = (TextView) navActivity.findViewById(R.id.error_new_password_repeat);
        TextView errorOldPass = (TextView) navActivity.findViewById(R.id.error_old_password);

        errorNome.setText("");
        errorSobrenome.setText("");
        errorDtNasc.setText("");
        errorNewPass.setText("");
        errorNewPassRepeat.setText("");
        errorOldPass.setText("");

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.sexo_arrays,
                    android.R.layout.simple_spinner_item);


            int itemPosition = adapter.getPosition(USER_TEMP.getSexo());
            sexo.setSelection(itemPosition);


        newPassword.setText("");
        EditText newPasswordRepeat = (EditText) navActivity.findViewById(R.id.new_password_repeat);
        EditText oldPassword = (EditText) navActivity.findViewById(R.id.old_password);
        newPasswordRepeat.setText("");
        oldPassword.setText("");
        USER = USER_TEMP;
    }

    private void setToEdit(){
        TextView email = (TextView) navActivity.findViewById(R.id.profile_email);
       // email.setEnabled(true);
        nome.setEnabled(true);
        sobrenome.setEnabled(true);
        dtNasc.setEnabled(true);
        sexo.setEnabled(true);
        btnSave.setVisibility(View.VISIBLE);
        btnEditPass.setVisibility(View.VISIBLE);
    }

    private void setToView(){
        email.setEnabled(false);
        nome.setEnabled(false);
        sobrenome.setEnabled(false);
        dtNasc.setEnabled(false);
        sexo.setEnabled(false);
        btnSave.setVisibility(View.GONE);
        btnEditPass.setVisibility(View.GONE);
    }

    private boolean setAndValidateUserData(){
        boolean isValid = true;

        String separatedData[] = dtNasc.getText().toString().split("/");


        if(IS_TO_EDIT_PASSWORD && !setAndValidatePassword())
            isValid = false;

        if(!isValidData(separatedData[0], separatedData[1], separatedData[2]))
            isValid = false;

        if(!isValidNome(nome.getText().toString()))
            isValid = false;

        if(!isValidSobrenome(sobrenome.getText().toString()))
            isValid = false;

        if(isValid){
            USER.setNome(nome.getText().toString());
            USER.setSobrenome(sobrenome.getText().toString());
            USER.setDt_nasc(separatedData[2] + "-" + separatedData[1] + "-" + separatedData[0]);
        }

        return isValid;

    }


    private boolean isValidSobrenome(String sobrenome){
        Boolean isValid = true;
        TextView errorSobrenome = (TextView) navActivity.findViewById(R.id.error_profile_sobrenome);

        if(sobrenome.length() < 1){
            isValid = false;
            errorSobrenome.setText("*Sobrenome inválido");
        }else
            errorSobrenome.setText("");

        return isValid;
    }

    private boolean isValidNome(String nome){
        Boolean isValid = true;
        TextView errorNome = (TextView) navActivity.findViewById(R.id.error_profile_nome);

        if(nome.length() < 1){
            isValid = false;
            errorNome.setText("*Nome inválido");
        }else{
            errorNome.setText("");
        }

        return isValid;
    }

    private boolean isValidData(String dia, String mes, String ano){
        Boolean isValid = true;
        TextView errorDtNasc = (TextView) navActivity.findViewById(R.id.error_profile_dt_nasc);
        String strDate = dia + "/" + mes + "/" + ano;
        SimpleDateFormat format = new SimpleDateFormat("d/MM/yyyy");
        Date selectedDate;

        try {
            selectedDate = format.parse(strDate);
        }catch(Exception e){
            System.out.println("ERRO AO FORMATAR DATA");
            return false;
        }

        if(ano == "" || mes == "" || dia == "" || (new Date().before(selectedDate))){
            isValid = false;
            errorDtNasc.setText("*Data inválida");
        }else{
            errorDtNasc.setText("");
        }

        return isValid;
    }

    private boolean setAndValidatePassword(){
        boolean isValid = true;
        TextView newPasswordRepeat = (TextView) navActivity.findViewById(R.id.new_password_repeat);
        TextView oldPassword = (TextView) navActivity.findViewById(R.id.old_password);
        newPassword = (TextView) navActivity.findViewById(R.id.new_password);

        TextView errorNewPassword = (TextView)  navActivity.findViewById(R.id.error_new_password);
        TextView errorNewPasswordRepeat = (TextView) navActivity.findViewById(R.id.error_new_password_repeat);
        TextView errorOldPassword = (TextView) navActivity.findViewById(R.id.error_old_password);

        String oldPasswordString = oldPassword.getText().toString();

        if(!oldPasswordString.equals(navActivity.CURRENT_USER.getPassword())){
            isValid = false;
            errorOldPassword.setText("*Senha incorreta.");
        }else
            errorOldPassword.setText("");

        System.out.println(navActivity.CURRENT_USER.getPassword());
        System.out.println(oldPasswordString);

        if(!newPassword.getText().toString().equals(newPasswordRepeat.getText().toString())){
            isValid = false;
            errorNewPasswordRepeat.setText("* As senhas não condizem.");
        }else
            errorNewPasswordRepeat.setText("");

        String pass = newPassword.getText().toString();
        if(pass.length() < 8 || !pass.matches(".*[a-zA-Z]+.*")){
            isValid = false;
            errorNewPassword.setText("*A senha deve conter no mínimo 8 caracteres, com ao menos 1 letra");
        }else
            errorNewPassword.setText("");



        if(isValid){
            USER.setPassword(newPassword.getText().toString());
        }

        return isValid;
    }


    private void setSexoSpinner(View view) {
        Spinner sexoSpinner = (Spinner) view.findViewById(R.id.profile_sexo);
        sexoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                USER.setSexo((String) parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.sexo_arrays,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoSpinner.setAdapter(adapter);
    }


    private void loadUser(View view) {
        btnSave = (Button) view.findViewById(R.id.profile_save);
        email = (TextView) view.findViewById(R.id.profile_email);
        nome = (TextView) view.findViewById(R.id.profile_nome);
        sobrenome = (TextView) view.findViewById(R.id.profile_sobrenome);
        dtNasc = (TextView) view.findViewById(R.id.profile_dt_nasc);
        sexo = (Spinner) view.findViewById(R.id.profile_sexo);
        newPassword = (TextView) view.findViewById(R.id.new_password);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!setAndValidateUserData()){
                    Toast.makeText(navActivity, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                    USER = navActivity.CURRENT_USER;
                    return;
                }else {
                    edit();
                    Toast.makeText(navActivity, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
                    navActivity.CURRENT_USER = USER;
                    USER_TEMP = navActivity.CURRENT_USER;
                    MainNavActivity.setHeaderData(navActivity.CURRENT_USER, navActivity);
                    try {

                        File file = new File(navActivity.getFilesDir(), "userData.xml");
                        if(file.exists())
                            file.delete();

                        FileOutputStream fos = new FileOutputStream(file);
                        FileOutputStream fileos = navActivity.openFileOutput("userData", Context.MODE_PRIVATE);

                        LoginActivity.storageUserData(fos, fileos, navActivity.CURRENT_USER);
                    }catch(Exception e){
                        System.out.println("ERRO AO SALVAR NOVOS DADOS DO USUÁRIO: " + e);
                    }
                }
            }
        });

        USER = navActivity.CURRENT_USER;

        setSexoSpinner(view);

        email.setText(USER.getEmail());
        nome.setText(USER.getNome());
        sobrenome.setText(USER.getSobrenome());
        dtNasc.setText(USER.getFormattedData());
        System.out.println(USER.getSexo());
        if(USER.getSexo() != null) {
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(navActivity, R.array.sexo_arrays,
                    android.R.layout.simple_spinner_item);


            int itemPosition = adapter.getPosition(USER.getSexo());
            sexo.setSelection(itemPosition);
        }

        setToView();
    }

    private boolean edit(){

        UserUpdateTask userUpdateTask = new UserUpdateTask();
        try{
            userUpdateTask.execute();
            userUpdateTask.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            System.out.println("Erro ao atualizar usuário: " + e);
            return false;
        }

        return true;
    }

    private class UserUpdateTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            String url = navActivity.BASE_URL + "/api/users/";
            url += USER.getId();

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

           restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());


            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", USER.getAuthToken());

            LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();

            _map.put("nome", USER.getNome());
            _map.put("sobrenome", USER.getSobrenome());
            _map.put("sexo", USER.getSexo());
            _map.put("dt_nasc", USER.getDt_nasc());
            _map.put("password", USER.getPassword());


            StringWriter _writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();

            try {
                mapper.writeValue(_writer, _map);
            }catch(Exception e){
                System.out.println("ERROR TRYING TO PARSE WRITER: " + e);
            }

            try {
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);



                ResponseEntity<User> result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity,  User.class);

            }catch(Exception e){
                System.out.println("FAILS TO VALIDATE USER: " + e);
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean isUserSaved) {
            if(!isUserSaved) return;

            AuthenticationTask authTask = new AuthenticationTask();
            try{
                authTask.execute();
                authTask.get(5000, TimeUnit.MILLISECONDS);
            }catch(Exception e){
                System.out.println("ERRO AO AUTENTICAR USUÁRIO APÓS UPDATE: " + e);
            }

        }
    }

    private class AuthenticationTask extends AsyncTask<Void, Void, User> {
        //FAVOR MOVER ISTO PARA ACTIVITY DE LOGIN!!!!
        @Override
        protected User doInBackground(Void... params){
            try{
                //User loggedUser = new User();
                String url = navActivity.BASE_URL + "/authenticate";

                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders requestHeaders = new HttpHeaders();

                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                requestHeaders.setContentType(MediaType.APPLICATION_JSON);
                //requestHeaders.add("x-access-token", getAuthToken());

                LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
                _map.put("email", USER.getEmail());
                _map.put("password", USER.getPassword());

                StringWriter _writer = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();

                try {
                    mapper.writeValue(_writer, _map);
                }catch(Exception e){

                }
                ResponseEntity<Auth> result;
                try {

                    HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                    result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Auth.class);
                    System.out.println("--> " + result.getStatusCode());
                    Auth body = result.getBody();

                    USER.setAuthToken(body.getToken());

                }catch(Exception e){
                    System.out.println("FAILS TO VALIDATE USER: " + e);
                    return new User();
                }finally{
                    return USER;
                }

            }catch(Exception e){
                System.out.println("Error: " + e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(User user){
            System.out.println("USER EMAIL " + USER.getEmail());
            System.out.println("USER PASS " + USER.getPassword());
            System.out.println("USER LOADED");
        }
    }
}
