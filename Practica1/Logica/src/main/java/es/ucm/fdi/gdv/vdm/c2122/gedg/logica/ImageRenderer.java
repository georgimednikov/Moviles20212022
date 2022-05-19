package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

/**
 * Clase que implementa ObjectRenderer para dibujar imágenes.
 */
public class ImageRenderer extends ObjectRenderer {

    private final float FADE_DURATION = 0.2f; //Segundos que duran los fades

    private Image image_;
    private int width_;
    private int height_;
    private boolean centered_; //Si false se dibuja sobre la esquina superior izquierda.

    public ImageRenderer(Image lock, int width, int height, boolean centered) {
        this(lock, width, height, centered, true);
    }
    public ImageRenderer(Image image, int width, int height, boolean centered, boolean visible) {
        super(visible);
        image_ = image;
        width_ = width;
        height_ = height;
        centered_ = centered;
        elapsedFade_ = 0;
        fadeDur_ = FADE_DURATION;
    }

    /**
     * Dibuja el objeto en el canvas. Método vacío que hay que implementar heredando.
     */
    @Override
    public void render(Graphics g) {
        if (alpha_ <= 0) return;
        g.drawImage(image_, 0, 0, width_, height_, centered_, alpha_);
    }
}
