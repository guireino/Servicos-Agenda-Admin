package com.example.servicosagendaadministrador;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import android.util.Log;

import com.example.servicosagendaadministrador.fragment.FragmentCalendario;
import com.example.servicosagendaadministrador.fragment.FragmentHome;
import com.example.servicosagendaadministrador.util.Permissao;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.annotations.Nullable;


public class MainActivity extends AppCompatActivity {

    private BottomNavigationView btmNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener;

    private FragmentHome fragmentHome;
    private FragmentCalendario fragmentCalendario;

    private Fragment fragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btmNavigationView = findViewById(R.id.bottomNavigationView);

        navigationBtm();

        fragmentHome = new FragmentHome();
        fragmentCalendario = new FragmentCalendario();

        fragmentManager = getSupportFragmentManager();

        // instanciando frament no mainActivity
        fragmentManager.beginTransaction().replace(R.id.frameLayout_Fragment, fragmentHome).commit();

        permissao();
    }

    private void permissao() {

        String permissoes[] = new String[]{ // buscando persicao para usuario
                Manifest.permission.GET_ACCOUNTS, Manifest.permission.READ_CONTACTS
        };

        // fazer com aparese para usuario tela as opcao para usuario permitir
        Permissao.validate(this, 333, permissoes);

    }

    private void navigationBtm(){

        // fazendo click funcionar navegacao botao
        onNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@Nullable MenuItem item) {

                switch (item.getItemId()){

                    case R.id.item_navegacao_home:
                        fragment = fragmentHome;
                        Log.d("fragment home", "fragment " + fragmentHome);
                        break;

                    case R.id.item_navegacao_calendario:
                        fragment = fragmentCalendario;
                        Log.d("fragmentCalendario ", "fragmentCalendario " + fragmentCalendario);
                        break;
                }

                // setando qual frameLayout que estiver ativo
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout_Fragment, fragment).commit();

                return true;
            }
        };

        btmNavigationView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}