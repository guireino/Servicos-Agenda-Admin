package com.example.servicosagendaadministrador.fragment;

import android.app.Activity;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.servicosagendaadministrador.R;
import com.example.servicosagendaadministrador.modelo.Empresa;
import com.example.servicosagendaadministrador.util.DialogProgress;
import com.example.servicosagendaadministrador.util.Util;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements View.OnClickListener {

    private ImageView imgView;
    private ProgressBar progressBar;

    private EditText editTxt_Info, editTxt_ValorServico, editTxt_Contato;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    private CardView cardView_AlterarDados;

    private Empresa empresaDados = new Empresa();
    private Uri uri = null;

    //private String latitude, longitude;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();

        //buscando caminho do bd
        reference = database.getReference().child("DB").child("Home").child("Dados");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgView = (ImageView) view.findViewById(R.id.imgView_Home);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_Home);

        editTxt_Info = view.findViewById(R.id.editTxt_Home_Info);
        editTxt_Contato = view.findViewById(R.id.editView_Home_NumeroContatos);
        editTxt_ValorServico = view.findViewById(R.id.editTxt_Home_Valor);

        cardView_AlterarDados = view.findViewById(R.id.cardView_Home_AlterarDados);

        // fazendo com o que ficar click
        imgView.setOnClickListener(this);
        cardView_AlterarDados.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imgView_Home:
                //Toast.makeText(getContext(), "Abrir Galaria de Imagens", Toast.LENGTH_LONG).show();
                obterImage_Galeria();
            break;

            case R.id.cardView_Home_AlterarDados:
                //Toast.makeText(getContext(), "Alterar Dados", Toast.LENGTH_LONG).show();
                validarDadosAlterados();
            break;
        }
    }

    //--------------------------------------- OBTER IMAGENS GALERIA --------------------------------------------------

    private void obterImage_Galeria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //startActivityForResult(Intent.createChooser(intent, "Escolhe uma imagem"), 10);
        openActivity.launch(intent);
    }

    //--------------------------------------- ALTERAR DADOS -----------------------------------------------------

    private void validarDadosAlterados(){

        String info = editTxt_Info.getText().toString();
        String valor = editTxt_ValorServico.getText().toString();
        String numero = editTxt_Contato.getText().toString();

        if(Util.statusInternet_MoWi(getContext())){

            if(!info.isEmpty() && !valor.isEmpty() && !numero.isEmpty()){

                if (empresaDados.getInformacao().equals(info) &&
                        empresaDados.getValorServico().equals(valor) && empresaDados.getNumeroContato().equals(numero) && uri == null){
                    Toast.makeText(getContext(), "Você não alterou nenhuma informação", Toast.LENGTH_LONG).show();
                }else{

                    //Toast.makeText(getContext(), "Gravar Dados", Toast.LENGTH_LONG).show();

                    String imgUrl = empresaDados.getImagemUrl();
                    String latitude = empresaDados.getLatitude();
                    String longitude = empresaDados.getLongitude();

                    if(uri != null){ // verificando se image esta null
                        //Toast.makeText(getContext(), "Altera a imagem", Toast.LENGTH_LONG).show();
                        alterarImgStorage(info, valor, numero, latitude, longitude);
                    }else{

                        //Toast.makeText(getContext(), "Altera a somente os textos", Toast.LENGTH_LONG).show();
                        alterarDadosDatabase(info, valor, numero, imgUrl, latitude, longitude);
                    }
                }

            }else{
                Toast.makeText(getContext(), "Preencha todos os campos obrigatório", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getContext(), "Erro - Verifique se você possui alguma conexão com a Internet", Toast.LENGTH_LONG).show();
        }
    }

    //--------------------------------------- ALTERAR IMAGEM NO STORAGE -----------------------------------------------------

    private void alterarImgStorage(final String info,final String valor,final String numero,final String latitude,final String longitude) {

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getChildFragmentManager(), "");

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // nome da pasta no bd firebase
        StorageReference storageReference = storage.getReference().child("imagemlogo");
        // nome da imagem no bd firebase
        StorageReference nomeImg = storageReference.child("LogoEmpresa" + System.currentTimeMillis() + ".png");

        // uploadTask e que vai fazer conunicacao app para firebase
        UploadTask uploadTask = nomeImg.putFile(uri);

        //fazendo upload imagem
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){

                    //Toast.makeText(getContext(), "Upload Ok", Toast.LENGTH_LONG).show();
                    nomeImg.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            if(task.isSuccessful()){

                                dialogProgress.dismiss();

                                //pegando url da imagem
                                String urlImagem = task.getResult().toString();

                                alterarDadosDatabase(info, valor, numero, urlImagem, latitude, longitude);

                                //Toast.makeText(getContext(), urlImagem, Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                }else{
                    dialogProgress.dismiss();
                    Toast.makeText(getContext(), "Falha no Upload", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //--------------------------------------- ALTERAR DADOS DATABASE -----------------------------------------------------

    private void alterarDadosDatabase(String info,String valor, String numero, String imgUrl, String latitude, String longitude) {

        Empresa empresa = new Empresa(imgUrl, info, latitude, longitude, numero, valor); // pegando os valores class empresa

        DialogProgress dialogProgress = new DialogProgress();
        dialogProgress.show(getChildFragmentManager(), "");

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference databaseReference = database.getReference().child("DB").child("Home");

        // atualizando dados bd firebase
        Map<String, Object> update = new HashMap<>();
        update.put("Dados", empresa);

        databaseReference.updateChildren(update).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    dialogProgress.dismiss();
                    Toast.makeText(getContext(), "Sucesso ao Realizar Alteração", Toast.LENGTH_LONG).show();
                }else{
                    dialogProgress.dismiss();
                    Toast.makeText(getContext(), "Erro ao Realizar Alteração", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //--------------------------------------- OUVINTE FIREBASE --------------------------------------------------

    private void ouvinte(){

        if(valueEventListener == null){

            valueEventListener = new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    if(dataSnapshot.exists()){

                        // pegando as referencia dos valores no class enpresa
                        Empresa empresa = dataSnapshot.getValue(Empresa.class);

                        //atribuindo os valores
                        empresaDados = empresa;

                        if(!empresa.getInformacao().isEmpty() && !empresa.getValorServico().isEmpty()){
                            updateDados(empresa);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError){

                }
            };

            reference.addValueEventListener(valueEventListener);
        }
    }

    private void updateDados(Empresa empresa){

        // add info que estal bd do firebase nas variavies
        editTxt_Info.setText(empresa.getInformacao());
        editTxt_ValorServico.setText(empresa.getValorServico());
        editTxt_Contato.setText(empresa.getNumeroContato());

        Glide.with(getContext()).asBitmap().load(empresa.getImagemUrl()).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                Toast.makeText(getContext(), "Erro ao realizar Donwload de imagem", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                        DataSource dataSource, boolean isFirstResource) {

                progressBar.setVisibility(View.GONE);

                return false;
            }

        }).into(imgView);
    }

    //--------------------------------------- Activity Result Launcher --------------------------------------------------

    ActivityResultLauncher<Intent> openActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {

                if(result.getResultCode() == Activity.RESULT_OK){

                    Intent data = result.getData();
                    int requestCode = 10;

                    if(requestCode == 10){

                        if(data != null){

                            Uri uriImg = data.getData();
                            uri = uriImg;

                            Glide.with(getContext()).asBitmap().load(uriImg).listener(new RequestListener<Bitmap>() {

                                @Override
                                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                                    Toast.makeText(getContext(), "Erro ao selecionar imagem", Toast.LENGTH_LONG).show();

                                    return false;
                                }

                                @Override
                                public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                    return false;
                                }
                            }).into(imgView);
                        }
                    }
                }
            }
    );

    //---------------------------------------CICLO DE VIDA FRAGMENT--------------------------------------------

    @Override
    public void onStart(){
        super.onStart();

        ouvinte();
    }

    @Override
    public void onDestroy() {

        if(valueEventListener != null){
            reference.removeEventListener(valueEventListener);
            valueEventListener = null;
        }

        super.onDestroy();
    }
}