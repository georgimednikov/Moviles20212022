package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

/**
 * Clase que renderiza una CellLogic
 */
public class CellRender extends ObjectRender {

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
    private ObjectRender object_;

    //Variables relacionadas con las animaciones de los tipos de celdas
    //Cada tipo las usa como le conviene
    private int numRepeats;
    private float animDur_;
    private float animElapsed_;
    private boolean animated_;

    //Candado
    private float cellExpansion_;

    public CellRender(CellLogic cell, int radius) {
        this(cell, radius, true);
    }
    public CellRender(CellLogic cell, int radius, boolean visible) {
        super(visible);
        type_ = CELL_TYPE.NORMAL;
        cell_ = cell;
        cellRadius_ = radius;
        blue_ = new Color(72, 193, 228, 255);
        red_ = new Color(245, 53, 73, 255);
        grey_ = new Color(238, 237, 239, 255);
    }

    @Override
    public void render(Graphics g) {
        if (alpha_ <= 0) return;
        Color cc = getColorState(cell_.getCurrState()); //Current Color
        Color pc = getColorState(cell_.getPrevState()); //Previous Color
        Color renderColor = new Color(cc.r, cc.g, cc.b, (int)(255 * alpha_));

        if (type_ == CELL_TYPE.NORMAL) {
            if (animated_) { //Si esta animada se dibuja progresivamente el nuevo color sobre el anterior
                g.setColor(new Color(pc.r, pc.g, pc.b, (int)(255 * alpha_)));
                g.fillCircle(0, 0, cellRadius_);
                float foregroundAlpha = Math.min((animElapsed_ / animDur_), 1); //Alpha del color nuevo que se dibuja sobre el anterior
                g.setColor(new Color(cc.r, cc.g, cc.b, (int)(255 * foregroundAlpha * alpha_)));
            } else //Si no se dibuja el nuevo
                g.setColor(renderColor);
            g.fillCircle(0, 0, cellRadius_);
        }


        else {
            //Si es una celda con numero o candado dibuja su circulo con el alpha actual
            //y con el tamaño variando de su animacion
            g.setColor(renderColor);
            int lockRadius = cellRadius_;
            if (animated_) {
                lockRadius += (cellRadius_ * cellExpansion_) * Math.abs(Math.sin(animElapsed_ / animDur_ * Math.PI));
            }
            g.fillCircle(0, 0, lockRadius);
            object_.render(g);
        }
    }

    @Override
    public void updateRender(double deltaTime) {
        if (type_ != CELL_TYPE.NORMAL) object_.updateRender(deltaTime);
        super.updateRender(deltaTime);

        //Se lleva las animaciones especiales aparte porque no sustituye los fades
        //Si se esta haciendo la animacion y no ha acabado se sigue contando el tiempo, si no se para
        if (animated_ && animElapsed_ >= (animDur_ * numRepeats)) animated_ = false;
        else if (animated_) animElapsed_ += deltaTime;
    }
    @Override
    public void fadeIn(float dur) {
        super.fadeIn(dur);
        if (type_ == CELL_TYPE.NUMBER) object_.fadeIn(dur);
        //Los candados no aparecen por defecto
    }
    @Override
    public void fadeOut(float dur) {
        super.fadeOut(dur);
        //Si tiene un objeto que renderiza y se esta renderizando
        if (type_ != CELL_TYPE.NORMAL && object_.alpha_ > 0) object_.fadeOut(dur);
    }

    /**
     * Activa la animacion del bump de la Cell
     */
    public void transitionCell(float transDuration) {
        animated_ = true;
        animDur_ = transDuration;
        numRepeats = 1;
        animElapsed_ = 0;
    }
    /**
     * Activa la animacion del bump de la Cell
     */
    public void bumpCell(float bumpExpansion, float bumpDuration, int numBumps) {
        animated_ = true;
        animDur_ = bumpDuration;
        cellExpansion_ = bumpExpansion;
        numRepeats = numBumps;
        animElapsed_ = 0;
    }

    /**
     * Fija la celda como tipo numero y crea su texto
     */
    public void setTypeNumber(Font font, String text, float animDur) {
        type_ = CELL_TYPE.NUMBER;
        object_ = new TextRender(font, text, true);
        object_.fadeDur_ = animDur;
    }

    /**
     * Fija la celda como tipo candado y crea su imagen
     */
    public void setTypeLock(Image lock, float animDur) {
        type_ = CELL_TYPE.LOCK;
        object_ = new ImageRender(lock, cellRadius_, cellRadius_, true, false);
        object_.fadeDur_ = animDur;
        object_.maxAlpha_ = 0.3f;
    }

    /**
     * Cicla el estado del candado
     */
    public void changeLock() {
        object_.changeState();
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
