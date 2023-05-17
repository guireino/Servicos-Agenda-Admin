package com.example.servicosagendaadministrador.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.servicosagendaadministrador.R;
import com.example.servicosagendaadministrador.modelo.Agendamento;
import com.example.servicosagendaadministrador.util.DialogProgress;
import com.example.servicosagendaadministrador.util.Util;
import com.google.android.gms.auth.api.credentials.CredentialPickerConfig;
import com.google.android.gms.auth.api.credentials.Credentials;
import com.google.android.gms.auth.api.credentials.HintRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AgendamentoServicoActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editTxt_Nome;
    private TextView txtView_NumeroContato, txtView_Email;
    private CheckBox checkBox_WhatsApp, checkBox_Barba, checkBox_Cabelo;
    private CardView cardView_Agendar;

    //private GoogleApiClient googleApiClient_Numero;

    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento_servico);

        data = getIntent().getStringArrayListExtra("data");

        //Toast.makeText(getBaseContext(), "Ano: " + data.get(2) + "\nMes: " + data.get(1)
        //        + "\nDia: " + data.get(0) + "\nHorario: " + data.get(3), Toast.LENGTH_LONG).show();

        editTxt_Nome = findViewById(R.id.edTxt_Agendamento_Nome);
        txtView_NumeroContato = findViewById(R.id.txtView_AgendamentoServico_Numero);
        txtView_Email = findViewById(R.id.txtView_AgendamentoServico_Email);
        checkBox_WhatsApp = findViewById(R.id.checkbox_AgendamentoServico_WhatsApp);
        checkBox_Barba = findViewById(R.id.checkbox_AgendamentoServico_Barba);
        checkBox_Cabelo = findViewById(R.id.checkbox_AgendamentoServico_Cabelo);
        cardView_Agendar = findViewById(R.id.cardView_AgendamentoServico_Agendar);

        cardView_Agendar.setOnClickListener(this); // add click cardView

        /*
        // configuracao com metodo googleApiClient
        googleApiClient_Numero = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API).build();
        */

        obterNumeroContato();
        obterEmail();

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

    // ======================================= OBTER NUMERO TELEFONE =======================================

    private void obterNumeroContato() {

        // fazendo requisição para exibir na tela do celular para client escolhar numero
        HintRequest hintRequest = new HintRequest.Builder().setHintPickerConfig(
                new CredentialPickerConfig.Builder().setShowCancelButton(false).build())
                .setPhoneNumberIdentifierSupported(true).build();

        //PendingIntent intent = Auth.CredentialsApi.getHintPickerIntent(googleApiClient_Numero, hintRequest);

        // fazendo comunicação insterna com numero client
        PendingIntent intent = Credentials.getClient(this).getHintPickerIntent(hintRequest);

        try {
            // 123 codigo call conversa com galeria para abrir na tela do celular para client escolhar numero
            startIntentSenderForResult(intent.getIntentSender(), 123, null, 0, 0, 0);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    // ======================================= OBTER EMAIL =======================================

    private void obterEmail() {

        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts = accountManager.getAccounts();

        for (Account account: accounts){

            String email = account.name; // email = test@gmail.com

            if(email.contains("@")){ // ele so vai cair nesse if se tiver @ escrito
                txtView_Email.setText(email); // add text
                break; // break e para sair do for
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 123){
            if(resultCode == RESULT_OK){

                com.google.android.gms.auth.api.credentials.Credential credential = data.getParcelableExtra(
                        com.google.android.gms.auth.api.credentials.Credential.EXTRA_KEY);

                if(!credential.getId().isEmpty()){
                    txtView_NumeroContato.setText(credential.getId());
                }else{
                    Toast.makeText(getBaseContext(), "Escolha um numero de contato para poder continuar", Toast.LENGTH_LONG).show();
                }
            }else{ // se cliente nao escolher numero
                dialogNumeroContato();
            }
        }
    }

    private void dialogNumeroContato() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("Escolha Obrigatoria")
                .setMessage("Escolha um número de telefone para agendar um horário.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        obterNumeroContato();
                    }

                }).setNegativeButton("Sair", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        finish();
                    }
                });

        builder.show();
    }

    // ======================================= AGENDAR NO FIREBASE =======================================

    private void agendar(){

        String nome = editTxt_Nome.getText().toString();
        String contato = txtView_NumeroContato.getText().toString();
        String email = txtView_Email.getText().toString();

        boolean whatsApp = checkBox_WhatsApp.isChecked();
        boolean barba = checkBox_Barba.isChecked();
        boolean cabelo = checkBox_Cabelo.isChecked();

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

    // ======================================= METODOS DE GOOGLE =======================================


}