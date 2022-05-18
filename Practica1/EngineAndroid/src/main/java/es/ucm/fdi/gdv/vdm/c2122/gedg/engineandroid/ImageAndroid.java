package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Bitmap;
import android.graphics.Paint;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class ImageAndroid implements Image {

    private Bitmap sprite_; //Imagen para Android
    private Paint paint_;
    private int width_;
    private int height_;

    public ImageAndroid(Bitmap sprite) {
        paint_ = new Paint();
        sprite_ = sprite;
        width_ = sprite_.getWidth(); height_ = sprite_.getHeight();
    }

    public Bitmap getBitmap() { return sprite_; }

    public Paint getPaint() { return paint_; }

    @Override
    public int getWidth() {
        return width_;
    }
    @Override
    public int getHeight() {
        return height_;
    }
}
