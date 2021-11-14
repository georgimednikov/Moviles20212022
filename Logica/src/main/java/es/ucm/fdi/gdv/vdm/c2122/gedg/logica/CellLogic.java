package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

/**
 * Clase representativa de una celda del tablero en la logica
 */
public class CellLogic {
    //Posibles estados de una celda
    public enum STATE {
        RED,
        GREY,
        BLUE
    }

    private int x_; //Posicion X
    private int y_; //Posicion Y
    private int number_; //Número de azules de la celda
    private boolean fixed_; //Si se puede modificar el estado
    private STATE currState_; //Color actual de la celda
    private STATE prevState_; //Color anterior de la celda

    public CellLogic(int x, int y){
        x_ = x;
        y_ = y;
        resetCell();
    }

    /**
     * Fija la celda con un estado dado
     */
    public void fixCell(STATE state) {
        currState_ = state;
        fixed_ = true;
    }

    /**
     * Restaura la celda a valores por defecto
     */
    public void resetCell(){
        number_ = -1;
        currState_ = prevState_ = STATE.GREY;
        fixed_ = false;
    }

    /**
     * Actualiza la celda dada una pista
     * @param h Pista que se usa para actualizar
     */
    public void applyHint(Hint h){
        switch (h.type){
            case VISIBLE_CELLS_COVERED:
            case CANNOT_SURPASS_LIMIT:
            case ISOLATED_AND_EMPTY:
            case BLUE_BUT_ISOLATED:
                currState_ = STATE.RED;
                break;
            case MUST_PLACE_BLUE:
                currState_ = STATE.BLUE;
                break;
        }
    }

    /**
     * Cambia el estado de la celda siguiendo el ciclo GRIS->AZUL->ROJO
     */
    public void changeState() {
        prevState_ = currState_; //Se guarda el estado anterior
        switch (currState_)
        {
            case GREY:
                currState_ = STATE.BLUE;
                break;
            case BLUE:
                currState_ = STATE.RED;
                break;
            case RED:
                currState_ = STATE.GREY;
                break;
        }
    }

    /**
     * Revierte el estado de la celda siguiendo el ciclo GRIS->ROJO->AZUL
     */
    public CellLogic.STATE revertState() {
        prevState_ = currState_; //Se guarda el estado anterior
        switch (currState_) {
            case RED:
                currState_ = STATE.BLUE;
                break;
            case BLUE:
                currState_ = STATE.GREY;
                break;
            case GREY:
                currState_ = STATE.RED;
                break;
        }
        return currState_;
    }

    /**
     * Fija el numero de celdas azules que ve (solo para celdas azules fijas)
     */
    public void setNumber(int number){
        number_ = number;
    }

    public int getX() { return x_; }
    public int getY() { return y_; }
    public int getNumber() { return number_; }
    public boolean isFixed() { return fixed_; }
    public STATE getCurrState() { return currState_; }
    public STATE getPrevState() { return prevState_; }
}