package es.ucm.fdi.vdm.c2122.gedg.logica;

//Clase representativa de una celda en el juego
public class Cell {
    public enum STATE {
        RED,
        GREY,
        BLUE
    }

    private boolean fixed_ = false; //Si se puede modificar el estado
    private int number_ = 0; //Número de la celda
    private STATE currState_; //Color actual de la celda
    private STATE solState_; //Color correcto de la celda

    public Cell(STATE solution){
        solState_ = solution;
        currState_ = solState_;
    }

    //Cicla el color de la celda siguiendo el orden
    public boolean changeState() {
        switch (currState_)
        {
            case RED:
                currState_ = STATE.GREY;
            case BLUE:
                currState_ = STATE.RED;
            case GREY:
                currState_ = STATE.BLUE;
        }
        return currState_ == solState_;
    }

    //Pone la casilla gris
    public void setGrey() {
        if (!fixed_) currState_ = STATE.GREY;
    }

    public boolean isFixed() { return fixed_; }
    public boolean isRight() { return currState_ == solState_; }
    public STATE getSolState() { return solState_; }
    public STATE getCurrState() { return currState_; }

    //Solo fija la celda (para cuando es roja) y java no permite parámetros por defecto
    public void fixCell() {
        fixed_ = true;
    }
    //Fija la celda y le asigna el número de celdas contiguas azules
    public void fixCell(int number) {
        number_ = number;
        fixCell();
    }
}