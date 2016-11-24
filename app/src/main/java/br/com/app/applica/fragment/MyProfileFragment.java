package br.com.app.applica.fragment;


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

import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.entitity.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyProfileFragment extends Fragment {
    private MainNavActivity navActivity;
    private User USER;
    private Menu mMenu;
    private MenuInflater mMenuInflater;
    private Boolean IS_TO_EDIT = false;
    private Boolean IS_TO_EDIT_PASSWORD = false;

    private TextView email;
    private TextView nome;
    private TextView sobrenome;
    private TextView dtNasc;
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

        btnEditPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleEditPassword();
            }
        });

        btnEditPass.setVisibility(View.GONE);

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
                toggleOptionsMenu();
                setToEdit();
                return true;
            case R.id.action_close:
                IS_TO_EDIT = false;
                toggleOptionsMenu();
                setToView();
        }
        return false;
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

    private void setUserData(){
        USER.setEmail(email.getText().toString());
        USER.setNome(nome.getText().toString());
        USER.setSobrenome(sobrenome.getText().toString());
        USER.setDt_nasc(dtNasc.getText().toString());
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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setUserData();
                if(!edit()){
                    navActivity.CURRENT_USER = USER;
                    Toast.makeText(navActivity, "Erro ao atualizar", Toast.LENGTH_SHORT).show();
                    return;
                }

                Toast.makeText(navActivity, "Atualizado com sucesso", Toast.LENGTH_SHORT).show();
                navActivity.CURRENT_USER = USER;
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

    private class UserUpdateTask extends AsyncTask<Void, Void, User> {

        @Override
        protected User doInBackground(Void... params) {
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

            }

            try {

                HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.PUT, httpEntity, User.class);

                System.out.println(result.getBody());

            }catch(Exception e){
                System.out.println("FAILS TO VALIDATE USER: " + e);
                USER = new User();
            }finally{
                return USER;
            }
        }
    }
}
