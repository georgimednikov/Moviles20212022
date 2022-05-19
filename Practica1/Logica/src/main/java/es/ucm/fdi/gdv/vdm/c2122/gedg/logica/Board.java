package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que representa la lógica del tablero y lo que esto implica.
 * Crea el tablero y guarda su estado.
 */
public class Board {

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
        Hint hint = null;
        while (hint == null) {
            //Se fijan ciertas celdas con valores aleatorios
            for (int i = 0; i < boardSize_; ++i) {
                for (int j = 0; j < boardSize_; ++j) {
                    board_[i][j].resetCell(); //Por si ha habido un intento anterior
                    if (OhnORandom.getRandomBoolean(FIXED_PROB)) {
                        if (OhnORandom.getRandomBoolean(BLUE_PROB)) {
                            board_[i][j].fixCell(Cell.STATE.BLUE);
                            fixedBlueCells_.add(board_[i][j]);
                        }
                        else
                            board_[i][j].fixCell(Cell.STATE.RED);
                    }
                }
            }
            for (Cell c : fixedBlueCells_) {
                c.setNumber(Math.min(Math.max(calculateNumber(board_, c.getX(), c.getY()), OhnORandom.r.nextInt(boardSize_) + 1), boardSize_));
            }

            // Se crea la solucion en solBoard
            contMistakes = boardSize_ * boardSize_ - fixedCells_;
            solBoard = copyBoard(board_);
            int placedCells = 0;
            boolean tryAgain = true;
            int attempts = 0;
            // Intenta rellenar la matriz auxiliar con las pistas, si no es capaz, no es resoluble
            tries:
            while (tryAgain && attempts++ < 99) {
                hint = giveHint(solBoard);
                tryAgain = hint != null;
                if (tryAgain) {
                    solBoard[hint.i][hint.j].applyHint(hint);
                    placedCells++;
                }
                if (fixedCells_ + placedCells == numCells_) {
                    // Comprueba que los numeros tienen sentido, si no, reinicia
                    for (int i = 0; i < fixedBlueCells_.size(); ++i) {
                        Cell c = fixedBlueCells_.get(i);
                        if (calculateNumber(solBoard, c.getX(), c.getY()) != c.getNumber()) {
                            hint = null;
                            break tries;
                        }
                    }
                    return;
                }
            }
        }
    }

    /**
     * Cuenta las celdas azules adyacentes a una dada. Recibe el tablero
     */
    private int calculateNumber(int x, int y) {
        int count = 0;
        int[] newPos;
        newPos = nextDiffColor(x, y, 1, 0, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[0] - x - 1;
        } else count += newPos[0] - x;
        newPos = nextDiffColor(x, y, 0, 1, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[1] - y - 1;
        } else count += newPos[1] - y;
        newPos = nextDiffColor(x, y, -1, 0, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += x - newPos[0] - 1;
        } else count += x - newPos[0];
        newPos = nextDiffColor(x, y, 0, -1, Cell.STATE.BLUE);
        if (board_[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += y - newPos[1] - 1;
        } else count += y - newPos[1];
        return count;
    }

    //

    /**
     * Busca la primera casilla con color distinto al dado.
     * Si no hay, devuelve la última casilla que hay.
     */
    private int[] nextDiffColor(int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while (inArray(x + dx * i, y + dy * i) && board_[x + dx * i][y + dy * i].getCurrState() == color) {
            i++;
        }
        if (!inArray(x + dx * i, y + dy * i)) {
            --i;
        }
        int[] res = {x + dx * i, y + dy * i};
        return res;
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
