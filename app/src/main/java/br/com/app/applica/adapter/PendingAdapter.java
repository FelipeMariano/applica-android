package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.app.applica.R;
import br.com.app.applica.entitity.Pendencia;

/**
 * Created by felipe on 17/11/16.
 */
public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter .PendingViewHolder> {
    private List<Pendencia> pendings;

    public static class PendingViewHolder extends RecyclerView.ViewHolder{
        private String id;
        private final TextView emailOrigem;
        //private final TextView nomeUserOrigem;
        //private final TextView cardenetaToShare;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public PendingViewHolder(final View itemView){
            super(itemView);
            itemView.setTag(this);

            emailOrigem = (TextView) itemView.findViewById(R.id.share_email_origem);
        }
    }

    public PendingAdapter(List<Pendencia> pendings){
        this.pendings = pendings;
    }

    @Override
    public PendingViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pendencia_item, parent, false);

        PendingViewHolder holder = new PendingViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(PendingViewHolder holder, int position) {
        PendingViewHolder vHolder = holder;

        Pendencia pendencia = pendings.get(position);

        vHolder.setId(pendencia.get_id());
        vHolder.emailOrigem.setText(pendencia.getEmailOrigem());

    }

    @Override
    public int getItemCount() {
        return pendings.size();
    }
}