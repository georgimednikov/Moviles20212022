package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class GraphicsAndroid implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics {

    Canvas canvas_;
    Paint paint_;

    public GraphicsAndroid() {
        paint_ = new Paint();
    }

    @Override
    public Image newImage(String name) {
        return null;
    }

    @Override
    public Font newFont(String filename, int size, boolean isBold) {
        return null;
    }

    @Override
    public void clear(int color) {
        canvas_.drawColor(color);
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height) {

    }

    @Override
    public void setColor(int color) {

    }

    @Override
    public void fillCircle(int cx, int cy, int r) {

    }

    @Override
    public void drawText(String text, int x, int y) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void translate() {

    }

    @Override
    public void scale() {

    }

    @Override
    public void save() {

    }

    @Override
    public void restore() {

    }
}
