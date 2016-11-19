package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.app.applica.R;
import br.com.app.applica.entitity.Unidade;

/**
 * Created by felipe on 15/11/16.
 */
public class UnidadeAdapter extends RecyclerView.Adapter<UnidadeAdapter .UnidadeViewHolder> {
    private static View.OnTouchListener onItemTouch;
    private List<Unidade> unidades;

    public static class UnidadeViewHolder extends RecyclerView.ViewHolder{
        private String id;
        public final TextView nome;
        public final TextView address;

        public void setId(String id){
            this.id = id;
        }

        public String getId() {
            return id;
        }

        public UnidadeViewHolder(final View itemView){
            super(itemView);
            itemView.setTag(this);
            nome = (TextView) itemView.findViewById(R.id.unidade_item_nome);
            address = (TextView) itemView.findViewById(R.id.unidade_item_address);
        }

    }

    public UnidadeAdapter(List<Unidade> unidades){
        this.unidades = unidades;
    }

    public void setOnItemTouch(View.OnTouchListener onItemTouch){
        this.onItemTouch = onItemTouch;
    }

    @Override
    public UnidadeViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unidade_item, parent, false);

        UnidadeViewHolder holder = new UnidadeViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(UnidadeViewHolder holder, int position){
        UnidadeViewHolder vHolder = holder;

        holder.itemView.setClickable(true);
        vHolder.itemView.setOnTouchListener(onItemTouch);

        Unidade unidade = unidades.get(position);

        vHolder.setId(unidade.get_id());
        vHolder.nome.setText(unidade.getNome());
        vHolder.address.setText(unidade.getLocation().getAddress());

    }

    @Override
    public int getItemCount(){
        return unidades.size();
    }
}
