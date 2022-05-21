package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Paint;
import android.graphics.Typeface;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;

/**
 * Clase que representa fuentes en la plataforma de Android.
 */
public class FontAndroid implements Font {

    int originalSize_; //Tamaño original de la fuente, NO modificado por el redimensionado de la pantalla
    private Color color_;
    private Paint paint_;
    private Typeface font_; //Fuente de Android

    public FontAndroid(Typeface font, Color color, int size, boolean isBold) {
        paint_ = new Paint();
        font_ = font; paint_.setTypeface(font_);
        color_ = color; paint_.setARGB(color.a, color.r, color.g, color.b);
        originalSize_ = size; paint_.setTextSize(originalSize_);
        paint_.setFakeBoldText(isBold);
    }

    /**
     * Devuelve la configuración de dibujado de JFrame de la fuente, que fundamentalmente es la fuente del texto.
     */
    public Paint getPaint() { return paint_; }

    /**
     * Fija el tamaño de la fuente con el que se va a escribir el texto.
     */
    @Override
    //Modifica el tamaño de renderizado
    public void setRenderSize(int size) {
        paint_.setTextSize(size);
    }

    /**
     * Fija el color de la fuente con el que se va a escribir el texto.
     */
    @Override
    public void setColor(Color color) {
        color_ = color;
        paint_.setARGB(color.a, color.r, color.g, color.b);
    }

    /**
     * Fija el tamaño original de la fuente para poder hacer reset en caso de que haya sido modificado.
     */
    @Override
    public void setSize(int size) {
        originalSize_ = size; paint_.setTextSize(size);
    }

    /**
     * Fija la negrita de la fuente con el que se va a escribir.
     */
    @Override
    public void setBold(boolean isBold) { paint_.setFakeBoldText(isBold); }

    /**
     * Devuelve el tamaño de dibujado de la fuente.
     */
    @Override
    public int getSize() { return (int)paint_.getTextSize(); }

    /**
     * Devuelve el color del a fuente.
     */
    @Override
    public Color getColor() { return color_; }
}
