package com.example.taller3.controladores;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Interfaz {

    public static int dp(Context contexto, int valor) {
        DisplayMetrics metricas = contexto.getResources().getDisplayMetrics();

        return Math.round(valor * metricas.density);
    }

    public static void aplicarMargenes(View raiz, boolean conTeclado) {
        ViewCompat.setOnApplyWindowInsetsListener(raiz, (vista, insets) -> {
            Insets barras = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());

            int abajo = barras.bottom;

            if (conTeclado) {
                abajo = Math.max(abajo, insets.getInsets(WindowInsetsCompat.Type.ime()).bottom);
            }

            vista.setPadding(barras.left, barras.top, barras.right, abajo);

            return WindowInsetsCompat.CONSUMED;
        });
    }
}
