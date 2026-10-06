package com.example.taller3.controladores;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import androidx.core.content.ContextCompat;

import com.example.taller3.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class Sello {

    public static void recibido(View ancla, String veredicto) {
        mostrar(ancla, veredicto, false);
    }

    public static void rechazado(View ancla, String veredicto) {
        mostrar(ancla, veredicto, true);
    }

    private static void mostrar(View ancla, String veredicto, boolean rechazo) {
        Context contexto = ancla.getContext();

        Snackbar sello = Snackbar.make(ancla, veredicto.toUpperCase(contexto.getResources().getConfiguration().getLocales().get(0)),
                rechazo ? Snackbar.LENGTH_LONG : Snackbar.LENGTH_SHORT);
        sello.setTextColor(ContextCompat.getColor(contexto, rechazo ? R.color.md_error : R.color.sello_tinta));
        sello.setAction(null, null);

        View laminar = sello.getView();
        laminar.setBackgroundResource(rechazo ? R.drawable.fondo_sello_rechazado : R.drawable.fondo_sello);
        laminar.setElevation(0f);
        laminar.setPadding(laminar.getPaddingLeft(), laminar.getPaddingTop() + 12, laminar.getPaddingRight(),
                laminar.getPaddingBottom() + 12);
        laminar.setScaleX(0.94f);
        laminar.setScaleY(0.94f);
        laminar.animate().scaleX(1f).scaleY(1f).setDuration(180).setInterpolator(new DecelerateInterpolator()).start();

        sello.show();
    }
}
