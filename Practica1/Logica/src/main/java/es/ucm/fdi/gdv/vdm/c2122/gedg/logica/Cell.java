package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

/**
 * Representación lógica de las celdas. Contienen su estado y si son fijas o no.
 * Contiene los métodos que utiliza el tablero para modificar su estado.
 */
public class Cell {

    //Posibles estados de una celda
    public enum STATE {
        RED,
        GREY,
        BLUE
    }

    private int number_; //Número de azules de la celda
    private boolean fixed_; //Si se puede modificar el estado
    private Cell.STATE currState_; //Color actual de la celda

    public Cell(){
        resetCell();
    }

    /**
     * Fija la celda con un estado dado
     */
    public void fixCell(Cell.STATE state) {
        currState_ = state;
        fixed_ = true;
    }

    /**
     * Restaura la celda a valores por defecto
     */
    public void resetCell(){
        number_ = -1;
        currState_ = Cell.STATE.GREY;
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
                currState_ = Cell.STATE.RED;
                break;
            case MUST_PLACE_BLUE:
                currState_ = Cell.STATE.BLUE;
                break;
        }
    }

    /**
     * Cambia el estado de la celda siguiendo el ciclo GRIS->AZUL->ROJO
     */
    public void changeState() {
        switch (currState_)
        {
            case GREY:
                currState_ = Cell.STATE.BLUE;
                break;
            case BLUE:
                currState_ = Cell.STATE.RED;
                break;
            case RED:
                currState_ = Cell.STATE.GREY;
                break;
        }
    }

    /**
     * Revierte el estado de la celda siguiendo el ciclo GRIS->ROJO->AZUL
     */
    public Cell.STATE revertState() {
        switch (currState_) {
            case RED:
                currState_ = Cell.STATE.BLUE;
                break;
            case BLUE:
                currState_ = Cell.STATE.GREY;
                break;
            case GREY:
                currState_ = Cell.STATE.RED;
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

    public int getNumber() { return number_; }
    public boolean isFixed() { return fixed_; }
    public Cell.STATE getCurrState() { return currState_; }
}
