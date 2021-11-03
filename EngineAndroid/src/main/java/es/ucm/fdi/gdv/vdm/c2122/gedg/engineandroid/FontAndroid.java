package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.content.Context;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;

public class FontAndroid implements Font {

    private Paint paint_;
    private Typeface font_;
    private Color color_;
    private int size_;

    public FontAndroid(Context context, String filename, Color color, int size) {
        paint_ = new Paint();
        font_ = Typeface.createFromAsset(context.getAssets(), filename); paint_.setTypeface(font_);
        color_ = color; paint_.setARGB(color_.a, color_.r, color_.g, color_.b);
        size_ = size; paint_.setTextSize(size_);
    }

    public boolean isLoaded() {
        return font_ != null;
    }
    public Paint getPaint() { return paint_; }

    @Override
    public void setColor(Color color) { paint_.setARGB(color_.a, color_.r, color_.g, color_.b); }
    @Override
    public void setSize(int size) {
        paint_.setTextSize(size_);
    }
    @Override
    public Color getColor() {
        return color_;
    }
    @Override
    public int getSize() {
        return size_;
    }
}
