package com.example.servicosagendaadministrador.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicosagendaadministrador.R;

import java.util.List;

public class AdapterRecyclerView extends RecyclerView.Adapter<AdapterRecyclerView.ViewHolder> {

    private Context context;
    private List<String> horarios;
    private ClickItemRecyclerView clickItemRecyclerView;

    public AdapterRecyclerView(Context context, List<String> horarios, ClickItemRecyclerView clickItemRecyclerView){
        this.context = context;
        this.horarios = horarios;
        this.clickItemRecyclerView = clickItemRecyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final String horario = horarios.get(position);

        holder.textView.setText(horario);

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemRecyclerView.clickItem(horario, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return horarios.size();
    }

    // usamos interface para poder implementar no metodo click
    public interface ClickItemRecyclerView{
        void clickItem(String horario, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardView_RecyclerView_Item);
            textView = itemView.findViewById(R.id.txtView_RecyclerView_Item);

        }
    }
}