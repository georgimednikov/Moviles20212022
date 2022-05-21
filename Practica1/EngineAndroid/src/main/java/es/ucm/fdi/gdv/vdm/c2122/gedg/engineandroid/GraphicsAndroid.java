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

/**
 * Motor gráfico del proyecto para la plataforma de Android.
 */
public class GraphicsAndroid extends GraphicsCommon {

    private final SurfaceView view_;
    private final SurfaceHolder holder_;
    private final Context context_;
    private Canvas canvas_;
    private final Paint paint_; //Paint para cada elemento visual que lo utilice

    public GraphicsAndroid(Context context, SurfaceView view) {
        context_ = context;
        view_ = view;
        holder_ = view_.getHolder();
        paint_ = new Paint();
    }

    /**
     * Se inicializa el canvas ajustándose a la ventana. Devuelve false si no se logra inicializar.
     */
    public boolean init() {
        curSizeX = view_.getWidth();
        curSizeY = view_.getHeight();
        adjustToWindowSize(view_.getWidth(), view_.getHeight());
        return curSizeX != 0;
    }

    /**
     * Hace lock del canvas para que solo un hilo pueda interactuar con él.
     */
    public void lock() {
        //Se espera a poder conseguir el canvas de la aplicacion y se fija
        while (!holder_.getSurface().isValid());
        canvas_ = holder_.lockCanvas();
    }

    /**
     * Se deja de hacer lock del canvas.
     */
    public void unlock() {
        //Se libera el canvas
        holder_.unlockCanvasAndPost(canvas_);
    }

    public SurfaceView getSurfaceView() {
        return view_;
    }

    /**
     * Pone el render buffer a blanco y ajusta la pantalla a las dimensiones correctas en base a la ventana
     */
    public void setGamePosition(){
        clear(new Color(255,255,255,255));
        canvas_.translate(curPosX, curPosY);
        canvas_.clipRect(0, 0, curSizeX, curSizeY); // Ya se ha trasladado, su 0, 0 esta movido ya
    }

    /**
     * Crea una imagen a partir de un path de archivo
     */
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

    /**
     * Crea una fuente de texto a partir de un path de archivo
     * @param filename path
     * @param color color
     * @param size tamaño
     * @param isBold si es negrita
     */
    @Override
    public Font newFont(String filename, Color color, int size, boolean isBold) {
        Typeface font = Typeface.createFromAsset(context_.getAssets(), filename);
        return new FontAndroid(font, color, size, isBold);
    }

    /**
     * Se limpia el render buffer pintando por encima del color dado cubriendo toda la pantalla
     */
    @Override
    public void clear(Color color) {
        canvas_.drawARGB(color.a, color.r, color.g, color.b);
    }

    /**
     * Dibuja la imagen que reciba con los parametros dados
     * @param image Imagen
     * @param x Posicion X
     * @param y Posicion Y
     * @param width Ancho
     * @param height Alto
     * @param centered Se dibuja en base a su centro a su esquina superior izquierda
     */
    @Override
    public void drawImage(Image image, int x, int y, int width, int height, boolean centered, float opacity) {
        ImageAndroid img = (ImageAndroid) image;
        //Se ajustan los valores a las dimensiones reales del canvas
        x = toPhysicalX(x);
        y = toPhysicalY(y);
        width = toPhysicalX(width);
        height = toPhysicalY(height);
        Rect src = new Rect(0, 0, img.getWidth(), img.getHeight());
        Rect dst;
        //Se centra si se ha pedido
        if (centered)
            dst = new Rect(x - width / 2, y - height / 2, x + width / 2, y + height / 2);
        else
            dst = new Rect(x, y, x + width, y + height);

        img.getPaint().setAlpha((int)(opacity * 255)); //Se modifica la opacidad.
        canvas_.drawBitmap(img.getBitmap(), src, dst, img.getPaint());
        img.getPaint().setAlpha(255); //Se restaura la opacidad por defecto.
    }

    /**
     * Fija el color con el que se va a pintar los siguientes elementos.
     */
    @Override
    public void setColor(Color color) {
        paint_.setARGB(color.a, color.r, color.g, color.b);
    }

    /**
     * Dibuja un circulo del color previamente establecido.
     * @param cx Posicion X del centro
     * @param cy Posicion Y del centro
     * @param r Radio
     */
    @Override
    public void fillCircle(int cx, int cy, int r) {
        //Se ajustan los valores a las dimensiones reales del canvas
        cx = toPhysicalX(cx);
        cy = toPhysicalY(cy);
        r = toPhysicalY(r);
        canvas_.drawCircle(cx, cy, r, paint_);
    }

    /**
     * Dibuja un texto dado con una fuente dada
     * @param font Fuente
     * @param text Texto
     * @param x Posicion X
     * @param y Posicion Y
     * @param centered Se dibuja en base a su centro a su esquina superior izquierda
     */
    @Override
    public void drawText(Font font, String text, int x, int y, boolean centered) {
        //Se ajustan los valores a las dimensiones reales del canvas
        x = toPhysicalX(x);
        y = toPhysicalY(y);
        FontAndroid f = (FontAndroid) font;
        f.setRenderSize(toPhysicalY(f.originalSize_)); //Se pasa a tamaño real.
        Rect bounds = new Rect(); f.getPaint().getTextBounds(text, 0, text.length(), bounds);

        //Se centra si se ha pedido.
        if (centered)
            canvas_.drawText(text, x - bounds.exactCenterX(), y - bounds.exactCenterY(), f.getPaint());
        else
            canvas_.drawText(text, x, y - bounds.exactCenterY(), f.getPaint());
    }

    /**
     * Devuelve el ancho de la ventana.
     */
    @Override
    public int getWidth() { return refSizeX; }

    /**
     * Devuelve el alto de la ventana.
     */
    @Override
    public int getHeight() {
        return refSizeY;
    }

    /**
     * Calcula el ancho de un texto con una fuente dada
     */
    @Override
    public int getTextWidth(Font font, String text) {
        Rect bounds = new Rect(); ((FontAndroid)font).getPaint().getTextBounds(text, 0, text.length(), bounds);
        return bounds.width();
    }
    /**
     * Calcula el alto de un texto con una fuente dada
     */
    @Override
    public int getTextHeight(Font font, String text) {
        Rect bounds = new Rect();
        ((FontAndroid) font).getPaint().getTextBounds(text, 0, text.length(), bounds);
        return bounds.height();
    }

    /**
     * Desplaza el canvas las unidades virtuales indicadas en los ejes X e Y haciendo
     * la transformacion necesaria para ajustarse al tamaño actual de la ventana
     */
    @Override
    public void translate(int dx, int dy) {
        dx = toPhysicalX(dx);
        dy = toPhysicalY(dy);
        canvas_.translate(dx, dy);
    }

    /**
     * Cambia la escala del canvas
     */
    @Override
    public void scale(float sx, float sy) { canvas_.scale(sx, sy); }

    /**
     * Guarda la posicion del canvas
     */
    @Override
    public void save() { canvas_.save(); }

    /**
     * Vuelve a la posicion anterior del canvas
     */
    @Override
    public void restore() {
        if (canvas_.getSaveCount() > 0) {
            canvas_.restore();
        }
    }
}
