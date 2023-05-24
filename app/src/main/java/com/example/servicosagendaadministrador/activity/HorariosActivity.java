package com.example.servicosagendaadministrador.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.servicosagendaadministrador.R;
import com.example.servicosagendaadministrador.adapter.AdapterRecyclerView;
import com.example.servicosagendaadministrador.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HorariosActivity extends AppCompatActivity implements AdapterRecyclerView.ClickItemRecyclerView {

    private RecyclerView recyclerView;
    private AdapterRecyclerView adapterRecyclerView;

    // variavel responsavel para armezenar informacao bd numa list
    private List<String> horarios = new ArrayList<>();
    private List<String> horariosTemp = new ArrayList<>();

    private ArrayList<String> data = new ArrayList<>();

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horarios);

        //horarios.add("7:00 Hrs");

        recyclerView = findViewById(R.id.recyclerView);

        database = FirebaseDatabase.getInstance();
        data = getIntent().getStringArrayListExtra("data");

        configurarRecyclerView();

        carregarHorarioFuncionamento();
        //buscarHorariosReservados();

        //Toast.makeText(this, "Informacoes da Nova Activity\n\nDia: "
                                  //  + data.get(0) + "\nMes: " + data.get(1) + "\nAno: " + data.get(2), Toast.LENGTH_LONG).show();
    }

    // ----------------------------------------------- CONFIGURAR RECYCLERVIEW -------------------------------------------------

    private void configurarRecyclerView(){

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapterRecyclerView = new AdapterRecyclerView(this, horarios, this);

        recyclerView.setAdapter(adapterRecyclerView);
    }

    // ----------------------------------------------- CARREGAR HORARIOS EMPRESA -----------------------------------------------

    private void carregarHorarioFuncionamento() {

        // buscando valores no bd do firebase
        DatabaseReference reference = database.getReference().child("DB").child("Calendario").child("HorariosFuncionamento");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                if(dataSnapshot.exists()){

                    // buscando todo os valores bd na pasta Calendario do firebase
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        String horario = snapshot.getValue(String.class);

                        horarios.add(horario);
                        horariosTemp.add(horario);
                    }

                    adapterRecyclerView.notifyDataSetChanged();
                    //adapterListView.notifyDataSetChanged();
                    buscarHorariosReservados();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ----------------------------------------------- BUSCA HORARIOS AGENDADOS -----------------------------------------------

    // esse metodo a chamado somente depois que o metodo acima for executado
    private void buscarHorariosReservados() {

        // buscando todos valores da bd firebase
        reference = database.getReference().child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia").child(data.get(0));

        if(childEventListener == null){

            childEventListener = new ChildEventListener() {

                // buscando todo os valores bd na pasta dia e seu horarios, onChildAdded vai ler pasta por pasta
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    String key = snapshot.getKey();  // buscar o nome pasta do bd firebase

                    //int index = 0;
                    int index = horarios.indexOf(key);

                    String horarioKey = key + " - Reservado";
                    horarios.set(index, horarioKey);

                    adapterRecyclerView.notifyDataSetChanged();

                    // horario temos 5 itens = horarios.get(1) == 08:00 Hrs
                    // horario temos 5 itens = horarios.get(0) == 07:00 Hrs
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    String key = snapshot.getKey();  // buscar o nome pasta do bd firebase

                    String horarioKey = key + " - Reservado";

                    //int index = 0;
                    int index = horarios.indexOf(horarioKey);

                    horarios.set(index, key);

                    adapterRecyclerView.notifyDataSetChanged();
                    //adapterListView.notifyDataSetChanged();
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            reference.addChildEventListener(childEventListener); // atualizar list
        }
    }

    // ----------------------------------------------- CLICK ITEM DA LISTA -----------------------------------------------


    @Override
    public void clickItem(String horario, int position) {

        if(Util.statusInternet_MoWi(getBaseContext())){
            consultarHorarioBancoDados(horario, position);
        }else{
            Toast.makeText(getBaseContext(), "Erro - Sem conexão com a Internet", Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(getBaseContext(), horario, Toast.LENGTH_LONG).show();
    }

    private void consultarHorarioBancoDados(String horario, int position){

        // buscando valores no bd do firebase
        DatabaseReference reference = database.getReference().child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia")
                .child(data.get(0)).child(horariosTemp.get(position));

        reference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                if(dataSnapshot.exists()){
                    Toast.makeText(getBaseContext(), "Ja esta reservaso - Exibir Dados do Cliente", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getBaseContext(), AlterarRemoverServicoActivity.class);

                    String horarioSelecionado = horariosTemp.get(position);

                    data.add(3, horarioSelecionado);

                    intent.putExtra("data", data);
                    startActivity(intent);

                }else{

                    Toast.makeText(getBaseContext(), "Agendamento Disponivel", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getBaseContext(), AgendamentoServicoActivity.class);

                    //data.add(dia); position 0
                    //data.add(mes); position 1
                    //data.add(ano); position 2
                    //data.add(horario); position 3

                    data.add(3, horario);

                    intent.putExtra("data", data); // inserino valor tela
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    // ======================================= OBTER EMAIL =======================================

    private String obterEmail() {

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccounts();

        for (Account account: accounts){

            String email = account.name; // email = test@gmail.com

            if(email.contains("@")){ // ele so vai cair nesse if se tiver @ escrito
                return email;
            }
        }

        return "";
    }


    // ----------------------------------------------- CLICO DE VIDA ACTIVITY -----------------------------------------------

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(childEventListener == null){
            reference.removeEventListener(childEventListener);
            childEventListener = null;
        }
    }
}