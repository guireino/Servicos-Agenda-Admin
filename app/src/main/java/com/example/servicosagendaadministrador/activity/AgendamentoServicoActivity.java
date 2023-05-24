package com.example.servicosagendaadministrador.activity;

import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AgendamentoServicoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTxt_Nome, editView_NumeroContato, editView_Email;
    private CheckBox checkBox_WhatsApp, checkBox_Barba, checkBox_Cabelo;
    private CardView cardView_Agendar;

    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento_servico);

        data = getIntent().getStringArrayListExtra("data");

        //Toast.makeText(getBaseContext(), "Ano: " + data.get(2) + "\nMes: " + data.get(1)
        //        + "\nDia: " + data.get(0) + "\nHorario: " + data.get(3), Toast.LENGTH_LONG).show();

        editTxt_Nome = findViewById(R.id.edTxt_Agendamento_Nome);
        editView_NumeroContato = findViewById(R.id.editTxt_AgendamentoServico_Numero);
        editView_Email = findViewById(R.id.editTxt_AgendamentoServico_Email);
        checkBox_WhatsApp = findViewById(R.id.checkbox_AgendamentoServico_WhatsApp);
        checkBox_Barba = findViewById(R.id.checkbox_AgendamentoServico_Barba);
        checkBox_Cabelo = findViewById(R.id.checkbox_AgendamentoServico_Cabelo);
        cardView_Agendar = findViewById(R.id.cardView_AgendamentoServico_Agendar);

        cardView_Agendar.setOnClickListener(this); // add click cardView

    }

    // ======================================= ACAO DE CLICK =======================================

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cardView_AgendamentoServico_Agendar:

                //Toast.makeText(getBaseContext(), "Agendar", Toast.LENGTH_LONG).show();
                agendar();

            break;
        }
    }

    // ======================================= AGENDAR NO FIREBASE =======================================

    private void agendar(){

        String nome = editTxt_Nome.getText().toString();
        String contato = editView_NumeroContato.getText().toString();
        String email = editView_Email.getText().toString();

        boolean whatsApp = checkBox_WhatsApp.isChecked();
        boolean barba = checkBox_Barba.isChecked();
        boolean cabelo = checkBox_Cabelo.isChecked();

        //verificar se variaveis esta vazias
        if(!nome.isEmpty()){

            if(!cabelo && !barba){
                Toast.makeText(getBaseContext(), "Escolha qual serviço gostaria de agenda.", Toast.LENGTH_LONG).show();
            }else{

                if(Util.statusInternet_MoWi(getBaseContext())){

                    //agendamento

                    agendarFirebase(nome, contato, email, whatsApp, barba, cabelo);

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

    private void agendarFirebase(String nome, String contato, String email, boolean whatsApp, boolean barba, boolean cabelo) {

       Agendamento agendamento = new Agendamento(nome, contato, email, whatsApp, barba, cabelo);

       //Agendamento agendamento = new Agendamento(nome, contato, email, whatsApp, barba, cabelo);

       DialogProgress dialogProgress = new DialogProgress();
       dialogProgress.show(getSupportFragmentManager(), "dialog");

       FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

       // espeseficando onde vai salvar informacao
       DatabaseReference reference = firebaseDatabase.getReference()
                .child("DB").child("Calendario").child("HorariosAgendados")
                .child(data.get(2)).child("Mes").child(data.get(1)).child("dia").child(data.get(0));

       reference.child(data.get(3)).setValue(agendamento).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {

               if(task.isSuccessful()){
                   dialogProgress.dismiss();
                   Toast.makeText(getBaseContext(), "Sucesso ao Agendar.", Toast.LENGTH_LONG).show();
               }else{
                   dialogProgress.dismiss();
                   Toast.makeText(getBaseContext(), "Falha ao Agendar.", Toast.LENGTH_LONG).show();
               }
           }
       });
    }

}