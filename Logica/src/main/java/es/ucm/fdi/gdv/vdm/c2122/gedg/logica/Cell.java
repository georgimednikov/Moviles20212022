package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

//Clase representativa de una celda en el juego
public class Cell {
    public enum STATE {
        RED,
        GREY,
        BLUE
    }

    private boolean fixed_; //Si se puede modificar el estado
    private int x_; //Posición
    private int y_;
    private int number_; //Número de la celda
    private STATE currState_; //Color actual de la celda

    public Cell(int x, int y){
        x_ = x;
        y_ = y;
        resetCell();
    }

    //Solo fija la celda (para cuando es roja) y java no permite parámetros por defecto
    public void fixCell(STATE solution) {
        currState_ = solution;
        fixed_ = true;
    }
    //Fija la celda y le asigna el número de celdas contiguas azules
    public void fixCell(STATE solution, int number) {
        number_ = number;
        fixCell(solution);
    }

    public void setNumber(int number){
        number_ = number;
    }

    public void resetCell(){
        number_ = -1;
        currState_ = STATE.GREY;
        fixed_ = false;
    }

    public void applyHint(Hint h){
        switch (h.type_){
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

    //Cicla el color de la celda siguiendo el orden, para el juego
    public void changeState() {
        switch (currState_)
        {
            case RED:
                currState_ = STATE.GREY;
            case BLUE:
                currState_ = STATE.RED;
            case GREY:
                currState_ = STATE.BLUE;
        }
    }
    public void revertState() {
        switch (currState_)
        {
            case RED:
                currState_ = STATE.BLUE;
            case BLUE:
                currState_ = STATE.GREY;
            case GREY:
                currState_ = STATE.RED;
        }
    }

    public boolean isFixed() { return fixed_; }
    public STATE getCurrState() { return currState_; }
    public int getNumber() { return number_; }
    public int getX() { return x_; }
    public int getY() { return y_; }
}