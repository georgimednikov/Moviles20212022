package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Paint;
import android.graphics.Typeface;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;

public class FontAndroid implements Font {

    private Paint paint_;
    private Typeface font_;
    private Color color_;
    private int size_;
    private boolean isBold_;

    public FontAndroid(Typeface font, Color color, int size, boolean isBold) {
        paint_ = new Paint();
        font_ = font; paint_.setTypeface(font_);
        color_ = color; paint_.setARGB(color_.a, color_.r, color_.g, color_.b);
        size_ = size; paint_.setTextSize(size_);
        isBold_ = isBold; paint_.setFakeBoldText(isBold_);
    }

    public boolean isLoaded() {
        return font_ != null;
    }
    public Paint getPaint() { return paint_; }

    @Override
    public void setColor(Color color) {
        color_ = color; paint_.setARGB(color_.a, color_.r, color_.g, color_.b);
    }
    @Override
    public void setSize(int size) {
        size_ = size; paint_.setTextSize(size_);
    }
    @Override
    public void setBold(boolean isBold) { isBold_ = isBold; paint_.setFakeBoldText(isBold_); }
    @Override
    public int getSize() {
        return size_;
    }
}
