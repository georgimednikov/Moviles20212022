package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsAndroid implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics {

    private Context context_;
    private Canvas canvas_;

    public GraphicsAndroid(Context context) {
        context_ = context;
    }

    @Override
    public Image newImage(String name) {
        return null;
    }

    @Override
    public Font newFont(String filename, Color color, int size) {
        return new FontAndroid(context_, filename, color, size);
    }

    @Override
    public void clear(Color color) {
        canvas_.drawARGB(color.a, color.r, color.g, color.b);
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height) {

    }

    @Override
    @Deprecated
    public void setColor(Color color) {}

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
    public void translate(int dx, int dy) {

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
