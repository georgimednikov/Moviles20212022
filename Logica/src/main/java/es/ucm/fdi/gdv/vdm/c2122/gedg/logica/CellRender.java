package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

public class CellRender {

    private int cellRadius_;

    private CellLogic cell_;
    private Color blue_;
    private Color red_;
    private Color grey_;

    private float alpha_;
    private float elapsedTime_;
    private float fadeDuration_ = 0.1f; //Segundos que duran los fades de las celdas
    private boolean switching_;

    public CellRender(CellLogic cell, int radius) {
        cell_ = cell;
        cellRadius_ = radius;
        blue_ = new Color(72, 193, 228, 255);
        red_ = new Color(245, 53, 73, 255);
        grey_ = new Color(238, 237, 239, 255);
    }

    public void render(Graphics g) {
        if (switching_) {
            Color prevColor = getColorState(cell_.getPrevState());
            g.setColor(prevColor);
            g.fillCircle(0, 0, cellRadius_);
            Color currColor = getColorState(cell_.getCurrState());
            g.setColor(new Color(currColor.r, currColor.g, currColor.b, (int) (255 * alpha_)));
        }
        else
            g.setColor(getColorState(cell_.getCurrState()));
        g.fillCircle(0, 0, cellRadius_);
    }

    public void updateCellRender(double deltaTime) {
        if (elapsedTime_ >= fadeDuration_) {
            switching_ = false;
        }
        else {
            elapsedTime_ += deltaTime;
            alpha_ = Math.min((elapsedTime_ / fadeDuration_), 1);
        }
    }

    public void fade() {
        switching_ = true;
        elapsedTime_ = 0;
    }

    private Color getColorState(CellLogic.STATE state) {
        switch (state) {
            case BLUE:
                return blue_;
            case RED:
                return red_;
            default:
                return grey_;
        }
    }
}
