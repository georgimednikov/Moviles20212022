package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

/**
 * Clase que renderiza una CellLogic
 */
public class CellRenderer extends ObjectRenderer {

    public enum CELL_TYPE {
        NORMAL,
        LOCK,
        NUMBER
    }

    private final float CELL_FADE_DURATION = 0.15f;
    private final float BUMP_DURATION = 0.15f;
    private final float BUMP_EXPAND_PERCENT = 0.1f;
    private final int NUM_BUMPS = 2;

    CELL_TYPE type_;
    private Cell.STATE state_;
    private Color blue_;
    private Color red_;
    private Color grey_;
    private int cellRadius_; //Radio del renderizado de la celda

    //Variables de posibles renderizados sobre la celda
    private ObjectRenderer object_;

    //Variables relacionadas con las animaciones de los tipos de celdas
    //Cada tipo las usa como le conviene
    private int numRepeats;
    private float animDur_;
    private float animElapsed_;
    private boolean animated_;

    //Candado
    private float cellExpansion_;

    public CellRenderer(int radius) {
        this(radius, true);
    }
    public CellRenderer(int radius, boolean visible) {
        super(visible);
        type_ = CELL_TYPE.NORMAL;
        cellRadius_ = radius;
        blue_ = new Color(72, 193, 228, 255);
        red_ = new Color(245, 53, 73, 255);
        grey_ = new Color(238, 237, 239, 255);
    }

    public void setState(Cell.STATE state){
        state_ = state;
    }

    @Override
    public void render(Graphics g) {
        if (alpha_ <= 0) return;
        Color cc = getColorState(state_); //Current Color
        Color pc = getColorState(state_); //Previous Color
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
    public void updateRenderer(double deltaTime) {
        if (type_ != CELL_TYPE.NORMAL) object_.updateRenderer(deltaTime);
        super.updateRenderer(deltaTime);

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
    public void transitionCell() {
        animated_ = true;
        animDur_ = CELL_FADE_DURATION;
        numRepeats = 1;
        animElapsed_ = 0;
    }
    /**
     * Activa la animacion del bump de la Cell
     */
    public void bumpCell() {
        animated_ = true;
        animDur_ = BUMP_DURATION;
        cellExpansion_ = BUMP_EXPAND_PERCENT;
        numRepeats = NUM_BUMPS;
        animElapsed_ = 0;
    }

    /**
     * Fija la celda como tipo numero y crea su texto
     */
    public void setTypeNumber(Font font, String text) {
        type_ = CELL_TYPE.NUMBER;
        object_ = new TextRender(font, text, true);
        object_.fadeDur_ = BUMP_DURATION * NUM_BUMPS;
    }

    /**
     * Fija la celda como tipo candado y crea su imagen
     */
    public void setTypeLock(Image lock) {
        type_ = CELL_TYPE.LOCK;
        object_ = new ImageRenderer(lock, cellRadius_, cellRadius_, true, false);
        object_.fadeDur_ = BUMP_DURATION * NUM_BUMPS;
        object_.maxAlpha_ = 0.3f;
    }

    /**
     * Cicla el estado del candado
     */
    public void changeLock() {
        object_.changeState();
    }

    private Color getColorState(Cell.STATE state) {
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
