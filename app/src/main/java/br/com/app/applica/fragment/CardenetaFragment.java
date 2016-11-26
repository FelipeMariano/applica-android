package br.com.app.applica.fragment;


import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import br.com.app.applica.MainNavActivity;
import br.com.app.applica.R;
import br.com.app.applica.activity.NotificationActivity;
import br.com.app.applica.adapter.AplicacaoAdapter;
import br.com.app.applica.entitity.Aplicacao;
import br.com.app.applica.entitity.Cardeneta;
import br.com.app.applica.entitity.User;
import br.com.app.applica.util.NotificationPublisher;

/**
 * A simple {@link Fragment} subclass.
 */
public class CardenetaFragment extends Fragment {
    private static Cardeneta CURRENT_CARD;
    private static String CURRENT_CARD_ID;
    private static String AUTH_TOKEN;
    private List<Aplicacao> aplicacoes;
    private RecyclerView.Adapter mAdapter;

    public static MainNavActivity navActivity;

    public CardenetaFragment() {
        // Required empty public constructor
    }

    private void setRecyclerLayout(RecyclerView recyclerView, List<Aplicacao> listAplicacoes){

        RecyclerView.LayoutManager layout;
        mAdapter = new AplicacaoAdapter(listAplicacoes);

        recyclerView.setAdapter(mAdapter);
        layout = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layout);


        ((AplicacaoAdapter) mAdapter).setOnItemClickListener(new AplicacaoAdapter.MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                AplicacaoAdapter.AplicacaoViewHolder vHolder = (AplicacaoAdapter.AplicacaoViewHolder) v.getTag();

                Fragment aplicacaoForm = new AplicacaoFormFragment();

                Bundle bundle = new Bundle();
                bundle.putString("aplicacao_id", vHolder.getId());
                bundle.putString("card_id", CURRENT_CARD_ID);

                aplicacaoForm.setArguments(bundle);

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout, aplicacaoForm);
                transaction.addToBackStack(null);

                transaction.commit();
            }
        });


        ((AplicacaoAdapter) mAdapter).setBtnAlarmeListener(new AplicacaoAdapter.BtnAlarmeListener() {
            @Override
            public void onToggle(int position, View view) {
                AplicacaoAdapter.AplicacaoViewHolder vHolder = (AplicacaoAdapter.AplicacaoViewHolder) view.getTag();
                String vacina = vHolder.vacina.getText().toString();
                String data = vHolder.data.getText().toString();
                String dose = vHolder.dose.getText().toString();
                String detalhes = "Tomarei a vacina no hospital de são jesus";

                Map<String, String> dados = new ArrayMap<String, String>();
                dados.put("vacina", vacina);
                dados.put("data", data);
                dados.put("dose", dose);
                dados.put("detalhes", detalhes);

                if(vHolder.btnAlarme.isChecked())
                    vHolder.btnAlarme.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_notification_on));
                else {
                    vHolder.btnAlarme.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_notification_off));
                    return;
                }
                try {
                    scheduleNotification(navActivity, 10000, 1, dados);
                    Toast.makeText(navActivity, "Alarme adicionado com sucesso.", Toast.LENGTH_SHORT).show();
                }catch(Exception e){

                }

            }
        });

    }

    private void loadDadadosCardeneta(){

        CardenetaDadosTask loadCardDados = new CardenetaDadosTask();

        try{
            loadCardDados.execute();
            loadCardDados.get(5000, TimeUnit.MILLISECONDS);
        }catch(Exception e){
            System.out.println("ERRO AO CHAMAR TASK PARA LOAD CARD: " + e);
        }

        String title_name = CURRENT_CARD.getNome() + " " + CURRENT_CARD.getSobrenome();
        navActivity.getSupportActionBar().setTitle(title_name);
        aplicacoes = CURRENT_CARD.getListaAplicacoes();

    }

    private List<Aplicacao> filterListaAplicacoes(String toggleAction){
        switch(toggleAction){
            case "agenda":
                return getFuturasAplicacoes();
            case "historico":
                return getHistoricoAplicacoes();
            default:
                return aplicacoes;
        }
    }

    private List<Aplicacao> getFuturasAplicacoes(){
        List<Aplicacao> futurasAplicacoes = new ArrayList<>();
        for(Aplicacao aplicacao : aplicacoes){
            if(aplicacao.getEfetivada().equals(false)){
                futurasAplicacoes.add(aplicacao);
            }
        }

        return futurasAplicacoes;
    }

    private List<Aplicacao> getHistoricoAplicacoes(){
        List<Aplicacao> realizadadasAplicacoes = new ArrayList<>();
        for(Aplicacao aplicacao : aplicacoes){
            if(aplicacao.getEfetivada().equals(true)){
                realizadadasAplicacoes.add(aplicacao);
            }
        }

        return realizadadasAplicacoes;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        navActivity = (MainNavActivity) getActivity();


        Bundle bundle = this.getArguments();
        if(bundle != null) {
            CURRENT_CARD_ID = bundle.getString("card_id");

            User user = navActivity.CURRENT_USER;
            navActivity.toggleFab(MainNavActivity.TAG_APLICACAO, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AplicacaoFormFragment fragment = new AplicacaoFormFragment();
                    fragment.CURRENT_APLICACAO_ID = null;
                    fragment.CURRENT_CARD_ID = CURRENT_CARD_ID;
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_layout, fragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }
            });
            AUTH_TOKEN = user.getAuthToken();
        }
        //Load cardeneta data by id acima /\


        loadDadadosCardeneta();

        View cardenetaView = inflater.inflate(R.layout.fragment_cardeneta, container, false);
        final RecyclerView mRecyclerView = (RecyclerView) cardenetaView.findViewById(R.id.aplicacoes_recycler);


        final Button btnFilterAgenda = (Button) cardenetaView.findViewById(R.id.btn_filter_agenda);
        final Button btnFilterHistorico= (Button) cardenetaView.findViewById(R.id.btn_filter_historico);

        btnFilterAgenda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerLayout(mRecyclerView, filterListaAplicacoes("agenda"));
                btnFilterAgenda.setBackground(getResources().getDrawable(R.drawable.mybutton));
                btnFilterAgenda.setTextColor(getResources().getColor(R.color.colorWhite));

                btnFilterHistorico.setBackground(getResources().getDrawable(R.drawable.defaultbutton));
                btnFilterHistorico.setTextColor(getResources().getColor(R.color.black_overlay));
            }
        });


        btnFilterHistorico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRecyclerLayout(mRecyclerView, filterListaAplicacoes("historico"));
                btnFilterAgenda.setBackground(getResources().getDrawable(R.drawable.defaultbutton));
                btnFilterAgenda.setTextColor(getResources().getColor(R.color.black_overlay));

                btnFilterHistorico.setBackground(getResources().getDrawable(R.drawable.mybutton));
                btnFilterHistorico.setTextColor(getResources().getColor(R.color.colorWhite));
            }
        });

        setRecyclerLayout(mRecyclerView, filterListaAplicacoes("agenda"));
        setHasOptionsMenu(true);
        return cardenetaView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        navActivity.getMenuInflater().inflate(R.menu.main_nav, menu);
    }

    public static boolean share(String token, final String card, final MainNavActivity navActivity){
        Context context = navActivity;
        LayoutInflater li = LayoutInflater.from(context);
        final View promptsView = li.inflate(R.layout.share_dialog, null);

        AUTH_TOKEN = token;

        final EditText input = new EditText(navActivity);
        final TextView label = new TextView(navActivity);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        label.setLayoutParams(lp);
        input.setLayoutParams(lp);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(navActivity);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Compartilhar",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id){
                                String emailToShare = input.getText().toString();
                                Toast.makeText(navActivity, "Cardeneta compartilhada com sucesso", Toast.LENGTH_SHORT).show();
                                share(card, navActivity.CURRENT_USER, emailToShare.trim().toLowerCase());
                            }
                        })
                .setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage("Digite o e-mail de destino");
        alertDialog.setView(input);
        alertDialog.show();

        return true;
    }

    private static void share(String card, User userOrig, String emailDest){
        String emailOrig = userOrig.getEmail();

        CardenetaShareTask cardenetaShareTask = new CardenetaShareTask();

        try{
            cardenetaShareTask.execute(card, emailOrig, emailDest);
        }catch(Exception e){
            System.out.println("ERRO AO COMPARTILHAR: " + e);
        }
    }

    public static boolean delete(String token, final String toDelete, final MainNavActivity navActivity){

        if (toDelete == null || toDelete.equals("")) return false;

        CURRENT_CARD_ID = toDelete;
        AUTH_TOKEN = token;

        new AlertDialog.Builder(navActivity).setTitle("Deletar cardeneta")
                .setMessage("Você realmente deseja deletar a cardeneta permanentemente? Todos os dados aqui serão perdidos!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){

                        try{
                            CardenetaDeleteTask deleteTask = new CardenetaDeleteTask();
                            try{
                                deleteTask.execute();
                                if (deleteTask.get(5000, TimeUnit.MILLISECONDS) == null) {
                                    Toast.makeText(navActivity, "Cardeneta deletada!", Toast.LENGTH_SHORT).show();

                                    if(navActivity != null)
                                        navActivity.getSupportFragmentManager().popBackStack();

                                }
                            }catch(Exception e){
                                System.out.println("TIME EXCEPTION WHILE DELETING! " + e);
                            }
                        }catch(Exception e){
                            System.out.println("INVALID ID");

                        }
                            Toast.makeText(navActivity, "Cardeneta deletada!", Toast.LENGTH_SHORT).show();
                            for(Cardeneta card : HomeFragment.cardenetas){
                                if(card.get_id().equals(toDelete)){
                                    HomeFragment.cardenetas.remove(card);
                                    break;
                                }
                            }
                    }
                }).setNegativeButton(android.R.string.no, null).show();


        return true;
    }

    public static void setToEdit(String toEdit, FragmentManager fragmentManager){
        CardenetaFormFragment cardForm = new CardenetaFormFragment();

        Bundle bundle = new Bundle();
        bundle.putString("card_id", toEdit);

        cardForm.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_layout, cardForm);
        transaction.addToBackStack(null);

        transaction.commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_edit:
                setToEdit(CURRENT_CARD_ID, getFragmentManager());
                return true;
            case R.id.action_delete:
                delete(AUTH_TOKEN, CURRENT_CARD_ID, navActivity);
                return true;
            case R.id.action_share:
                share(AUTH_TOKEN, CURRENT_CARD_ID, navActivity);
        }
        return false;
    }

    private static class CardenetaShareTask extends AsyncTask<String, String, Cardeneta>{

        @Override
        protected Cardeneta doInBackground(String... params) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            String card = params[0];
            String emailOrig = params[1];
            String emailDest = params[2];


            LinkedHashMap<String, Object> _map = new LinkedHashMap<String, Object>();
            _map.put("email_orig", emailOrig);
            _map.put("email_dest", emailDest);

            StringWriter _writer = new StringWriter();
            ObjectMapper mapper = new ObjectMapper();

            try {
                mapper.writeValue(_writer, _map);
            }catch(Exception e){

            }

            try {

                String url = navActivity.BASE_URL +  "/api/cardenetas/" + card;
                url += "/share";

                HttpEntity<String> httpEntity = new HttpEntity<String>(_writer.toString(), requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Object.class);
                System.out.println(result.getBody());
            }catch(Exception e){
                System.out.println("ERRO AO COMPARTILHAR CARDENETA: " + e);
            }

            return null;
        }
    }

    private static class CardenetaDeleteTask extends AsyncTask<Void, Void, br.com.app.applica.entitity.Cardeneta>{

        @Override
        protected Cardeneta doInBackground(Void... params) {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            try {

                String url = navActivity.BASE_URL + "/api/cardenetas/" + CURRENT_CARD_ID;

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.DELETE, httpEntity, Object.class);
                System.out.println(result.getBody());
                CURRENT_CARD_ID = null;
            }catch(Exception e){
                System.out.println("ERRO AO DELETAR CARDENETA: " + e);
            }


            return null;
        }
    }

    private class CardenetaDadosTask extends AsyncTask<Void, Void, Cardeneta> {
        private Cardeneta loadedCardeneta;

        private void loadData(HttpHeaders requestHeaders, RestTemplate restTemplate){
            try {

                String url = navActivity.BASE_URL + "/api/cardenetas/" + CURRENT_CARD_ID;

                HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

                ResponseEntity<?> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, Cardeneta.class);
                loadedCardeneta = (Cardeneta) result.getBody();
            }catch(Exception e){
                System.out.println("ERRO AO LOAD CARDENETA: " + e);
                loadedCardeneta = new Cardeneta();
            }
        }

        private void loadAplicacoes(HttpHeaders requestHeaders, RestTemplate restTemplate){
            String url = navActivity.BASE_URL + "/api/cardenetas/" + CURRENT_CARD_ID;
            url += "/aplicacoes";

            HttpEntity<String> httpEntity = new HttpEntity<String>(requestHeaders);

            ResponseEntity<List<Aplicacao>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Aplicacao>>() {
            });

            loadedCardeneta.setListaAplicacoes(result.getBody());
            System.out.println(loadedCardeneta.getListaAplicacoes());
        }

        @Override
        protected Cardeneta doInBackground(Void... params) {

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders requestHeaders = new HttpHeaders();

            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            requestHeaders.setContentType(MediaType.APPLICATION_JSON);
            requestHeaders.add("x-access-token", AUTH_TOKEN);

            loadData(requestHeaders, restTemplate);

            if(loadedCardeneta.get_id() != null)
                loadAplicacoes(requestHeaders, restTemplate);

            //ResponseEntity<List<Cardeneta>> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, new ParameterizedTypeReference<List<Cardeneta>>() {
            //});
            CURRENT_CARD = loadedCardeneta;
            return CURRENT_CARD;
        }
    }

    public void scheduleNotification(Context context, long delay, int notificationId, Map<String, String> dados) {//delay is after how much time(in millis) from current time you want to schedule the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(context.getString(R.string.notification_content))
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_check)
                .setLargeIcon(((BitmapDrawable) context.getResources().getDrawable(R.drawable.cast_ic_notification_rewind)).getBitmap())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        Intent intent = new Intent(context, NotificationActivity.class);
        intent.putExtra("data", dados.get("data"));
        intent.putExtra("vacina", dados.get("vacina"));
        intent.putExtra("dose", dados.get("dose"));
        intent.putExtra("detalhes", dados.get("detalhes"));
        PendingIntent activity = PendingIntent.getActivity(context, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        builder.setContentIntent(activity);

        Notification notification = builder.build();

        Intent notificationIntent = new Intent(context, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, notificationId);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);

    }
}
