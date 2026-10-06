package com.example.taller3.vistas;

import android.content.Context;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taller3.R;
import com.example.taller3.controladores.Interfaz;
import com.example.taller3.datos.Usuario;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.FilaUsuario> {

    public interface Escucha {
        void alPulsarFila(Usuario usuario);
    }

    private final List<Usuario> usuarios = new ArrayList<>();
    private Escucha escucha;

    public void setEscucha(Escucha escucha) {
        this.escucha = escucha;
    }

    public void actualizar(List<Usuario> nuevos) {
        usuarios.clear();
        usuarios.addAll(nuevos);
        notifyDataSetChanged();
    }

    public List<Usuario> usuarios() {
        return usuarios;
    }

    @NonNull
    @Override
    public FilaUsuario onCreateViewHolder(@NonNull ViewGroup padre, int tipo) {
        return new FilaUsuario(padre.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull FilaUsuario fila, int posicion) {
        Usuario usuario = usuarios.get(posicion);

        fila.txtNombre.setText(usuario.getNombre());
        fila.txtCorreo.setText(usuario.getEmail());
        fila.itemView.setOnClickListener(vista -> {
            if (escucha != null) {
                escucha.alPulsarFila(usuario);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    static class FilaUsuario extends RecyclerView.ViewHolder {

        final TextView txtNombre;
        final TextView txtCorreo;

        FilaUsuario(Context contexto) {
            super(crearCaja(contexto));

            int lateral = Interfaz.dp(contexto, 16);
            int vertical = Interfaz.dp(contexto, 10);

            txtNombre = new TextView(contexto);
            txtNombre.setTextAppearance(R.style.TextAppearance_Taller3_FilaNombre);
            txtNombre.setTextColor(ContextCompat.getColor(contexto, R.color.md_on_surface));
            txtNombre.setMaxLines(2);
            txtNombre.setEllipsize(TextUtils.TruncateAt.END);

            txtCorreo = new TextView(contexto);
            txtCorreo.setTextAppearance(R.style.TextAppearance_Taller3_Mono);
            txtCorreo.setTextColor(ContextCompat.getColor(contexto, R.color.md_on_surface_variant));
            txtCorreo.setMaxLines(1);
            txtCorreo.setEllipsize(TextUtils.TruncateAt.END);

            LinearLayout caja = (LinearLayout) itemView;
            caja.setOrientation(LinearLayout.VERTICAL);
            caja.setMinimumHeight(Interfaz.dp(contexto, 64));
            caja.setPadding(lateral, vertical, lateral, vertical);

            caja.addView(txtNombre);
            caja.addView(txtCorreo);
        }

        private static View crearCaja(Context contexto) {
            LinearLayout caja = new LinearLayout(contexto);
            caja.setOrientation(LinearLayout.VERTICAL);
            caja.setLayoutParams(new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            TypedValue valor = new TypedValue();

            if (contexto.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, valor, true)) {
                caja.setBackgroundResource(valor.resourceId);
            }

            return caja;
        }
    }
}
