package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.graphics.Bitmap;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class ImageAndroid implements Image {

    private Bitmap sprite_; //Imagen para Android
    private int width_;
    private int height_;

    public ImageAndroid(Bitmap sprite) {
        sprite_ = sprite;
        width_ = sprite_.getWidth(); height_ = sprite_.getHeight();
    }

    public Bitmap getBitmap() { return sprite_; }

    @Override
    public void setSize(int width, int height) { width_ = width; height_ = height; }
    @Override
    public int getWidth() {
        return width_;
    }
    @Override
    public int getHeight() {
        return height_;
    }
}
