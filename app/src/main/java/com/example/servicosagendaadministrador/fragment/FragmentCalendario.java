package com.example.servicosagendaadministrador.fragment;

import static android.widget.Toast.LENGTH_LONG;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.servicosagendaadministrador.R;
import com.example.servicosagendaadministrador.activity.HorariosActivity;
import com.example.servicosagendaadministrador.util.Util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCalendario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCalendario extends Fragment implements CalendarView.OnDateChangeListener {

    private CalendarView calendarView;

    private int diaAtual, mesAtual, anoAtual;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentCalendario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCalendario.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCalendario newInstance(String param1, String param2) {
        FragmentCalendario fragment = new FragmentCalendario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);

        abterDataAtual();

        calendarView.setOnDateChangeListener(this);

        versionLollipop();

        // Inflate the layout for this fragment
        return view;
    }

    // ----------------------------------------- OBTER DATA ATUAL --------------------------------------

    private void abterDataAtual() {

        long dataLong = calendarView.getDate(); //123456465465213

        // obter o tipo de configuracao para formata data
        Locale locale = new Locale("pt", "BR");

        SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
        SimpleDateFormat mes = new SimpleDateFormat("MM", locale);
        SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

        //SimpleDateFormat data = new SimpleDateFormat("ddd/MM/yyyy", locale);

        diaAtual = Integer.parseInt(dia.format(dataLong));
        //diaAtual = 30;
        mesAtual = Integer.parseInt(mes.format(dataLong));
        anoAtual = Integer.parseInt(ano.format(dataLong));

        //Toast.makeText(getContext(), "Dia: " + diaAtual + "\nMes: " + mesAtual + "\nAno: " + anoAtual, LENGTH_LONG).show();
    }

    // ----------------------------------------- CLICK CALENDARIO --------------------------------------

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

        // janeiro = 0
        // fevereiro = 1

        // somando +1 para mes fica numeracao correta, pq mes no CalendarView comeca com valor 0
        int mes = month + 1;

        dataSelecionado(dayOfMonth, mes, year);

        //Toast.makeText(getContext(), "Dia: " + diaAtual + "\nMes: " + mesAtual + "\nAno: " + anoAtual, LENGTH_LONG).show();
    }

    private void dataSelecionado(int diaSelected, int mesSelected, int anoSelected) {

        Locale locale = new Locale("pt", "BR");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);

        Calendar data = Calendar.getInstance();

        try{

            data.setTime(simpleDateFormat.parse(diaSelected + "/" + mesSelected + "/" + anoSelected));

                //Toast.makeText(getContext(), "Você pode agendar nesse dia", LENGTH_LONG).show();

            if(Util.statusInternet_MoWi(getContext())){

               String diaDisponive = String.valueOf(diaSelected);
               String mesDisponive = String.valueOf(mesSelected);
               String anoDisponive = String.valueOf(anoSelected);

               ArrayList<String> dataList = new ArrayList<String>();

               dataList.add(diaDisponive); // position 0
               dataList.add(mesDisponive); // position 1
               dataList.add(anoDisponive); // position 2

               // chamar a nossa proxima activity
               //Intent intent = new Intent(getContext(), HorariosActivity.class);
               //intent.putExtra("data", dataList);

               //startActivity(intent);

            }else{
                Toast.makeText(getContext(), "Erro - Sem conexão com a internet", LENGTH_LONG).show();
            }

        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    // ----------------------------------------- VERSAO ANTIGAS ANDROID --------------------------------------

    private void versionLollipop(){

        int versao = Build.VERSION.SDK_INT; // pegando versao atual do android

        if(versao <= Build.VERSION_CODES.LOLLIPOP){
            WindowManager windowManager = (WindowManager)getActivity().getSystemService(getActivity().WINDOW_SERVICE);

            Display display = windowManager.getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            int width = size.x;

            if(width == 480){

                // tamanho calendario no celular com resolucao 480
                calendarView.getLayoutParams().width = 730;
                calendarView.getLayoutParams().height = 500;
            }else if(width == 800){
                calendarView.getLayoutParams().width = 800;
                calendarView.getLayoutParams().height = 650;
            }else{
                // version mais novas do android. Nao preicisa configurar manualmente o calendario.
            }

        }
    }
}