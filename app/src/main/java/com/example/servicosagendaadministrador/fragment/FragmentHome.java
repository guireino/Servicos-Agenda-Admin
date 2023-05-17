package com.example.servicosagendaadministrador.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.servicosagendaadministrador.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
                Toast.makeText(getContext(), "Abrir Galaria de Imagens", Toast.LENGTH_LONG).show();
            break;

            case R.id.cardView_Home_AlterarDados:
                Toast.makeText(getContext(), "Alterar Dados", Toast.LENGTH_LONG).show();
            break;
        }
    }

    //---------------------------------------OUVINTE FIREBASE--------------------------------------------------

    private void ouvinte(){

        if(valueEventListener == null){

            valueEventListener = new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    if(dataSnapshot.exists()){

                        // pegando as referencia dos valores no bd do firebase
                        String imagemUrl = dataSnapshot.child("imagemUrl").getValue(String.class);
                        String info = dataSnapshot.child("informacao").getValue(String.class);
                        String latitude = dataSnapshot.child("latitude").getValue(String.class);
                        String longitude = dataSnapshot.child("longitude").getValue(String.class);
                        String numeroContato = dataSnapshot.child("numeroContato").getValue(String.class);
                        String valorServico = dataSnapshot.child("valorServico").getValue(String.class);

                        Log.i("ouvinte latitude", "ouvinte latitude: " + latitude);

                        if(!info.isEmpty() && !valorServico.isEmpty()){
                            updateDados(imagemUrl, info, latitude, longitude, numeroContato, valorServico);
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

    private void updateDados(String imagemUrl, String info, String latitudeFirebase, String longitudeFirebase,
                                        String numeroContato, String valorServico){

        // add info que estal bd do firebase nas variavies
        editTxt_Info.setText(info);
        editTxt_ValorServico.setText(valorServico);
        editTxt_Contato.setText(numeroContato);

        Glide.with(getContext()).asBitmap().load(imagemUrl).listener(new RequestListener<Bitmap>() {
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