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
        DOWN(0, -1, 3);

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

    private int boardSize_;
    private Cell[][] board_; //Tablero
    private List<Tuple<Integer, Integer>> previousMoves_ = new ArrayList<>(); //Lista con las posiciones de las celdas modificadas

    private int numCells_;
    private int fixedCells_ = 0; //Número total de celdas fijas (no modificables)
    private int coloredCells_ = 0; //Número de celdas no grises
    private int numberTries; //Número de intentos que se realizan en el algoritmo de generación.

    public Board(int size) {
        numCells_ = size * size;
        boardSize_ = size;
        numberTries = (boardSize_ * boardSize_) * 5; //Numero de intentos que vaya acorde del tamaño

        //Se crea e inicializa el tablero
        board_ = new Cell[size][size];
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++)
                board_[i][j] = new Cell();

        //Se establecen las celdas para una partida
        setNewBoard(size);
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
     * Cicla el estado de la celda. Devuelve true si se completa el nivel.
     */
    public boolean changeCell(int x, int y) {
        Cell.STATE prevState = board_[x][y].getCurrState();
        board_[x][y].changeState();
        Cell.STATE currState = board_[x][y].getCurrState();
        previousMoves_.add(new Tuple<>(x, y)); //Se añade a la lista de movimientos realizados.

        if (prevState == Cell.STATE.GREY) {
            coloredCells_++;
        }
        else if (currState == Cell.STATE.GREY) {
            coloredCells_--;
        }

        return solve(true);
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
        Cell.STATE prevState = board_[cellPos.x][cellPos.y].getCurrState();
        board_[cellPos.x][cellPos.y].revertState();
        Cell.STATE currState = board_[cellPos.x][cellPos.y].getCurrState();

        //Si la celda ahora es gris entonces hay una celda coloreada menos y hay que actualizar el progreso
        //Si la celda era gris entonces hay una celda coloreada mas y hay que actualizar el progreso
        if (prevState == Cell.STATE.GREY) {
            coloredCells_++;
        }
        else if (currState == Cell.STATE.GREY) {
            coloredCells_--;
        }

        return cellPos;
    }

    /**
     * Crea un nivel en el tablero.
     */
    public void setNewBoard(int boardSize) {
        // Pone las celdas a azul
        setAllBlue(false);
        // Mira las direcciones y cuenta el maximo de azules que ve en cada direccion
        collectInfoPass1();
        // Pone las celdas a azul con su numero correcto
        setAllBlue(true);
        // Hace que las celdas azules vean hasta boardSize otras azules
        maxify(boardSize);
        // Pone celdas en grises mientras la solucion sea unica
        breakDown();

        //Se cuentan las celdas fijas y se asignan las a 0 las coloreadas que ha puesto el usuario.
        fixedCells_ = coloredCells_ = 0;
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                if (board_[i][j].isFixed()) fixedCells_++;
            }
        }
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
                    board_[i][j].fixCell(Cell.STATE.BLUE);
                    board_[i][j].setNumber(board_[i][j].getCurNumber());
                }
            }
        }
    }

    /**
     * Recoge las cells que estan en el rango [min, max] de x,y
     */
    public Cell[] getCellsInRange(int x, int y, int min, int max){
        List<Cell> res = new ArrayList<>();
        for (Direction dir :
                Direction.values()) {
            int k = 1;
            while(inArray(x + dir.dx * k, y + dir.dy * k)){
                Cell otherCell = board_[x + dir.dx * k][y + dir.dy * k];
                if(otherCell.getCurrState() == Cell.STATE.RED) break;
                if(k >= min && k <= max){
                    res.add(otherCell);
                }
                k++;
            }
        }
        Cell[] cells = new Cell[res.size()];
        for (int i = 0; i < res.size(); i++) {
            cells[i] = res.get(i);
        }
        return cells;
    }

    /**
     * Coloca paredes en el tablero de forma aleatoria hasta que todas las celdas vean como maximo el maxallowed
     * Asigna el tablero solucion
     */
    public void maxify(int maxAllowed){
        boolean tryAgain = true;
        int attempts = 0;
        while(tryAgain && attempts++ < numberTries){
            tryAgain = false;
            List<Tuple<Integer, Integer>> maxCells = new ArrayList<>();
            for (int i = 0; i < boardSize_; i++)
                for (int j = 0; j < boardSize_; j++)
                    if(board_[i][j].getNumber() > maxAllowed) maxCells.add(new Tuple<>(i, j));
            if(maxCells.size() == 0)
                break;
            int random = OhnORandom.r.nextInt(maxCells.size());
            Cell[] cuts = getCellsInRange(maxCells.get(random).x, maxCells.get(random).y, 1, maxAllowed);

            if (cuts.length > 0){
                Cell cut = cuts[OhnORandom.r.nextInt(cuts.length)];
                cut.fixCell(Cell.STATE.RED);
                cut.setNumber(-1);
                setAllBlue(true);
                collectInfoPass1();
                for (int i = 0; i < boardSize_; i++) {
                    for (int j = 0; j < boardSize_; j++) {
                        Cell cell = board_[i][j];
                        if(cell.getCurrState() == Cell.STATE.BLUE && cell.getGreysAround() == 0){
                            if(cell.getCurNumber() > 0) {
                                cell.setNumber(cell.getCurNumber());
                                cell.fixCell(Cell.STATE.BLUE);
                            } else {
                                cell.setNumber(-1);
                                cell.fixCell(Cell.STATE.RED);
                            }
                        }
                    }
                }
                tryAgain = true;
            }
        }
    }

    /**
     * Consigue la informacion de las casillas alrededor de cada celda en las cuatro direcciones y la guarda
     */
    public void collectInfoPass1() {
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                Cell cell = board_[i][j];
                cell.resetInfo();
                Direction singlePossibleDirection = null;
                for (Direction dir :
                        Direction.values()) {
                    int k = 1;
                    Cell dirCell;
                    DirectionInfo dirInfo = cell.getDirectionInfo(dir);
                    while (inArray(i + dir.dx * k, j + dir.dy * k) &&
                            (dirCell = board_[i + dir.dx * k][j + dir.dy * k]).getCurrState() != Cell.STATE.RED){
                        if(dirCell.getCurrState() == Cell.STATE.BLUE){
                            if(dirInfo.greysCount == 0) {
                                cell.setCurNumber(cell.getCurNumber() + 1);
                                dirInfo.numberWhenFillingFirstGrey++;
                            }

                            if(dirInfo.greysCount == 1){
                                dirInfo.numberCountAfterGreys++;
                                dirInfo.numberWhenFillingFirstGrey++;
                                if (dirInfo.numberCountAfterGreys + 1 > cell.getNumber())
                                    dirInfo.wouldBeTooMuch = true;
                            }
                        }
                        else { // GREY
                            cell.setGreysAround(cell.getGreysAround() + 1);

                            if(dirInfo.greysCount == 0)
                                dirInfo.numberWhenFillingFirstGrey++;

                            dirInfo.greysCount++;
                        }

                        dirInfo.maxPossibleCount++;

                        ++k;
                    }
                }

                for (Direction dir :
                        Direction.values()) {
                    DirectionInfo dirInfo = cell.getDirectionInfo(dir);
                    int possibleDirections = 0;

                    for (Direction other :
                            Direction.values()) {
                        DirectionInfo otherDirInfo = cell.getDirectionInfo(other);
                        if (dir == other)
                            continue;

                        if(otherDirInfo.greysCount > 0)
                            possibleDirections++;

                        dirInfo.maxPossibleCountInOtherDirections += otherDirInfo.maxPossibleCount;
                    }

                    if(possibleDirections == 0 && dirInfo.greysCount > 0)
                        singlePossibleDirection = dir;
                }

                cell.setSinglePossibleDirection(singlePossibleDirection);
            }
        }
    }

    /**
     * Una vez tiene informacion intrinseca de cada celda, obtiene informacion extra del resto de celdas
     */
    public void collectInfoPass2(){
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                Cell cell = board_[i][j];
                for (Direction dir :
                        Direction.values()) {
                    int k = 1;
                    Cell dirCell;

                    while (inArray(i + dir.dx * k, j + dir.dy * k) &&
                            (dirCell = board_[i + dir.dx * k][j + dir.dy * k]).getCurrState() != Cell.STATE.RED) {
                        if(dirCell.isCompleted() && dirCell.getCurrState() == Cell.STATE.BLUE) // TODO: esto esta mal seguramente
                            cell.setCompletedBlueAround(true);

                        k++;
                    }
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
                        (!allowBlues && board_[i][j].getCurrState() == Cell.STATE.BLUE && !board_[i][j].isFixed()))
                    return false;
            }
        return true;
    }

    /**
     * Pone paredes en los extremos (al final de los azules) de la celda dada
     */
    public void closeCell(int x, int y){
        for (Direction dir :
                Direction.values()) {
            fillDirectionCell(x, y, dir, Cell.STATE.RED);
        }
    }

    /**
     * Pone el color color al encontrarse un gris a partir de x,y siguiendo la direccion dir
     */
    public void fillDirectionCell(int x, int y, Direction dir, Cell.STATE color){
        int k = 1;
        while(inArray(x + dir.dx * k, y + dir.dy * k)){
            Cell otherCell = board_[x + dir.dx * k][y + dir.dy * k];
            if(otherCell.getCurrState() == Cell.STATE.GREY){
                otherCell.setCurrState(color);
                break;
            } else
                k++;
        }
    }

    public void render(Cell[][] board){
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                Cell c = board[i][j];
                String s;
                switch (c.getCurrState()){
                    case RED:
                    default:
                        s = "R ";
                        break;
                    case GREY:
                        s = "_ ";
                        break;
                    case BLUE:
                        s = c.getNumber() + " ";
                        break;
                }
                System.out.print(s);
            }
            System.out.println();
        }
    }

    /**
     * Intenta resolver el tablero, devuelve true si el tablero esta resuelto y false en caso contrario
     * @param hintMode Si es verdadero, el tablero no lo modifica, si no, aplica la pista directamente
     */
    public boolean solve(boolean hintMode){
        board_ = new Cell[5][5];
        board_[0][0] = new Cell(-1, Cell.STATE.RED, true);
        board_[1][0] = new Cell(4, Cell.STATE.BLUE, true);
        board_[2][0] = new Cell(2, Cell.STATE.BLUE, true);
        board_[3][0] = new Cell(2, Cell.STATE.BLUE, true);
        board_[4][0] = new Cell(-1, Cell.STATE.RED, true);
        board_[0][1] = new Cell(2, Cell.STATE.BLUE, true);
        board_[1][1] = new Cell(3, Cell.STATE.BLUE, true);
        board_[2][1] = new Cell(-1, Cell.STATE.RED, true);
        board_[3][1] = new Cell(-1, Cell.STATE.RED, true);
        board_[4][1] = new Cell(3, Cell.STATE.BLUE, true);
        board_[0][2] = new Cell(3, Cell.STATE.BLUE, true);
        board_[1][2] = new Cell(2, Cell.STATE.BLUE, true);
        board_[2][2] = new Cell(-1, Cell.STATE.RED, true);
        board_[3][2] = new Cell(1, Cell.STATE.BLUE, true);
        board_[4][2] = new Cell(4, Cell.STATE.BLUE, true);
        board_[0][3] = new Cell(-1, Cell.STATE.RED, true);
        board_[1][3] = new Cell(-1, Cell.STATE.RED, true);
        board_[2][3] = new Cell(-1, Cell.STATE.RED, true);
        board_[3][3] = new Cell(-1, Cell.STATE.RED, true);
        board_[4][3] = new Cell(3, Cell.STATE.BLUE, true);
        board_[0][4] = new Cell(1, Cell.STATE.GREY, false);
        board_[1][4] = new Cell(1, Cell.STATE.GREY, false);
        board_[2][4] = new Cell(-1, Cell.STATE.RED, true);
        board_[3][4] = new Cell(1, Cell.STATE.BLUE, true);
        board_[4][4] = new Cell(4, Cell.STATE.BLUE, true);

        boolean tryAgain = true;
        int attempts = 0;

        while (tryAgain && attempts++ < numberTries){
            tryAgain = false;

            if(isDone(false))
                return true;

            collectInfoPass1();

            List<Integer> random = new ArrayList<>();
            for (int i = 0; i < boardSize_ * boardSize_; i++) {
                random.add(i);
            }
            Collections.shuffle(random);

            collectInfoPass2();

            // Se recorren las casillas en orden aleatorio
            for (int k = 0; k < random.size(); k++) {
                int i = random.get(k) / boardSize_;
                int j = random.get(k) % boardSize_;
                Cell cell = board_[i][j];

                if(cell.getCurrState() == Cell.STATE.BLUE && cell.isFixed() && cell.getGreysAround() > 0){
                    if(cell.isCompleted()){
                        if(!hintMode)
                            closeCell(i, j);
                        hint = new Hint(Hint.HintType.VISIBLE_CELLS_COVERED, i, j);
                        System.out.print(hint != null ? Hint.hintText[hint.type.ordinal()] + " " : "");
                        System.out.print(hint != null ? hint.x + " " : "");
                        System.out.println(hint != null ? hint.y + " " : "");//TODO
                        tryAgain = true;
                        break;
                    }

                    if(cell.getSinglePossibleDirection() != null){
                        if(!hintMode)
                            fillDirectionCell(i, j, cell.getSinglePossibleDirection(), Cell.STATE.BLUE);
                        hint = new Hint(Hint.HintType.MUST_PLACE_BLUE, i, j);
                        System.out.print(hint != null ? Hint.hintText[hint.type.ordinal()] + " " : "");
                        System.out.print(hint != null ? hint.x + " " : "");
                        System.out.println(hint != null ? hint.y + " " : "");//TODO
                        tryAgain = true;
                        break;
                    }

                    for(Direction dir : Direction.values()){
                        DirectionInfo dirInfo = cell.getDirectionInfo(dir);
                        if(dirInfo.wouldBeTooMuch){
                            if(!hintMode)
                                fillDirectionCell(i, j, dir, Cell.STATE.RED);
                            hint = new Hint(Hint.HintType.CANNOT_SURPASS_LIMIT, i, j);
                            System.out.print(hint != null ? Hint.hintText[hint.type.ordinal()] + " " : "");
                            System.out.print(hint != null ? hint.x + " " : "");
                            System.out.println(hint != null ? hint.y + " " : "");//TODO
                            tryAgain = true;
                            break;
                        }

                        if(dirInfo.greysCount > 0 && dirInfo.numberWhenFillingFirstGrey + dirInfo.maxPossibleCountInOtherDirections <= cell.getNumber()){
                            if(!hintMode)
                                fillDirectionCell(i, j, dir, Cell.STATE.BLUE);
                            hint = new Hint(Hint.HintType.MUST_PLACE_BLUE, i, j);
                            System.out.print(hint != null ? Hint.hintText[hint.type.ordinal()] + " " : "");
                            System.out.print(hint != null ? hint.x + " " : "");
                            System.out.println(hint != null ? hint.y + " " : "");//TODO
                            tryAgain = true;
                            break;
                        }
                    }
                    if(tryAgain)
                        break;
                }

                if(cell.getCurrState() == Cell.STATE.GREY && cell.getCurNumber() == 0 && cell.getGreysAround() == 0 && !cell.getCompletedBlueAround()){
                    if(!hintMode)
                        cell.setCurrState(Cell.STATE.RED);
                    hint = new Hint(Hint.HintType.ISOLATED_AND_EMPTY, i, j);
                    System.out.print(hint != null ? Hint.hintText[hint.type.ordinal()] + " " : "");
                    System.out.print(hint != null ? hint.x + " " : "");
                    System.out.println(hint != null ? hint.y + " " : "");//TODO
                    tryAgain = true;
                    break;
                }
            }
        }
        return false;
    }

    /**
     * Coloca celdas grises en el tablero mientras tenga una solucion unica
     */
    public void breakDown(){
        boolean tryAgain = true;
        int attempts = 0;
        int reds = 0;
        int minReds = 1;
        Cell cell;
        List<Tuple<Integer, Integer>> cellPool = new ArrayList<>();
        render(board_); // TODO
        System.out.println();
        System.out.println();
        for (int i = 0; i < boardSize_; i++) {
            for (int j = 0; j < boardSize_; j++) {
                cell = board_[i][j];
                cellPool.add(new Tuple<>(i, j));

                if (cell.getCurrState() == Cell.STATE.RED)
                    reds++;
            }
        }

        while (tryAgain && cellPool.size() > 0 && attempts++ < numberTries){
            tryAgain = false;
            Cell[][] save1 = copyBoard();

            Tuple<Integer, Integer> poolCell = cellPool.remove(OhnORandom.r.nextInt(cellPool.size()));
            cell = board_[poolCell.x][poolCell.y];
            boolean isRed = cell.getCurrState() == Cell.STATE.RED;

            if (isRed && reds <= minReds) continue;

            int cellNum = cell.getNumber();
            cell.resetCell(); // Comentar
            cell.setNumber(cellNum);
            Cell[][] save2 = copyBoard();
            render(board_); // TODO
            if (solve(false)) {
                if (isRed) reds--;
                board_ = save2;
            }
            else {
                board_ = save1;
            }
            tryAgain = true;
            System.out.println();
            System.out.println();
        }
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
    public Cell[][] copyBoard() {
        Cell[][] copy = new Cell[boardSize_][boardSize_];
        for (int i = 0; i < boardSize_; ++i) {
            for (int j = 0; j < boardSize_; ++j) {
                copy[i][j] = new Cell();
                if(board_[i][j].isFixed()) {
                    copy[i][j].fixCell(board_[i][j].getCurrState());
                }
                else {
                    copy[i][j].setCurrState(board_[i][j].getCurrState());
                }
                copy[i][j].setNumber(board_[i][j].getNumber());
            }
        }
        return copy;
    }
}
