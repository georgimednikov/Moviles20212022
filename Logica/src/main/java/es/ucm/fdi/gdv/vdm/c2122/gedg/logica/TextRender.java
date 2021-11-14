package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

public class TextRender extends ObjectRender {

    private boolean reappear_;
    private boolean centered_;
    private boolean newBold_; //Nuevo valor bold
    private int newSize_; //Nuevo valor size
    private String newText_; //Nuevo valor texto
    private String text_;
    private Font font_;

    TextRender(Font font, String text, boolean centered) {
        this(font, text, centered, true);
    }
    TextRender(Font font, String text, boolean centered, boolean visible) {
        super(visible);
        font_ = font;
        text_ = text;
        centered_ = centered;
        reappear_ = false;
    }

    /**
     * Actualiza los valores del texto tras hacer un fade-out
     * @param newText Nuevo texto
     * @param newSize Nuevo tamaño
     * @param newBold Si pasa a ser negrita o no
     */
    public void fadeNewText(String newText, int newSize, boolean newBold, float dur) {
        newText_ = newText;
        newSize_ = newSize;
        newBold_ = newBold;
        reappear_ = true;
        fadeOut(dur);
    }

    /**
     * Actualiza el texto sin necesidad de animacion
     * @param text Nuevo texto
     */
    public void setText(String text) {
        text_ = text;
    }

    /**
     * Renderiza el texto con los valores previamente establecidos
     * @param g Motor grafico
     */
    @Override
    public void render(Graphics g) {
        if (alpha_ <= 0) return;
        Color c = font_.getColor();
        font_.setColor(new Color(c.r, c.g, c.b, (int)(255 * alpha_))); //Transiciona el alpha
        g.drawText(font_, text_, 0, 0, centered_);
        font_.setColor(c); //Restaura el valor original del color de la fuente
    }

    /**
     * Se llama a este metodo cuando se acaba un fade-out
     */
    @Override
    protected void onFadeOutEnd() {
        if (!reappear_) return;
        font_.setSize(newSize_);
        font_.setBold(newBold_);
        text_ = newText_;
        fadeIn(fadeDur_); //Tras cambiar los valores aparecen con un fade-in
    }
}
