package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Bitmap;
import android.graphics.Paint;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

/**
 * Clase que representa una imagen en la plataforma de Android.
 */
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

    /**
     * Devuelve el bitmap de Android que describe la imagen.
     */
    public Bitmap getBitmap() { return sprite_; }

    /**
     * Devuelve la configuración de dibujado de Android de la imagen.
     */
    public Paint getPaint() { return paint_; }


    /**
     * Devuelve la anchura de la imagen.
     */
    @Override
    public int getWidth() {
        return width_;
    }

    /**
     * Devuelve la altura de la imagen.
     */
    @Override
    public int getHeight() {
        return height_;
    }
}
