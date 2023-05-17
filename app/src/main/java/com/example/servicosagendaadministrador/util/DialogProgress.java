package com.example.servicosagendaadministrador.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.example.servicosagendaadministrador.R;
import com.google.firebase.database.annotations.Nullable;


public class DialogProgress extends DialogFragment {

    public DialogProgress(){

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.dialog_progress,container);
        setRetainInstance(true);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setCancelable(false);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
}