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
        BLUE,
        NUMBERED_BLUE
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

    /**
     * Fija el numero de celdas azules que ve (solo para celdas azules fijas)
     */
    public void setNumber(int number){
        number_ = number;
    }

    public int getNumber() { return number_; }
    public boolean isFixed() { return fixed_; }
    public Cell.STATE getCurrState() { return currState_; }

    function collect(info) {
        // pass 1
        if (!info) {
            info = {
                    unknownsAround: 0, // are there still any unknowns around
                    numberCount: 0, // how many numbers/dots are seen in all directions
                    numberReached: false, // if the current tile is a number and it has that many numbers/dots around
                    canBeCompletedWithUnknowns: false, // if the number can be reached by exactly its amount of unknowns
                    completedNumbersAround: false, // if the current tile has one or more numberReached tiles around (second pass only)
                    singlePossibleDirection: null, // if there's only one way to expand, set this to that direction
                    direction: {}
      };
            for (var dir in Directions) {
                info.direction[dir] = {
                        unknownCount: 0,
                        numberCountAfterUnknown: 0, // how many numbers after an unknown were found
                        wouldBeTooMuch: false, // would filling an unknown with a number be too much
                        maxPossibleCount: 0, // what would optionally be the highest count?
                        maxPossibleCountInOtherDirections: 0,
                        numberWhenDottingFirstUnknown: 0, // what number would this direction give when the first unknown was filled
        }
            }
            // the following for loops traverse over the OTHER tiles around the current one
            // so t is always one of the other tiles, giving information over the current tile
            var lastPossibleDirection = null,
                    possibleDirCount = 0;

            for (var dir in Directions) {
                // check each direction but end at a wall or grid-boundary
                for (var t = self.move(dir); t && !t.isWall(); t = t.move(dir)) {
                    var curDir = info.direction[dir]
                    if (t.isUnknown()) {
                        // if this is the first unknown in this direction, add it to the possible-would-be value
                        if (!curDir.unknownCount) {
                            curDir.numberWhenDottingFirstUnknown++;
                        }
                        curDir.unknownCount++;
                        curDir.maxPossibleCount++;
                        info.unknownsAround++;

                        // if we're looking FROM a number, count the possible directions
                        if (isNumber() && lastPossibleDirection != dir) {
                            possibleDirCount++;
                            lastPossibleDirection = dir;
                        }
                    }
                    else if (t.isNumber() || t.isDot()) {
                        // count the maximum possible value
                        curDir.maxPossibleCount++;
                        // if no unknown found yet in this direction
                        if (!curDir.unknownCount) {
                            info.numberCount++;
                            curDir.numberWhenDottingFirstUnknown++;
                        }
                        // else if we were looking FROM a number, and we found a number with only 1 unknown in between...
                        else if (isNumber() && curDir.unknownCount == 1) {
                            curDir.numberCountAfterUnknown++;
                            curDir.numberWhenDottingFirstUnknown++;
                            if (curDir.numberCountAfterUnknown + 1 > value) {
                                curDir.wouldBeTooMuch = true;
                            }
                        }
                    }
                }
            }

            // if there's only one possible direction that has room to expand, set it
            if (possibleDirCount == 1) {
                info.singlePossibleDirection = lastPossibleDirection;
            }

            // see if this number's value has been reached, so its paths can be closed
            if (isNumber() && value == info.numberCount)
                info.numberReached = true;
            else if (isNumber() && value == info.numberCount + info.unknownsAround)
                // TODO: only set when
                info.canBeCompletedWithUnknowns = true;
        }
        // pass 2
        else {
            for (var dir in Directions) {
                var curDir = info.direction[dir];
                for (var t = self.move(dir); t && !t.isWall(); t = t.move(dir)) {
                    if (t.isNumber() && t.info.numberReached) {
                        info.completedNumbersAround = true; // a single happy number was found around
                    }
                }
                // if we originate FROM a number, and there are unknowns in this direction
                if (isNumber() && !info.numberReached && curDir.unknownCount) {
                    // check all directions other than this one
                    curDir.maxPossibleCountInOtherDirections = 0;
                    for (var otherDir in Directions) {
                        if (otherDir != dir)
                            curDir.maxPossibleCountInOtherDirections += info.direction[otherDir].maxPossibleCount;
                    }
                }
            }
        }

        // if there's only one possible direction that has room to expand, set it
        if (possibleDirCount == 1) {
            info.singlePossibleDirection = lastPossibleDirection;
        }

        // see if this number's value has been reached, so its paths can be closed
        if (isNumber() && value == info.numberCount)
            info.numberReached = true;
        else if (isNumber() && value == info.numberCount + info.unknownsAround)
            info.canBeCompletedWithUnknowns = true;

        return info;
    }
}
