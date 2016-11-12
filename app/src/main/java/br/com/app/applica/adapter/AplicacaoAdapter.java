package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import br.com.app.applica.R;
import br.com.app.applica.entitity.Aplicacao;

/**
 * Created by felipe on 30/10/16.
 */
public class AplicacaoAdapter extends RecyclerView.Adapter<AplicacaoAdapter .AplicacaoViewHolder> implements Filterable{
    private List<Aplicacao> aplicacoes;
    private List<Aplicacao> filteredAplicacoes;
    private AplicacaoFilter aplicacaoFilter;
    private static String LOG_TAG = "AplicacaoAdapter";
    private static MyClickListener myClickListener;



    public static class AplicacaoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private String id;
        public final TextView data;
        public final TextView vacina;
        public final TextView dose;

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
            vacina = (TextView) itemView.findViewById(R.id.aplicacao_item_vacina);
            dose = (TextView) itemView.findViewById(R.id.aplicacao_item_dose);
            Log.i(LOG_TAG, "Adding Listener");
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getPosition(), v);
        }
    }

    public AplicacaoAdapter(List<Aplicacao> aplicacoes){
        this.aplicacoes = aplicacoes;
        this.filteredAplicacoes = new ArrayList<>();
    }



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
       // vHolder.data.setText(aplicacao.get_id());

        vHolder.data.setText("Data: " + aplicacao.getFormattedData());
        vHolder.dose.setText(aplicacao.getDose());
        vHolder.vacina.setText(aplicacao.getVacina());
    }


    @Override
    public Filter getFilter(){
        if(aplicacaoFilter == null)
            aplicacaoFilter = new AplicacaoFilter(this, aplicacoes);

        return aplicacaoFilter;
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


    private static class AplicacaoFilter extends Filter{
        private final AplicacaoAdapter adapter;
        private final List<Aplicacao> originalList;
        private final List<Aplicacao> filteredList;

        private AplicacaoFilter(AplicacaoAdapter adapter, List<Aplicacao> originalList){
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint){
            filteredList.clear();
            final FilterResults results = new FilterResults();

            if(constraint.length() == 0){
                filteredList.addAll(originalList);
            }else{
                final String filterPattern = constraint.toString().toLowerCase().trim();

                for(final Aplicacao aplicacao : originalList){
                    System.out.println("FILTERING: " + aplicacao.getEfetivada());
                    if(aplicacao.getEfetivada()){
                        filteredList.add(aplicacao);
                    }
                }
            }

            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results){
            adapter.filteredAplicacoes.clear();
            adapter.filteredAplicacoes.addAll((ArrayList<Aplicacao>) results.values);
            adapter.notifyDataSetChanged();
        }
    }

}
