package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class ImageAndroid implements Image {

    private int x_;
    private int y_;
    private int width_;
    private int height_;

    public ImageAndroid(String filename, int x, int y, int width, int height) {
        x_ = x; y_ = y;
        width_ = width; height_ = height;
    }

    @Override
    public void setPosition(int x, int y) { x_ = x; y_ = y; }
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
