package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.List;

public class Board {

    //Constantes de probabilidad del nivel
    private final float BLUE_PROB = 0.5f; //Probabilidad de que una celda sea azul en vez de roja en la solución
    private final float FIXED_PROB = 0.5f; //Probabilidad de que una celda sea fija

    private int boardSize;
    private Cell[][] board; //Tablero
    private List<Tuple<Integer, Integer>> previousMoves = new ArrayList<>(); //Lista de movimientos realizados

    private int numCells;
    private int fixedCells = 0;
    private int coloredCells = 0; // <---------------------------------------------------------------------------------Usar esta y la de arriba en la generacion de la board
    private List<Cell> fixedBlueCells = new ArrayList<>(); //Lista de celdas logicas azules fijas

    public Board(int size, int cellRadius) {

        numCells = size * size;
        boardSize = size;
        board = new Cell[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board[i][j] = new Cell(cellRadius);

        setNewBoard();
    }

    public boolean isFixed(int i, int j){
        return board[i][j].isFixed();
    }

    public Cell.STATE getCurrState(int i, int j){
        return board[i][j].getCurrState();
    }

    public int getNumber(int i, int j){
        return board[i][j].getNumber();
    }

    public void fixCell(int x, int y, Cell.STATE state) {
        board[x][y].fixCell(state);
        if (state == Cell.STATE.BLUE) fixedBlueCells.add(board[x][y]);
        fixedCells++;
    }

    public boolean changeCell(int x, int y) {
        board[x][y].changeState();
        previousMoves.add(new Tuple<>(x, y));

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
            coloredCells++;
        }
        else if (currState == CellLogic.STATE.GREY) {
            coloredCells--;
        }
        return contMistakes == 0;
    }

    public int donePercentage() {
        return Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100);
    }

    public Tuple<Integer, Integer> undoMove() {
        if (previousMoves.isEmpty()) return null;

        Tuple<Integer, Integer> cellPos = previousMoves.remove(previousMoves.size() - 1);
        board[cellPos.x][cellPos.y].revertState();

        CellLogic.STATE currState = cellPos.getCurrState();
        CellLogic.STATE prevState = cellPos.getPrevState();
        CellLogic.STATE solState = solBoard[cellPos.getX()][cellPos.getY()].getCurrState();

        //Si la celda ahora es gris entonces hay una celda coloreada menos y hay que actualizar el progreso
        //Si la celda era gris entonces hay una celda coloreada mas y hay que actualizar el progreso
        if (currState == CellLogic.STATE.GREY) {
            coloredCells--;
        } else if (prevState == CellLogic.STATE.GREY) {
            coloredCells++;
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

    //Crea la matriz que representa el nivel de un tamaño dado
    public void setNewBoard() {
        Hint hint = null;
        while (hint == null) {
            //Se fijan ciertas celdas con valores aleatorios
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    board[i][j].resetCell(); //Por si ha habido un intento anterior
                    if (OhnORandom.getRandomBoolean(FIXED_PROB)) {
                        if (OhnORandom.getRandomBoolean(BLUE_PROB)) {
                            board[i][j].fixCell(Cell.STATE.BLUE);
                            fixedBlueCells.add(board[i][j]);
                        }
                        else
                            board[i][j].fixCell(Cell.STATE.RED);
                    }
                }
            }
            for (Cell c : fixedBlueCells) {
                c.setNumber(Math.min(Math.max(calculateNumber(board, c.getX(), c.getY()), OhnORandom.r.nextInt(boardSize) + 1), boardSize));
            }

            // Se crea la solucion en solBoard
            contMistakes = boardSize * boardSize - fixedCells;
            solBoard = copyBoard(board);
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
                if (fixedCells + placedCells == numCells) {
                    // Comprueba que los numeros tienen sentido, si no, reinicia
                    for (int i = 0; i < fixedBlueCells.size(); ++i) {
                        Cell c = fixedBlueCells.get(i);
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

    //Cuenta las celdas azules adyacentes a una dada
    private int calculateNumber(Cell[][] mat, int x, int y) {
        int count = 0;
        int[] newPos;
        newPos = nextDiffColor(mat, x, y, 1, 0, Cell.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[0] - x - 1;
        } else count += newPos[0] - x;
        newPos = nextDiffColor(mat, x, y, 0, 1, Cell.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[1] - y - 1;
        } else count += newPos[1] - y;
        newPos = nextDiffColor(mat, x, y, -1, 0, Cell.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += x - newPos[0] - 1;
        } else count += x - newPos[0];
        newPos = nextDiffColor(mat, x, y, 0, -1, Cell.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += y - newPos[1] - 1;
        } else count += y - newPos[1];
        return count;
    }

    //Busca la primera casilla con color distinto al dado
    //Si no hay, devuelve la última casilla que hay.
    public int[] nextDiffColor(Cell[][] mat, int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while (inArray(mat, x + dx * i, y + dy * i) && mat[x + dx * i][y + dy * i].getCurrState() == color) {
            i++;
        }
        if (!inArray(mat, x + dx * i, y + dy * i)) {
            --i;
        }
        int[] res = {x + dx * i, y + dy * i};
        return res;
    }

    //Comprueba que una posición no se sale del array
    private boolean inArray(Cell[][] mat, int x, int y) {
        return ((x >= 0 && x < mat.length) && (y >= 0 && y < mat[0].length));
    }

    /**
     * Deep copy del tablero
     */
    public Board copy() {
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
    }

    public void setNumber(int x, int y, int number){
        board[x][y].setNumber(number);
    }
}
