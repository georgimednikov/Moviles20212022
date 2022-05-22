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

    private boolean fixed_; //Si se puede modificar el estado
    private Cell.STATE currState_; //Color actual de la celda
    private int number_ = 0; // Numero de azules que tiene que tener alrededor

    private int curNumber_ = 0; // Numero de azules que tiene actualmente alrededor
    private int greysAround_ = 0; // Numero de grises alrededor
    private boolean fixedBlueAround = false; // Si tiene alrededor alguna azul completada
    private Board.Direction singlePossibleDirection_ = null; // Si solamente se puede expandir en una direccion
    private DirectionInfo[] directionInfo_ = new DirectionInfo[4]; // Informacion del resto de direcciones

    public void resetInfo(){
        curNumber_ = 0;
        greysAround_ = 0;
        fixedBlueAround = false;
        singlePossibleDirection_ = null;
        directionInfo_ = new DirectionInfo[4];
        for (int i = 0; i < 4; i++) {
            directionInfo_[i] = new DirectionInfo();
        }
    }

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
        resetInfo();
    }

    /**
     * Actualiza la celda dada una pista
     * @param h Pista que se usa para actualizar
     */
    public void applyHint(Hint h){
        switch (h.type_){
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
     * Pone el estado de la celda a s, para generar
     * @param s
     */
    public void setCurrState(Cell.STATE s){
        currState_ = s;
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

    public void unfix(){
        fixed_ = false;
    }

    /**
     * Fija el numero de celdas azules que ve (solo para celdas azules fijas)
     */
    public void setNumber(int number){
        number_ = number;
    }
    public void setCurNumber(int number){
        curNumber_ = number;
    }
    public void setGreysAround(int number){
        greysAround_ = number;
    }
    public void setCompletedNumbersAround(boolean completedNumbersAround) {
        fixedBlueAround = completedNumbersAround;
    }
    public void setSinglePossibleDirection(Board.Direction d) {
        singlePossibleDirection_ = d;
    }
    public DirectionInfo getDirectionInfo(Board.Direction d){
        return directionInfo_[d.id];
    }
    public int getNumber() { return number_; }
    public int getCurNumber() { return curNumber_; }
    public int getGreysAround() { return greysAround_; }
    public boolean hasFixedBlueAround() {
        return fixedBlueAround;
    }
    public boolean canBeCompletedWithGreys(){return greysAround_ + curNumber_ == number_;}
    public boolean isCompleted(){return curNumber_ == number_;}
    public boolean isFixed() { return fixed_; }
    public Cell.STATE getCurrState() { return currState_; }
    public Board.Direction getSinglePossibleDirection() { return singlePossibleDirection_; }
}
