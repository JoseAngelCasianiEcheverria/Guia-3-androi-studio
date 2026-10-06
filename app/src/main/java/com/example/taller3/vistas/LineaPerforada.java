package com.example.taller3.vistas;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.example.taller3.R;
import com.example.taller3.controladores.Interfaz;

public class LineaPerforada extends View {

    private final Paint pintura = new Paint(Paint.ANTI_ALIAS_FLAG);

    public LineaPerforada(Context contexto) {
        this(contexto, null);
    }

    public LineaPerforada(Context contexto, AttributeSet atributos) {
        super(contexto, atributos);

        pintura.setStyle(Paint.Style.STROKE);
        pintura.setStrokeWidth(Interfaz.dp(contexto, 2));
        pintura.setPathEffect(new DashPathEffect(
                new float[]{Interfaz.dp(contexto, 4), Interfaz.dp(contexto, 4)}, 0));
        pintura.setColor(ContextCompat.getColor(contexto, R.color.md_outline));
    }

    @Override
    protected void onMeasure(int anchoMedido, int altoMedido) {
        int alto = Interfaz.dp(getContext(), 3);
        setMeasuredDimension(
                resolveSizeAndState(getSuggestedMinimumWidth(), anchoMedido, 0),
                resolveSizeAndState(alto, altoMedido, 0));
    }

    @Override
    protected void onDraw(Canvas lienzo) {
        float centro = getHeight() / 2f;
        lienzo.drawLine(0, centro, getWidth(), centro, pintura);
    }
}
