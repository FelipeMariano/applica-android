package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.app.applica.R;
import br.com.app.applica.entitity.Aplicacao;

/**
 * Created by felipe on 30/10/16.
 */
public class AplicacaoAdapter extends RecyclerView.Adapter<AplicacaoAdapter .AplicacaoViewHolder> {
    private List<Aplicacao> aplicacoes;
    private static String LOG_TAG = "AplicacaoAdapter";
    private static MyClickListener myClickListener;


    public static class AplicacaoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private String id;
        public final TextView data;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public AplicacaoViewHolder(final View itemView){
            super(itemView);
            itemView.setTag(this);
            data = (TextView) itemView.findViewById(R.id.aplicacao_item_data);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public AplicacaoAdapter(List<Aplicacao> aplicacoes){ this.aplicacoes = aplicacoes; }


    @Override
    public AplicacaoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.aplicacao_item, parent, false);

        AplicacaoViewHolder holder = new AplicacaoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(AplicacaoViewHolder holder, int position) {
        AplicacaoViewHolder vHolder = holder;

        Aplicacao aplicacao = aplicacoes.get(position);
        vHolder.setId(aplicacao.get_id());
        vHolder.data.setText(aplicacao.get_id());
    }

    @Override
    public int getItemCount() {
        return aplicacoes.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener){
        this.myClickListener = myClickListener;
    }
    public interface MyClickListener{
        public void onItemClick(int position, View v);
    }

}
