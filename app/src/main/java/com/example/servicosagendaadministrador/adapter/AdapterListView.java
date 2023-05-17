package com.example.servicosagendaadministrador.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.servicosagendaadministrador.R;

import java.util.List;

public class AdapterListView extends BaseAdapter {

    private Activity activity;
    private List<String> horarios;

    private ClickItemListView clickItemListView;

    public AdapterListView(Activity activity, List<String> horarios, ClickItemListView clickItemListView){

        this.activity = activity;
        this.horarios = horarios;

        this.clickItemListView = clickItemListView;
    }

    @Override
    public int getCount() { // metodo esta com informacao horarios
        return horarios.size();
    }

    @Override
    public Object getItem(int position) {
        return horarios.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //veculando layout com view
        View view = activity.getLayoutInflater().inflate(R.layout.listview_item, parent, false);

        TextView textView = (TextView)view.findViewById(R.id.txtView_ListView_Item);
        CardView cardView = (CardView)view.findViewById(R.id.cardView_ListView_Item);

        // pegando cada valor na sua posicao do textView
        textView.setText(horarios.get(position));

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickItemListView.clickItem(horarios.get(position), position);
            }
        });

        return view;
    }

    // essa interface e para vecular com class adapterlistView com horarioActivity
    public interface ClickItemListView{
        void clickItem(String horario, int position);
    }

}
