package br.com.app.applica.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import br.com.app.applica.R;
import br.com.app.applica.entitity.Pendencia;

/**
 * Created by felipe on 17/11/16.
 */
public class PendingAdapter extends RecyclerView.Adapter<PendingAdapter .PendingViewHolder> {
    private List<Pendencia> pendings;
    private static AcceptClickListener acceptClickListener;
    private static RejectClickListener rejectClickListener;

    public static class PendingViewHolder extends RecyclerView.ViewHolder{
        private String id;
        private final TextView emailOrigem;
        private final TextView nomeUserOrigem;
        private final TextView cardenetaToShare;
        private final Button btnAccept;
        private final Button btnReject;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public PendingViewHolder(final View itemView){
            super(itemView);
            itemView.setTag(this);
            nomeUserOrigem = (TextView) itemView.findViewById(R.id.share_nome_origem);
            cardenetaToShare = (TextView) itemView.findViewById(R.id.share_cardeneta);
            emailOrigem = (TextView) itemView.findViewById(R.id.share_email_origem);
            btnAccept = (Button) itemView.findViewById(R.id.pending_accept);
            btnReject = (Button) itemView.findViewById(R.id.pending_reject);

            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptClickListener.onAcceptClick(getPosition(), itemView);
                }
            });

            btnReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rejectClickListener.onRejectClick(getPosition(), itemView);
                }
            });
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
        final PendingViewHolder vHolder = holder;

        Pendencia pendencia = pendings.get(position);

        vHolder.setId(pendencia.get_id());
        vHolder.emailOrigem.setText(pendencia.getUser_origin().getEmail());
        vHolder.cardenetaToShare.setText(pendencia.getCardenetaNome());
        vHolder.nomeUserOrigem.setText("user name here");
    }

    @Override
    public int getItemCount() {
        return pendings.size();
    }

    public interface AcceptClickListener{
        public void onAcceptClick(int position,View v);
    }

    public interface RejectClickListener{
        public void onRejectClick(int position, View v);
    }

    public void setAcceptClickListener(AcceptClickListener acceptClickListener){
        this.acceptClickListener = acceptClickListener;
    }

    public void setRejectClickListener(RejectClickListener rejectClickListener){
        this.rejectClickListener = rejectClickListener;
    }
}
