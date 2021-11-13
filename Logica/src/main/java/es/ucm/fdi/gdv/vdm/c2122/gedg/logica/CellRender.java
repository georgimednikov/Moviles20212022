package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

/**
 * Clase que renderiza una CellLogic
 */
public class CellRender {

    public enum CELL_TYPE {
        NORMAL,
        LOCK,
        NUMBER
    }
    CELL_TYPE type_;
    private CellLogic cell_; //Celda logica que representa
    private Color blue_;
    private Color red_;
    private Color grey_;
    private int cellRadius_; //Radio del renderizado de la celda

    //Variables de posibles renderizados sobre la celda
    private Text number_;
    private Image lock_;
    private boolean locked_;

    //Variables relacionadas con las animaciones
    private final float FADE_DURATION = 0.1f; //Segundos que duran los fades de las celdas
    private final float EXPAND_PERCENT = 0.1f;
    private final float BUMP_DURATION = 0.15f;
    private final int NUM_BUMPS = 2;
    private float alpha_;
    private float elapsedTime_;
    private boolean switching_;

    public CellRender(CellLogic cell, int radius) {
        type_ = CELL_TYPE.NORMAL;
        cell_ = cell;
        cellRadius_ = radius;
        blue_ = new Color(72, 193, 228, 255);
        red_ = new Color(245, 53, 73, 255);
        grey_ = new Color(238, 237, 239, 255);
    }

    public void render(Graphics g) {
        if (type_ == CELL_TYPE.NORMAL) {
            if (switching_) {
                Color prevColor = getColorState(cell_.getPrevState());
                g.setColor(prevColor);
                g.fillCircle(0, 0, cellRadius_);
                Color currColor = getColorState(cell_.getCurrState());
                g.setColor(new Color(currColor.r, currColor.g, currColor.b, (int) (255 * alpha_)));
            } else
                g.setColor(getColorState(cell_.getCurrState()));
            g.fillCircle(0, 0, cellRadius_);
        } else {
            g.setColor(getColorState(cell_.getCurrState()));
            int lockRadius = cellRadius_;
            if (switching_) {
                lockRadius += (cellRadius_ * EXPAND_PERCENT) * Math.abs(Math.sin(elapsedTime_ / BUMP_DURATION * Math.PI));
            }
            g.fillCircle(0, 0, lockRadius);
            if (type_ == CELL_TYPE.NUMBER)
                number_.render(g);
            else if (locked_)
                g.drawImage(lock_, 0, 0, cellRadius_, cellRadius_, true);
        }
    }

    public void updateCellRender(double deltaTime) {
        if (!switching_) return;
        if (type_ == CELL_TYPE.NORMAL) {
            if (elapsedTime_ >= FADE_DURATION) {
                switching_ = false;
            } else {
                elapsedTime_ += deltaTime;
                alpha_ = Math.min((elapsedTime_ / FADE_DURATION), 1);
            }
        }
        else {
            if (elapsedTime_ >= BUMP_DURATION * NUM_BUMPS)
                switching_ = false;
            else
                elapsedTime_ += deltaTime;
        }
    }

    /**
     * Anima la celda. Esta animacion depende de su tipo
     */
    public void animateCell() {
        switching_ = true;
        elapsedTime_ = 0;
    }

    /**
     * Define la celda como fija y con numero
     */
    public void setTypeNumber(Text number) {
        type_ = CELL_TYPE.NUMBER;
        number_ = number;
    }
    /**
     * Define la celda como fija y con imagen
     */
    public void setTypeLock(Image lock) {
        type_ = CELL_TYPE.LOCK;
        lock_ = lock;
    }

    /**
     * Cicla el estado del candado
     */
    public void changeLock() {
        locked_ = !locked_;
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
