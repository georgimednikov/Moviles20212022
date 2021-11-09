package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.InputStream;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsAndroid extends GraphicsCommon {


    private SurfaceView view_;
    private SurfaceHolder holder_;
    private Context context_;
    private Canvas canvas_;
    private Paint paint_; //Paint para cada elemento visual que lo utilice
    private int saveCalled = 0; //Contador para que no se pueda llamar mas veces a restore que a save

    public GraphicsAndroid(Context context) {
        context_ = context;
        view_ = new SurfaceView(context);
        holder_ = view_.getHolder();
        paint_ = new Paint();
    }

    public void lock() {
        while (!holder_.getSurface().isValid());
        canvas_ = holder_.lockCanvas();
    }

    public void unlock() {
        holder_.unlockCanvasAndPost(canvas_);
    }

    public SurfaceView getSurfaceView() {
        return view_;
    }

    @Override
    public Image newImage(String filename) {
        try {
            InputStream stream = context_.getAssets().open(filename);
            Bitmap sprite = BitmapFactory.decodeStream(stream);
            return new ImageAndroid(sprite);
        }
        catch(Exception e) {
            return null;
        }
    }

    @Override
    public Font newFont(String filename, Color color, int size, boolean isBold) {
        Typeface font = Typeface.createFromAsset(context_.getAssets(), filename);
        return new FontAndroid(font, color, size, isBold);
    }

    @Override
    public void clear(Color color) {
        canvas_.drawARGB(color.a, color.r, color.g, color.b);
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height, boolean centered) {
        ImageAndroid img = (ImageAndroid) image;
        Rect src = new Rect(0, 0, img.getWidth(), img.getHeight());
        Rect dst = new Rect(x, y, width, height);
        if (centered) {
            //Paint.Align prevAlign = paint_.getTextAlign();
            paint_.setTextAlign(Paint.Align.CENTER);
            canvas_.drawBitmap(img.getBitmap(), src, dst, paint_);
            //paint_.setTextAlign(prevAlign);
            return;
            //dst.left -= width / 2;
            //dst.top -= height / 2;
        }
        paint_.setTextAlign(Paint.Align.LEFT);
        canvas_.drawBitmap(img.getBitmap(), src, dst, paint_);
    }

    @Override
    public void setColor(Color color) {
        paint_.setARGB(color.a, color.r, color.g, color.b);
    }

    @Override
    public void fillCircle(int cx, int cy, int r) {
        cx = toRealX(cx);
        cy = toRealY(cy);
        r = toRealY(r);
        canvas_.drawCircle(cx, cy, r, paint_);
    }

    @Override
    public void drawText(Font font, String text, int x, int y, boolean centered) {
        x = toReal(x);
        y = toReal(y);
        FontAndroid f = (FontAndroid) font;
        Rect bounds = new Rect(); f.getPaint().getTextBounds(text, 0, text.length(), bounds);
        if (!f.isLoaded()) return;
        if (centered) {
            paint_.setTextAlign(Paint.Align.CENTER);
            canvas_.drawText(text, x, y, f.getPaint());
            return;
        }
        paint_.setTextAlign(Paint.Align.LEFT);
        canvas_.drawText(text, x, y, f.getPaint());
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
    public int getTextWidth(Font font, String text) {
        Rect bounds = new Rect(); ((FontAndroid)font).getPaint().getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }

    @Override
    public int getTextHeight(Font font, String text) {
        Rect bounds = new Rect();
        ((FontAndroid) font).getPaint().getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    @Override
    public void translate(int dx, int dy) {
        dx = toReal(dx);
        dy = toReal(dy);
        canvas_.translate(dx, dy);
    }

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

    public boolean init() {
        return canvas_ == null;
    }
}
