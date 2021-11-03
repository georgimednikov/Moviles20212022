package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsAndroid implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics {

    private Context context_;
    private Canvas canvas_;
    private Paint paint_; //Paint para cada elemento visual que lo utilice
    private int saveCalled = 0; //Contador para que no se pueda llamar mas veces a restore que a save

    public GraphicsAndroid(Context context) {
        context_ = context;
        paint_ = new Paint();
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
    public void setColor(Color color) {
        paint_.setARGB(color.a, color.r, color.g, color.b);
    }

    @Override
    public void fillCircle(int cx, int cy, int r) {
        canvas_.drawCircle(cx, cy, r, paint_);
    }

    @Override
    public void drawText(Font font, String text, int x, int y) {
        FontAndroid f = (FontAndroid) font;
        if (f.isLoaded()) canvas_.drawText(text, x, y, f.getPaint());
    }

    @Override
    public int getWidth() {
        return canvas_.getWidth();
    }

    @Override
    public int getHeight() {
        return canvas_.getHeight();
    }

    @Override
    public void translate(int dx, int dy) { canvas_.translate(dx, dy); }

    @Override
    public void scale(float sx, float sy) { canvas_.scale(sx, sy); }

    @Override
    public void save() { canvas_.save(); saveCalled++; }

    @Override
    public void restore() {
        if (saveCalled > 0) {
            canvas_.restore();
            saveCalled--;
        }
    }
}
