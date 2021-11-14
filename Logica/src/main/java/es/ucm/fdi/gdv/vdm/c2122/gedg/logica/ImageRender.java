package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class ImageRender extends ObjectRender {

    private final float FADE_DURATION = 0.2f; //Segundos que duran los fades

    private Image lock_;
    private int size_;
    private boolean centered_;

    public ImageRender(Image lock, int size, boolean centered) {
        this(lock, size, centered, true);
    }
    public ImageRender(Image lock, int size, boolean centered, boolean visible) {
        super(visible);
        lock_ = lock;
        size_ = size;
        centered_ = centered;
        elapsedFade_ = 0;
        fadeDur_ = FADE_DURATION;
    }

    @Override
    public void render(Graphics g) {
        if (alpha_ <= 0) return;
        g.drawImage(lock_, 0, 0, size_, size_, centered_, alpha_);
    }
}
