package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

public class Text {
    private float alpha_;
    private float elapsedTime_;
    private float fadeDuration_ = 0.1f; //Segundos que duran los fades de las celdas
    private boolean switching_;
    private boolean appearing_ = false;

    private boolean centered_;
    private int newSize_;
    private Font font_;
    private String text_;
    private String newText_;
    private boolean newBold_;

    Text(Font font, String text, boolean centered) {
        font_ = font;
        text_ = text;
        centered_ = centered;
    }

    public void render(Graphics g) {
        if (switching_) {
            Color color = font_.getColor();
            int originalAlpha = color.a;
            color.a = (int)(255 * alpha_);
            g.drawText(font_, text_, 0, 0, centered_);
            color.a = originalAlpha;
        }
        else g.drawText(font_, text_, 0, 0, centered_);
    }

    public void updateText(double deltaTime) {
        if (!switching_) return;
        if (elapsedTime_ >= fadeDuration_) {
            if (appearing_) {
                switching_ = false;
                appearing_ = false;
            }
            else {
                appearing_ = true;
                font_.setSize(newSize_);
                font_.setBold(newBold_);
                text_ = newText_;
                elapsedTime_ = 0;
            }
        }
        else {
            elapsedTime_ += deltaTime;
            if (!appearing_) alpha_ = 1 - Math.min((elapsedTime_ / fadeDuration_), 1);
            else alpha_ = Math.min((elapsedTime_ / fadeDuration_), 1);
        }
    }

    public void fade(String newText, int newSize, boolean newBold) {
        switching_ = true;
        newText_ = newText;
        newSize_ = newSize;
        newBold_ = newBold;
        elapsedTime_ = 0;
    }

    public void setText(String text) {
        text_ = text;
    }
}
