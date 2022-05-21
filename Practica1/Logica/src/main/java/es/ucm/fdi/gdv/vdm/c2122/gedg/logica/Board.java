package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Clase que representa la lógica del tablero y lo que esto implica.
 * Crea el tablero y guarda su estado.
 */
public class Board {

    public enum Direction{
        LEFT(-1, 0, 0),
        UP(0, 1, 1),
        RIGHT(1, 0, 2),
        DOWN(0, -1, 3),
        NONE(0, 0, -1);

        public final int dx;
        public final int dy;
        public final int id;

        Direction(int x, int y, int i){
            dx = x;
            dy = y;
            id = i;
        }
    }

    public Hint hint;

    //Constantes de probabilidad del nivel
    private final float BLUE_PROB = 0.5f; //Probabilidad de que una celda sea azul en vez de roja en la solución
    private final float FIXED_PROB = 0.5f; //Probabilidad de que una celda sea fija

    private int boardSize_;
    private Cell[][] board_; //Tablero
    private List<Tuple<Integer, Integer>> previousMoves_ = new ArrayList<>(); //Lista con las posiciones de las celdas modificadas

    private int numCells_;
    private int fixedCells_ = 0; //Número total de celdas fijas (no modificables)
    private int coloredCells_ = 0; //Número de celdas no grises
    private List<Cell> fixedBlueCells_ = new ArrayList<>(); //Lista de celdas logicas azules fijas

    public Board(int size, int cellRadius) {
        numCells_ = size * size;
        boardSize_ = size;

        //Se crea e inicializa el tablero
        board_ = new Cell[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board_[i][j] = new Cell(cellRadius);

        //Se establecen las celdas para una partida
        setNewBoard();
    }

    /**
     * Dadas unas coordenadas devuelve true si es fija y false en caso contrario.
     */
    public boolean isFixed(int i, int j){
        return board_[i][j].isFixed();
    }

    /**
     * Devuelve el estado de una celda en las coordenadas indicadas.
     */
    public Cell.STATE getCurrState(int i, int j){
        return board_[i][j].getCurrState();
    }

    /**
     * Devuelve el número de una celda. Si no es azul y fija devuelve -1.
     */
    public int getNumber(int i, int j){
        return board_[i][j].getNumber();
    }

    /**
     * Fija una celda. Si es gris no hace nada.
     */
    public void fixCell(int x, int y, Cell.STATE state) {
        if (state == Cell.STATE.GREY) return;
        board_[x][y].fixCell(state);
        if (state == Cell.STATE.BLUE) fixedBlueCells_.add(board_[x][y]);
        fixedCells_++;
    }

    /**
     * Cicla el estado de la celda. Devuelve true si se completa el nivel.
     */
    public boolean changeCell(int x, int y) {
        board_[x][y].changeState();
        previousMoves_.add(new Tuple<>(x, y)); //Se añade a la lista de movimientos realizados.

        CellLogic.STATE prevState = cell.getPrevState();
        CellLogic.STATE currState = cell.getCurrState();
        CellLogic.STATE solState = solBoard[x][y].getCurrState();

        //Si se ha cambiado una celda que estaba bien hay un error mas
        //Si ahora la celda esta bien entonces hay un error menos
        if (prevState == solState) contMistakes++;
        else if (currState == solState) contMistakes--;
        if(contMistakes == 0) {
            return true;
        }
        if (prevState == CellLogic.STATE.GREY) {
            coloredCells_++;
        }
        else if (currState == CellLogic.STATE.GREY) {
            coloredCells_--;
        }
        return contMistakes == 0;
    }

    /**
     * Devuelve el porcentaje del tablero que esta completo (el porcentaje de celdas no grises).
     */
    public int donePercentage() {
        return Math.round((float) coloredCells_ / (float)(numCells_ - fixedCells_) * 100);
    }

    /**
     * Deshace el último movimiento realizado. Si no hay, no hace nada y devuelve null.
     * En otro caso, deshace el movimiento y devuelve la posición de la casilla que se ha modificado.
     */
    public Tuple<Integer, Integer> undoMove() {
        if (previousMoves_.isEmpty()) return null;

        Tuple<Integer, Integer> cellPos = previousMoves_.remove(previousMoves_.size() - 1);
        board_[cellPos.x][cellPos.y].revertState();

        CellLogic.STATE currState = cellPos.getCurrState();
        CellLogic.STATE prevState = cellPos.getPrevState();
        CellLogic.STATE solState = solBoard[cellPos.getX()][cellPos.getY()].getCurrState();

        //Si la celda ahora es gris entonces hay una celda coloreada menos y hay que actualizar el progreso
        //Si la celda era gris entonces hay una celda coloreada mas y hay que actualizar el progreso
        if (currState == CellLogic.STATE.GREY) {
            coloredCells_--;
        } else if (prevState == CellLogic.STATE.GREY) {
            coloredCells_++;
        }
        //Si se ha cambiado una celda que estaba bien hay un error mas
        //Si ahora la celda esta bien entonces hay un error menos
        if (prevState == solState) contMistakes++;
        else if (currState == solState) contMistakes--;

        //Asigna una posicion al circulo negro
        highlightPosX = BOARD_OFFSET_X + cellRadius * (cellPos.getY() + 1) + (cellSeparation + cellRadius) * cellPos.getY();
        highlightPosY = BOARD_OFFSET_Y + cellRadius * (cellPos.getX() + 1) + (cellSeparation + cellRadius) * cellPos.getX();

        return cellPos;
    }

    /**
     * Crea un nivel en el tablero.
     */
    public void setNewBoard() {

    }

    /**
     * Pone todas las celdas que no son pared del tablero a relleno
     * Si overwriteNumbers, las pone todas a azul
     */
    public void setAllBlue(boolean overwriteNumbers){
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                if (board_[i][j].getCurrState() == Cell.STATE.GREY ||
                        (overwriteNumbers && board_[i][j].getCurrState() == Cell.STATE.BLUE && board_[i][j].isFixed())){
                    board_[i][j].resetCell();
                    board_[i][j].setCurrState(Cell.STATE.BLUE);
                }
            }
        }
    }

    /**
     * Coloca paredes en el tablero de forma aleatoria hasta que todas las celdas vean como maximo el maxallowed
     */
    public void maxify(int maxAllowed){
        boolean tryAgain = true;
        int attempts = 0;
        while(tryAgain && attempts++ < 99){
            tryAgain = false;
            List<Cell> maxTiles = new ArrayList<>();
            for (int i = 0; i < boardSize_; i++)
                for (int j = 0; j < boardSize_; j++)
                    if(board_[i][j].getNumber() > maxAllowed) maxTiles.add(board_[i][j]);
            Cell chosenOne = maxTiles.get(OhnORandom.r.nextInt(maxTiles.size()));
            Cell[] cuts = chosenOne.getTilesInRange(1, maxAllowed);
            Cell cut = cuts[OhnORandom.r.nextInt(cuts.length)];
            if(cut != null) {
                cut.setCurrState(Cell.STATE.RED);
                setAllBlue(true);
                solve();
                tryAgain = true;
            }
            else{
                System.out.println("no cut found for", chosenOne.x, chosenOne.y, chosenOne.getNumber(), cuts, 1, maxAllowed);
            }
        }
    }

    /**
     * Consigue la información de las casillas alrededor de cada celda en las cuatro direcciones y la guarda.
     */
    public void collectInfo() {
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                Cell cell = board_[i][j];
                cell.resetInfo();
                int nPossibleDirections = 0;
                Direction singlePossibleDirection = null;
                for (Direction dir :
                        Direction.values()) {
                    int k = 1;
                    Cell dirCell;
                    boolean greyFound = false;
                    DirectionInfo dirInfo = cell.getDirectionInfo(dir); // TODO: mirar que se modifica el de la cell
                    while (inArray(i + dir.dx * k, j + dir.dy * k) &&
                            (dirCell = board_[i + dir.dx * k][j + dir.dy * k]).getCurrState() != Cell.STATE.RED){
                        if(dirCell.getCurrState() == Cell.STATE.GREY){
                            if(dirInfo.greysCount == 0)
                                dirInfo.numberWhenFillingFirstGrey++;
                            cell.setGreysAround(cell.getGreysAround() + 1);
                            dirInfo.greysCount++;
                            greyFound = true;
                        }
                        else if(dirCell.getCurrState() == Cell.STATE.BLUE){
                            if(dirInfo.greysCount == 0)
                                dirInfo.numberWhenFillingFirstGrey++;
                            cell.setCurNumber(cell.getCurNumber() + 1);
                            if(dirCell.isFixed()){
                                cell.setCompletedNumbersAround(true);
                            }
                            if(greyFound){
                                dirInfo.numberCountAfterGreys++;
                            }
                            if(dirInfo.greysCount == 1){
                                dirInfo.numberCountAfterGreys++;
                                if (dirInfo.numberCountAfterGreys + 1 > cell.getNumber()) {
                                    dirInfo.wouldBeTooMuch = true;
                                }
                            }
                        }
                        dirInfo.maxPossibleCount++;
                        ++k;
                    }
                    if(k > 1){
                        nPossibleDirections++;
                        singlePossibleDirection = dir;
                    }
                }
                for (Direction dir :
                        Direction.values()) {
                    DirectionInfo dirInfo = cell.getDirectionInfo(dir); // TODO: mirar que se modifica el de la cell
                    for (Direction other :
                            Direction.values()) {
                        DirectionInfo otherDirInfo = cell.getDirectionInfo(other); // TODO: mirar que se modifica el de la cell
                        if (dir == other)
                            continue;
                        dirInfo.maxPossibleCountInOtherDirections += otherDirInfo.maxPossibleCount;
                    }
                }
                if(nPossibleDirections == 1){
                    cell.setSinglePossibleDirection(singlePossibleDirection);
                }
                else{
                    cell.setSinglePossibleDirection(null);
                }
            }
        }
    }

    /**
     * Devuelve true si el tablero no tiene celdas grises.
     */
    private boolean isDone(boolean allowBlues) {
        for (int i = 0; i < boardSize_; i++)
            for (int j = 0; j < boardSize_; j++) {
                if (board_[i][j].getCurrState() == Cell.STATE.GREY ||
                        (!allowBlues && board_[i][j].getCurrState() == Cell.STATE.BLUE))
                    return false;
            }
        return true;
    }

    /**
     * Intenta resolver el tablero, devuelve la pista que ha usado
     * @param hintMode Si es verdadero, el tablero no lo modifica, si no, aplica la pista directamente
     */
    public boolean solve(boolean hintMode){
        boolean tryAgain = true;
        int attempts = 0;

        while (tryAgain && attempts++ < 99){
            tryAgain = false;

            if(isDone(false))
                return true;

            collectInfo();

            List<Integer> random = new ArrayList<>();
            for (int i = 0; i < boardSize_ * boardSize_; i++) {
                random.add(i);
            }
            Collections.shuffle(random);

            collectInfo();

            // Se recorren las casillas en orden aleatorio
            for (int k = 0; k < random.size(); k++) {
                int i = random.get(k) / boardSize_;
                int j = random.get(k) % boardSize_;
                Cell cell = board_[i][j];

                if(!hintMode && cell.getCurrState() == Cell.STATE.BLUE && cell.getGreysAround() == 0){
                    cell.setNumber(cell.getCurNumber());
                    cell.fixCell(Cell.STATE.BLUE);
                    break;
                }

                if(cell.getCurrState() == Cell.STATE.BLUE && cell.isFixed() && cell.getGreysAround() > 0){
                    if(cell.isCompleted()){
                        if(!hintMode)
                            cell.close();
                        hint = new Hint(Hint.HintType.VISIBLE_CELLS_COVERED, i, j);
                        break;
                    }

                    if(cell.getSinglePossibleDirection() != null){
                        if(!hintMode)
                            cell.closeDirection(info.singlePossibleDirection, true, 1);
                        hint = new Hint(Hint.HintType.MUST_PLACE_BLUE, i, j);
                        break;
                    }

                    for(Direction dir : Direction.values()){
                        DirectionInfo dirInfo = cell.getDirectionInfo(dir);
                        if(dirInfo.wouldBeTooMuch){
                            if(!hintMode)
                                cell.closeDirection(dir);
                            hint = new Hint(Hint.HintType.CANNOT_SURPASS_LIMIT, i, j);
                            break;
                        }

                        if(dirInfo.greysCount > 0 && dirInfo.numberWhenFillingFirstGrey + dirInfo.maxPossibleCountInOtherDirections <= cell.getNumber()){
                            if(!hintMode)
                                cell.closeDirection(dir, true, 1);
                            hint = new Hint(Hint.HintType.MUST_PLACE_BLUE, i, j);
                            break;
                        }
                    }
                }

                if(cell.getCurrState() == Cell.STATE.GREY && cell.getGreysAround() == 0 && cell.hasFixedBlueAround()){
                    if(cell.getCurNumber() == 0){
                        if(!hintMode)
                            cell.setCurrState(Cell.STATE.RED);
                        hint = new Hint(Hint.HintType.ISOLATED_AND_EMPTY, i, j);
                        break;
                    }
                }
            }
        }
        return false;
    }


    /**
     * Cuenta las celdas azules adyacentes a una dada
     */
    private int calculateNumber(int x, int y) {
        int count = 0;
        int[] newPos;
        newPos = nextDiffColorCell(x, y, 1, 0, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[0] - x - 1;
        } else count += newPos[0] - x;
        newPos = nextDiffColorCell(x, y, 0, 1, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[1] - y - 1;
        } else count += newPos[1] - y;
        newPos = nextDiffColorCell(x, y, -1, 0, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += x - newPos[0] - 1;
        } else count += x - newPos[0];
        newPos = nextDiffColorCell(x, y, 0, -1, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += y - newPos[1] - 1;
        } else count += y - newPos[1];
        return count;
    }

    /**
     * Busca la primera casilla del color dado
     * Si no hay, devuelve la última casilla que hay.
     */
    private Tuple<Integer, Integer> nextColorCell(int x, int y, Direction direction, Cell.STATE color) {
        int i = 1;
        while (inArray(x + direction.dx * i, y + direction.dy * i) &&
                board_[x + direction.dx * i][y + direction.dy * i].getCurrState() != color)
            i++;
        if (!inArray(x + direction.dx * i, y + direction.dy * i))
            --i;
        return new Tuple<>(x + direction.dx * i, y + direction.dy * i);
    }

    /**
     * Busca la primera casilla azul fijada
     * Si no hay, devuelve la última casilla que hay.
     */
    private Tuple<Integer, Integer> nextFixedBlueCell(int x, int y, Direction direction) {
        int i = 1;
        while (inArray(x + direction.dx * i, y + direction.dy * i) &&
                board_[x + direction.dx * i][y + direction.dy * i].getCurrState() != Cell.STATE.BLUE &&
                !board_[x + direction.dx * i][y + direction.dy * i].isFixed())
            i++;
        if (!inArray(x + direction.dx * i, y + direction.dy * i))
            --i;
        return new Tuple<>(x + direction.dx * i, y + direction.dy * i);
    }

    /**
     * Busca la primera casilla con color distinto al dado.
     * Si no hay, devuelve la última casilla que hay.
     */
    private Tuple<Integer, Integer> nextDiffColorCell(int x, int y, Direction direction, Cell.STATE color) {
        int i = 1;
        while (inArray(x + direction.dx * i, y + direction.dy * i) &&
                board_[x + direction.dx * i][y + direction.dy * i].getCurrState() == color)
            i++;
        if (!inArray(x + direction.dx * i, y + direction.dy * i))
            --i;
        return new Tuple<>(x + direction.dx * i, y + direction.dy * i);
    }

    /**
     * Comprueba que una posición no se sale del array.
     */
    private boolean inArray(int x, int y) {
        return ((x >= 0 && x < boardSize_) && (y >= 0 && y < boardSize_));
    }

    /**
     * Deep copy del tablero
     */
/*    public Board copy() {
        Board copy = new Board(boardSize);
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                if (board[i][j].isFixed()) {
                    copy.fixCell(i, j, board[i][j].getCurrState());
                    copy.setNumber(i, j, board[i][j].getNumber());
                }
            }
        }
        return copy;
    }*/
}
