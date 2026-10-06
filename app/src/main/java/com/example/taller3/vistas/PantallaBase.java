package com.example.taller3.vistas;

import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.taller3.R;
import com.example.taller3.controladores.ConexionHttpPostServer;
import com.example.taller3.controladores.Interfaz;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public abstract class PantallaBase extends AppCompatActivity {

    protected List<Pair<String, String>> parametros(String... pares) {
        List<Pair<String, String>> lista = new ArrayList<>();

        for (int i = 0; i < pares.length; i += 2) {
            lista.add(ConexionHttpPostServer.parametro(pares[i], pares[i + 1]));
        }

        return lista;
    }

    protected String enviarPeticion(String... pares) {
        try {
            return new ConexionHttpPostServer()
                    .conexionConElServidor(parametros(pares), ConexionHttpPostServer.urlDeLaServidor(this));
        } catch (Exception e) {
            return null;
        }
    }

    protected void confirmarDesprender(String mensaje, Runnable alConfirmar) {
        LinearLayout lamina = new LinearLayout(this);
        lamina.setOrientation(LinearLayout.VERTICAL);

        int lateral = Interfaz.dp(this, 24);
        lamina.setPadding(lateral, Interfaz.dp(this, 8), lateral, Interfaz.dp(this, 8));

        TextView titulo = texto(0, R.style.TextAppearance_Taller3_Sello, R.color.md_on_surface);
        titulo.setText(R.string.dialogo_eliminar_titulo);
        lamina.addView(titulo);

        LineaPerforada linea = new LineaPerforada(this);
        LinearLayout.LayoutParams medidas = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, Interfaz.dp(this, 3));
        medidas.topMargin = Interfaz.dp(this, 14);
        medidas.bottomMargin = Interfaz.dp(this, 14);
        linea.setLayoutParams(medidas);
        lamina.addView(linea);

        TextView cuerpo = texto(0, R.style.TextAppearance_Taller3_Mono, R.color.md_on_surface_variant);
        cuerpo.setText(getString(R.string.dialogo_eliminar_mensaje, mensaje));
        lamina.addView(cuerpo);

        new MaterialAlertDialogBuilder(this)
                .setView(lamina)
                .setNegativeButton(R.string.dialogo_cancelar, null)
                .setPositiveButton(R.string.dialogo_eliminar_confirmar, (dialogo, cual) -> alConfirmar.run())
                .show();
    }

    protected TextView texto(int recurso, int estilo, int color) {
        TextView vista = new TextView(this);

        if (recurso != 0) {
            vista.setText(recurso);
        }

        vista.setTextAppearance(estilo);
        vista.setTextColor(ContextCompat.getColor(this, color));

        return vista;
    }

    protected void mostrarHoja(View raiz, boolean conTeclado) {
        Interfaz.aplicarMargenes(raiz, conTeclado);
    }
}
