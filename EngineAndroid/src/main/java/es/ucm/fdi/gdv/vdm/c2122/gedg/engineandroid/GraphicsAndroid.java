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

    private final SurfaceView view_;
    private final SurfaceHolder holder_;
    private final Context context_;
    private Canvas canvas_;
    private final Paint paint_; //Paint para cada elemento visual que lo utilice
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

    public void setGamePosition(){
        clear(new Color(255,255,255,255));
        canvas_.translate(curPosX, curPosY);
        canvas_.clipRect(0, 0, curSizeX, curSizeY); // Ya se ha trasladado, su 0, 0 esta movido ya
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
        x = toRealX(x);
        y = toRealY(y);
        width = toRealX(width);
        height = toRealY(height);
        Rect src = new Rect(0, 0, img.getWidth(), img.getHeight());
        Rect dst;
        if (centered)
            dst = new Rect(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        else
            dst = new Rect(x, y, x + width, y + height);
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
        x = toRealX(x);
        y = toRealY(y);
        FontAndroid f = (FontAndroid) font;
        f.setRenderSize(toRealY(f.originalSize_));
        Rect bounds = new Rect(); f.getPaint().getTextBounds(text, 0, text.length(), bounds);
        if (centered) {
            canvas_.drawText(text, x - bounds.exactCenterX(), y - bounds.exactCenterY(), f.getPaint());
        }
        else
            canvas_.drawText(text, x, y - bounds.exactCenterY(), f.getPaint());
        /*if (centered) { // TODO: aqui es donde salta
            f.getPaint().setTextAlign(Paint.Align.CENTER);
            height = bounds.height();
        }
        else
            f.getPaint().setTextAlign(Paint.Align.LEFT);
        f.setRenderSize(toRealY(f.originalSize_));
        canvas_.drawText(text, x, y + height / 2.0f, f.getPaint());*/
    }

    @Override
    public int getWidth() { return refSizeX; }

    @Override
    public int getHeight() {
        return refSizeY;
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
        dx = toRealX(dx);
        dy = toRealY(dy);
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
        curSizeX = view_.getWidth();
        curSizeY = view_.getHeight();
        adjustToWindowSize(view_.getWidth(), view_.getHeight());
        return curSizeX != 0;
    }
}
