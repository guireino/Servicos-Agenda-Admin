package com.example.servicosagendaadministrador.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.servicosagendaadministrador.R;
import com.example.servicosagendaadministrador.adapter.AdapterListView;
import com.example.servicosagendaadministrador.util.Util;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HorariosActivity extends AppCompatActivity implements AdapterListView.ClickItemListView {

    private ListView listView;
    private AdapterListView adapterListView;

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

        listView = (ListView)findViewById(R.id.listView);

        database = FirebaseDatabase.getInstance();

        data = getIntent().getStringArrayListExtra("data");

        configurarListView();

        carregarHorarioFuncionamento();
        //buscarHorariosReservados();

        //Toast.makeText(this, "Informacoes da Nova Activity\n\nDia: "
                                  //  + data.get(0) + "\nMes: " + data.get(1) + "\nAno: " + data.get(2), Toast.LENGTH_LONG).show();
    }

    // ----------------------------------------------- CONFIGURAR LISTVIEW -----------------------------------------------

    private void configurarListView(){

        //adapterlist que faz lista funcionar
        adapterListView = new AdapterListView(this, horarios, this);

        listView.setAdapter(adapterListView);
    }

    // ----------------------------------------------- CARREGAR HORARIOS EMPRESA -----------------------------------------------

    private void carregarHorarioFuncionamento() {

        // buscando valores no bd do firebase
        DatabaseReference reference = database.getReference().child("DB").child("Calendario").child("HorariosFuncionamento");

        reference.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                if(dataSnapshot.exists()){

                    // buscando todo os valores bd na pasta Calendario
                    for (DataSnapshot snapshot: dataSnapshot.getChildren()){

                        String horario = snapshot.getValue(String.class);

                        horarios.add(horario);
                        horariosTemp.add(horario);
                    }

                    adapterListView.notifyDataSetChanged();
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

                    adapterListView.notifyDataSetChanged();

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

                    adapterListView.notifyDataSetChanged();
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

    /*

    private void consultarHorarioSelecionado(String horario, int position) {

        if(horario.contains("Reservado")){
            Toast.makeText(getBaseContext(), "Ja existe um agendamento para esse horario", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getBaseContext(), "Agendamento Disponivel", Toast.LENGTH_LONG).show();
        }
    }

    */


    private void consultarHorarioBancoDados(String horario, int position){

        // buscando valores no bd do firebase
        DatabaseReference reference = database.getReference().child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia")
                .child(data.get(0)).child(horariosTemp.get(position));

        reference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                if(dataSnapshot.exists()){

                    String emailRecuperadoBD = dataSnapshot.child("email").getValue(String.class);

                    String emailDispositivoUsuario = obterEmail();

                    if(emailRecuperadoBD.equals(emailDispositivoUsuario)){
                        Toast.makeText(getBaseContext(), "O e-mail e Igual você pode alterar ou Remover os dados", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getBaseContext(), "Não foi você quem faz o agendamento", Toast.LENGTH_LONG).show();
                    }

                }else{
                    //Toast.makeText(getBaseContext(), "Agendamento Disponivel", Toast.LENGTH_LONG).show();

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