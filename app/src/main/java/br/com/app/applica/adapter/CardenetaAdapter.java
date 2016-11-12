package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.util.List;

import br.com.app.applica.R;
import br.com.app.applica.entitity.Cardeneta;

/**
 * Created by felipe on 30/10/16.
 */
public class CardenetaAdapter extends RecyclerView.Adapter<CardenetaAdapter .CardenetaViewHolder> {
       private List<Cardeneta> cardenetas;
       private static String LOG_TAG = "CardenetaAdapter";
       private static MyClickListener myClickListener;
       private static MyLongClickListener myLongClickListener;

    public static class CardenetaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        private String id;
        public final ImageView perf_img;
        public final TextView nome;
        public final TextView idade;

        public void setId(String id){
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public CardenetaViewHolder(final View itemView) {
            super(itemView);
            itemView.setTag(this);
            nome = (TextView) itemView.findViewById(R.id.cardeneta_item_nome);
            idade = (TextView) itemView.findViewById(R.id.cardeneta_item_idade);
            perf_img = (ImageView) itemView.findViewById(R.id.card_perf_img);

            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }

        @Override
        public boolean onLongClick(View v){
            return myLongClickListener.onItemHold(getPosition(), v);
        }
    }

    public CardenetaAdapter(List<Cardeneta> cardenetas){
        this.cardenetas = cardenetas;
    }

    public void setOnItemClickListener(MyClickListener myClickListener){
        this.myClickListener = myClickListener;
    }

    public void setOnItemHoldListener(MyLongClickListener myLongClickListener){
        this.myLongClickListener = myLongClickListener;
    }

    @Override
    public CardenetaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardeneta_item, parent, false);

        CardenetaViewHolder holder = new CardenetaViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(CardenetaViewHolder holder, int position) {
        CardenetaViewHolder vHolder =  holder;

        Cardeneta cardeneta = cardenetas.get(position);
        vHolder.setId(cardeneta.get_id());
        vHolder.nome.setText(cardeneta.toString());

        int idade_months = 0;

        if(cardeneta.getDt_nasc() != null) {
            String dt = cardeneta.getDt_nasc().substring(0, 10);

            String splittedBirthDate[] = dt.split("-");
            System.out.println(splittedBirthDate[0]);

            LocalDate localDate = new LocalDate(Integer.parseInt(splittedBirthDate[0]), Integer.parseInt(splittedBirthDate[1]), Integer.parseInt(splittedBirthDate[2]));

            idade_months = getMonths(localDate);

            vHolder.idade.setText(getIdadeByDataNasc(idade_months));
        }

        if(cardeneta.getSexo().equals("Masculino")) {
            if(idade_months >= 180)
                vHolder.perf_img.setImageResource(R.mipmap.ic_profile_boy);
            else
                vHolder.perf_img.setImageResource(R.mipmap.ic_profile_baby_boy);
        }else{
            if(idade_months >= 180)
                vHolder.perf_img.setImageResource(R.mipmap.ic_profile_girl);
            else
                vHolder.perf_img.setImageResource(R.mipmap.ic_profile_baby_girl);
        }
    }

    @Override
    public int getItemCount() {
        return cardenetas.size();
    }

    public interface MyClickListener{
        public void onItemClick(int position, View v);
    }

    public interface MyLongClickListener{
        public boolean onItemHold(int position, View v);
    }

    private int getMonths(LocalDate birthDate){
        LocalDate now = new LocalDate();
        return Months.monthsBetween(birthDate, now).getMonths();
    }

    private String getIdadeByDataNasc(int meses){
        if(meses >= 24)
            return ((int) meses / 12) + " anos";
        else
            return meses + " meses";
    }
}
