package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        vHolder.nome.setText(cardeneta.getNome());
    }

    @Override
    public int getItemCount() {
        return cardenetas.size();
    }

    public interface MyClickListener{
        public void onItemClick(int position, View v);
    }
}
