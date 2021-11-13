package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Paint;
import android.graphics.Typeface;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;

public class FontAndroid implements Font {

    int originalSize_;
    private Color color_;
    private Paint paint_;
    private Typeface font_;

    public FontAndroid(Typeface font, Color color, int size, boolean isBold) {
        paint_ = new Paint();
        font_ = font; paint_.setTypeface(font_);
        color_ = color; paint_.setARGB(color.a, color.r, color.g, color.b);
        originalSize_ = size; paint_.setTextSize(originalSize_);
        paint_.setFakeBoldText(isBold);
    }

    public Paint getPaint() { return paint_; }

    public void setRenderSize(int size) {
        if(paint_.getTextSize() == size) return;
        paint_.setTextSize(size);
    }

    @Override
    public void setColor(Color color) {
        color_ = color;
        paint_.setARGB(color.a, color.r, color.g, color.b);
    }
    @Override
    public void setSize(int size) {
        originalSize_ = size; paint_.setTextSize(size);
    }
    @Override
    public void setBold(boolean isBold) { paint_.setFakeBoldText(isBold); }
    @Override
    public int getSize() { return (int)paint_.getTextSize(); }
    @Override
    public Color getColor() { return color_; }
}
