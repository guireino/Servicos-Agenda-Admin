package com.example.servicosagendaadministrador.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.servicosagendaadministrador.R;
import com.example.servicosagendaadministrador.modelo.Agendamento;
import com.example.servicosagendaadministrador.util.DialogProgress;
import com.example.servicosagendaadministrador.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlterarRemoverServicoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTxt_Nome, editTxt_NumeroContato, editTxt_Email;
    private CheckBox checkBox_WhatsApp, checkBox_Barba, checkBox_Cabelo;
    private CardView cardView_Alterar, cardView_Remover;

    private FirebaseDatabase database;
    private DatabaseReference reference;

    private ValueEventListener valueEventListener;

    private Agendamento agendamento;

    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alterar_remove_servico);

        data = getIntent().getStringArrayListExtra("data");

        database = FirebaseDatabase.getInstance();

        editTxt_Nome = findViewById(R.id.edTxt_AlterarRemover_Nome);
        editTxt_NumeroContato = findViewById(R.id.editTxt_AlterarRemover_Numero);
        editTxt_Email = findViewById(R.id.editTxt_AlterarRemover_Email);
        checkBox_WhatsApp = findViewById(R.id.checkbox_AlterarRemover_WhatsApp);
        checkBox_Barba = findViewById(R.id.checkbox_AlterarRemover_Barba);
        checkBox_Cabelo = findViewById(R.id.checkbox_AlterarRemover_Cabelo);
        cardView_Alterar = findViewById(R.id.cardView_AlterarRemover_Alterar);
        cardView_Remover = findViewById(R.id.cardView_AlterarRemover_Remover);

        cardView_Alterar.setOnClickListener(this); // add click cardView
        cardView_Remover.setOnClickListener(this);

        obterDadosAgendamento();
    }

    // ======================================= ACAO DE CLICK =======================================

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cardView_AlterarRemover_Alterar:

                //Toast.makeText(getBaseContext(), "Botao Alterar", Toast.LENGTH_LONG).show();
                validarCampos();

            break;

            case R.id.cardView_AlterarRemover_Remover:

                //Toast.makeText(getBaseContext(), "Botao Remover.", Toast.LENGTH_LONG).show();
                removerAgendamento();

            break;
        }
    }

    // ======================================= OBTER DADOS DO SERVICO =======================================

    private void obterDadosAgendamento() {

        //O caminho do firebase onde esta bd e dados com informacao do cliente
        reference = database.getReference().child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia")
                .child(data.get(0)).child(data.get(3));

        if(valueEventListener == null){

            valueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    if(snapshot.exists()){

                        Agendamento agendamentoCliente = snapshot.getValue(Agendamento.class);
                        agendamento = agendamentoCliente;

                        updateDados(agendamento);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };

            reference.addValueEventListener(valueEventListener);
        }
    }

    private void updateDados(Agendamento agendamento) {

        editTxt_Nome.setText(agendamento.getNome());
        editTxt_NumeroContato.setText(agendamento.getContato());
        editTxt_Email.setText(agendamento.getEmail());
        checkBox_WhatsApp.setChecked(agendamento.isWhatsApp());
        checkBox_Cabelo.setChecked(agendamento.isCabelo());
        checkBox_Barba.setChecked(agendamento.isBarba());
    }

    // ======================================= AGENDAR NO FIREBASE =======================================

    private void validarCampos(){

        String nome = editTxt_Nome.getText().toString();
        String contato = editTxt_NumeroContato.getText().toString();
        String email = editTxt_Email.getText().toString();

        boolean whatsApp = checkBox_WhatsApp.isChecked();
        boolean barba = checkBox_Barba.isChecked();
        boolean cabelo = checkBox_Cabelo.isChecked();

        //verificar se variaveis esta vazias
        if(!nome.isEmpty()){

            if(!cabelo && !barba){
                Toast.makeText(getBaseContext(), "Escolha qual serviço gostaria de agenda.", Toast.LENGTH_LONG).show();
            }else{

                if(Util.statusInternet_MoWi(getBaseContext())){

                    if (agendamento.getNome().equals(nome) && agendamento.getContato().equals(contato)
                            && agendamento.isWhatsApp() == whatsApp && agendamento.getEmail().equals(email)
                            && agendamento.isBarba() == barba && agendamento.isCabelo() == cabelo){

                        Toast.makeText(getBaseContext(), "Você não alterou nenhuma informação.", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getBaseContext(), "Podemos fazer alteracao", Toast.LENGTH_LONG).show();
                    }

                    //agendamento

                    alterarDadosAgendamento(nome, contato, email, whatsApp, barba, cabelo);

                    //Toast.makeText(getBaseContext(), "Nome: " + nome + "\nContato: " + contato +
                             // "\nEmail: " + email, Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getBaseContext(), "Erro - Verifique sua conexão com a internet", Toast.LENGTH_LONG).show();
                }
            }

        }else{
            Toast.makeText(getBaseContext(), "Insira seu nome para Agendar.", Toast.LENGTH_LONG).show();
        }
    }

    private void alterarDadosAgendamento(String nome, String contato, String email, boolean whatsApp, boolean barba, boolean cabelo) {

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        Agendamento dadosAgendamento = new Agendamento(nome, contato, email, whatsApp, barba, cabelo);

        //O caminho do firebase onde esta bd e dados com informacao do cliente
        DatabaseReference databaseReference = database.getReference().child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia").child(data.get(0));

        Map<String, Object> updates = new HashMap<>();

        updates.put(data.get(3), dadosAgendamento);

        databaseReference.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Sucesso ao alterar dados", Toast.LENGTH_LONG).show();
                }else{
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao alterar dados", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // ======================================= REMOVER NO FIREBASE =======================================

    private void removerAgendamento() {

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getSupportFragmentManager(), "");

        //O caminho do firebase onde esta bd e dados com informacao do cliente
        DatabaseReference databaseReference = database.getReference().child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia").child(data.get(0));

        databaseReference.child(data.get(3)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){

                    Toast.makeText(getBaseContext(), "Sucesso ao remover Agendamento", Toast.LENGTH_LONG).show();
                    dialogProgress.dismiss();
                    finish();
                }else{
                    dialogProgress.dismiss();
                    Toast.makeText(getBaseContext(), "Erro ao remover Agendamento", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(valueEventListener != null){
            reference.removeEventListener(valueEventListener);
            valueEventListener = null;
        }
    }
}