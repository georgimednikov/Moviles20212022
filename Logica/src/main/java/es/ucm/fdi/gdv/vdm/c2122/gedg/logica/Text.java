package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

public class Text {

    //Variables de animación
    private float FADE_DURATION = 0.2f; //Segundos que duran los fades de las celdas
    private float alpha_;
    private float elapsedTime_;
    private boolean switching_;
    private boolean appearing_;

    private boolean centered_;
    private boolean newBold_; //Nuevo valor bold
    private int newSize_; //Nuevo valor size
    private String newText_; //Nuevo valor texto
    private String text_;
    private Font font_;

    Text(Font font, String text, boolean centered) {
        font_ = font;
        text_ = text;
        centered_ = centered;
    }

    /**
     * Renderiza el texto con los valores previamente establecidos
     * @param g Motor grafico
     */
    public void render(Graphics g) {
        if (switching_) {
            Color c = font_.getColor();
            font_.setColor(new Color(c.r, c.g, c.b, (int)(255 * alpha_))); //Transiciona el alpha
            g.drawText(font_, text_, 0, 0, centered_);
            font_.setColor(c); //Restaura el valor original del color de la fuente
        }
        else g.drawText(font_, text_, 0, 0, centered_);
    }

    /**
     * Actualiza los tiempos de las animaciones del texto
     */
    public void updateText(double deltaTime) {
        if (!switching_) return; //Si no esta haciendo una animacion sale
        if (elapsedTime_ >= FADE_DURATION) { //Si ha acabado un ciclo
            if (appearing_) { //Estaba apareciendo: Se acaba la animacion
                switching_ = false;
            }
            else { //Ha desaparecido, ahora empieza una nueva animacion con sus nuevos valores apareciendo
                appearing_ = true;
                font_.setSize(newSize_);
                font_.setBold(newBold_);
                text_ = newText_;
                elapsedTime_ = 0;
            }
        }
        else { //Actualiza el tiempo y el alpha en base a este
            elapsedTime_ += deltaTime;
            if (!appearing_) alpha_ = 1 - Math.min((elapsedTime_ / FADE_DURATION), 1);
            else alpha_ = Math.min((elapsedTime_ / FADE_DURATION), 1);
        }
    }

    /**
     * Hace fade-out de los valores actuales y a continuacion fade-in de los que se pasan
     * @param newText Texto al que transiciona
     * @param newSize Tamaño al que transiciona
     * @param newBold Bold al que transiciona
     */
    public void fade(String newText, int newSize, boolean newBold) {
        switching_ = true;
        appearing_ = false;
        newText_ = newText;
        newSize_ = newSize;
        newBold_ = newBold;
        elapsedTime_ = 0;
        alpha_ = 1;
    }

    /**
     * Actualiza el texto sin necesidad de animacion
     * @param text Nuevo texto
     */
    public void setText(String text) {
        text_ = text;
    }
}
