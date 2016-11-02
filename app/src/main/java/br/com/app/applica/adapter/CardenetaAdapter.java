package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public static class CardenetaViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private String id;
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
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public CardenetaAdapter(List<Cardeneta> cardenetas){
        this.cardenetas = cardenetas;
    }

    public void setOnItemClickListener(MyClickListener myClickListener){
        this.myClickListener = myClickListener;
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


        if(cardeneta.getDt_nasc() != null || cardeneta.getDt_nasc() == "") {
            String dt = cardeneta.getDt_nasc().substring(0, 10);

            String splittedBirthDate[] = dt.split("-");
            LocalDate localDate = new LocalDate(Integer.parseInt(splittedBirthDate[0]), Integer.parseInt(splittedBirthDate[1]), Integer.parseInt(splittedBirthDate[2]));
            vHolder.idade.setText(getIdadeByDataNasc(localDate));
        }
    }

    public void addItem(Cardeneta newCardeneta, int index){
        cardenetas.add(newCardeneta);
        System.out.println("CARDENETA ADDED: " + newCardeneta.get_id());
        notifyItemInserted(index);
    }

    @Override
    public int getItemCount() {
        return cardenetas.size();
    }

    public interface MyClickListener{
        public void onItemClick(int position, View v);
    }

    private int getMonths(LocalDate birthDate){
        LocalDate now = new LocalDate();
        return Months.monthsBetween(birthDate, now).getMonths();
    }

    private String getIdadeByDataNasc(LocalDate birthDate){
        int meses = getMonths(birthDate);
        if(meses > 12)
            return ((int) meses / 12) + " anos";
        else
            return meses + " meses";
    }
}
