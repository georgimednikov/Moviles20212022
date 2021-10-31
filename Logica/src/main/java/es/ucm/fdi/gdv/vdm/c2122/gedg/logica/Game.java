package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;
import java.util.Random;
import java.util.*;


public class Game {

    private final boolean DEBUG = true;

    private final float blueProb = 0.8f; //Porbabilidad de que una celda sea azul en vez de roja en la solución
    private final float fixedProb = 0.5f; //Probabilidad de que una celda sea fija

    int contMistakes = 0; //Número de celdas mal puestas
    private Cell[][] board;
    private Vector<Cell> fixedBlueCells = new Vector<Cell>();
    private boolean solved = false;

    public Game(int size) {
        createBoard(size);
    }

    static private Random rand = new Random(System.currentTimeMillis());
    static private boolean getRandomBoolean(float p){
        assert p > 1.0f && p < 0.0f: String.format("getRandomBoolean recibe un número entre 0 y 1: (%d)", p);
        return rand.nextFloat() < p;
    }

    public void showInConsole(){
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[0].length; ++j) {
                Cell.STATE s = board[i][j].getCurrState();
                switch (s) {
                    case RED:
                        System.out.print("r ");
                        break;
                    case BLUE:
                        System.out.print(board[i][j].getNumber() + " ");
                        break;
                    case GREY:
                        System.out.print("0 ");
                        break;
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    //Crea la matriz que representa el nivel de un tamaño dado
    private void createBoard(int size) {
        // Crea los objetos
        // Primero coloca un numero de casillas en posiciones aleatorias
        board = new Cell[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                board[i][j] = new Cell(i, j);
            }
        }

        int totalCells = size * size;
        Hint hint = null;
        outer:
        while (hint == null) {
            int fixedCells = 0;
            if(!DEBUG) {
                //Se fijan ciertas celdas con valores aleatorios
                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        board[i][j].resetCell(); //Por si ha habido un intento anterior
                        if (getRandomBoolean(0.4f)) {
                            if (getRandomBoolean(blueProb)) {
                                board[i][j].fixCell(Cell.STATE.BLUE, rand.nextInt(size) + 1);
                                fixedBlueCells.add(board[i][j]);
                            } else
                                board[i][j].fixCell(Cell.STATE.RED);
                            fixedCells++;
                        }
                    }
                }
            }
            else {
                readFromConsole();
            }
            Cell[][] mat = copyBoard(board);
            if(DEBUG) showInConsole();
            int placedCells = 0;
            boolean tryAgain = true;
            int attempts = 0;
            while (tryAgain && attempts++ < 99) {
                hint = giveHint(mat);
                tryAgain = hint != null;
                if(tryAgain) {
                    mat[hint.x_][hint.y_].applyHint(hint);
                    placedCells++;
                    if (fixedCells + placedCells == totalCells) break outer;
                }
            }
        }
    }

    private void readFromConsole(){
        for(int i = 0; i < board.length; ++i){
            for(int j = 0; j < board.length; ++j) {
                char c = '?';
                try {
                    do {
                        c = (char) System.in.read();
                    }while(c == ' ' || c == '\n');
                } catch (Exception e){
                    for (int x = 0; x < board.length; ++x) {
                        for (int y = 0; y < board.length; ++y) {
                            board[x][y].resetCell(); //Por si ha habido un intento anterior
                        }
                    }
                    System.out.println("MAL formato");
                    readFromConsole();
                    return;
                }
                switch (c) {
                    case 'r':
                        board[i][j].fixCell(Cell.STATE.RED);
                        break;
                    default: // numeros
                        int num = Character.getNumericValue(c);
                        if(num == -1){
                            for (int x = 0; x < board.length; ++x) {
                                for (int y = 0; y < board.length; ++y) {
                                    board[x][y].resetCell(); //Por si ha habido un intento anterior
                                }
                            }
                            System.out.println("MAL numero");
                            readFromConsole();
                            return;
                        }
                        board[i][j].fixCell(Cell.STATE.BLUE, num);
                        break;
                }
            }
        }
    }

    private Cell[][] copyBoard(Cell[][] orig) {
        Cell[][] copy = new Cell[orig.length][orig[0].length];
        for (int i = 0; i < orig.length; ++i) {
            for (int j = 0; j < orig[0].length; ++j) {
                copy[i][j] = new Cell(orig[i][j].getX(), orig[i][j].getY());
                if(orig[i][j].isFixed()) copy[i][j].fixCell(orig[i][j].getSolState(), orig[i][j].getNumber());
            }
        }
        return copy;
    }

    //Busca la primera casilla con color distinto al dado
    //Si no hay, devuelve la última casilla que hay.
    public int[] nextDiffColor(Cell[][] mat, int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while(inArray(mat, x + dx * i, y + dy *i) && mat[x + dx * i][y + dy *i].getSolState() == color) {
            i++;
        }
        if(!inArray(mat, x + dx * i, y + dy *i)){
            --i;
        }
        int[] res = { x + dx * i, y + dy *i };
        return res;
    }

    //Busca la primera casilla del color dado
    //Si no hay, devuelve la última casilla que hay.
    public int[] nextColorCell(Cell[][] mat, int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while(inArray(mat, x + dx * i, y + dy *i) && mat[x + dx * i][y + dy *i].getSolState() != color) {
            i++;
        }
        if(!inArray(mat, x + dx * i, y + dy *i)){
            --i;
        }
        int[] res = { x + dx * i, y + dy *i };
        return res;
    }

    //Cuenta las celdas azules adyacentes a una dada
    private int calculateNumber(Cell[][] mat, int x, int y) {
        int count = 0;
        int[] newPos;
        newPos = nextDiffColor(mat, x, y, 1, 0, Cell.STATE.BLUE);
        if(mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[0] - x -1;
        } else count += newPos[0] - x;
        newPos = nextDiffColor(mat, x, y, 0, 1, Cell.STATE.BLUE);
        if(mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += newPos[1] - y - 1;
        } else count += newPos[1] - y;
        newPos = nextDiffColor(mat, x, y, -1, 0, Cell.STATE.BLUE);
        if(mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += x - newPos[0] - 1;
        } else count += x - newPos[0];
        newPos = nextDiffColor(mat, x, y, 0, -1, Cell.STATE.BLUE);
        if(mat[newPos[0]][newPos[1]].getCurrState() != Cell.STATE.BLUE) {
            count += y - newPos[1] - 1;
        } else count += y - newPos[1];
        return count;
    }

    //Comprueba que una posición no se sale del array
    private boolean inArray(Cell[][] mat, int x, int y) {
        return ((x >= 0 && x < mat.length) && (y >= 0 &&  y < mat[0].length));
    }

    //Calcula la distancia entre dos casillas
    private int distanceBetweenPos(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    //Cambia el estado de una celda. Si ha resuelto el nivel, devuelve true
    public boolean changeCell(int i, int j) {
        if (!board[i][j].isFixed()) {
            if (board[i][j].isRight()) {
                contMistakes++;
            }
            if (board[i][j].changeState()) {
                contMistakes--;
                if (contMistakes == 0) solved = true;
            }
            return true;
        }
        return false;
    }

//region Hints
    public Hint giveHint(Cell[][] mat) {
        Hint hint = new Hint();
        //Pistas basadas en celdas fijas
        for (int i = 0; i < fixedBlueCells.size(); ++i){
            if (getHintFixedCell(hint, mat, fixedBlueCells.get(i))) return hint;
        }
        //Pistas basadas en celdas ordinarias
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[0].length; ++j) {
                if (board[i][j].isFixed()) continue;
                if (getHintRegularCell(hint, mat, board[i][j])) return hint;
            }
        }
        return null; //No se han encontrado pistas
    }

    private boolean getHintFixedCell(Hint hint, Cell[][] mat, Cell cell) {
        int curCount = calculateNumber(mat, cell.getX(), cell.getY()); //Número correcto de azules adyacentes
        //Pistas que requieren mirar cada direccion
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                if (hint_VISIBLE_CELLS_COVERED(hint, mat, cell, i, j, curCount) ||
                        hint_CANNOT_SURPASS_LIMIT(hint, mat, cell, i, j, curCount) ||
                        hint_MUST_PLACE_BLUE(hint, mat, cell, i, j, curCount)) return true;
            }
        }
        return false;
    }

    private boolean getHintRegularCell(Hint hint, Cell[][] mat, Cell cell) {
        Cell.STATE state = cell.getCurrState();
        //Se mira si se aplican unas pistas u otras dependiendo del color de la celda
        switch (state) {
            case GREY:
                if (hint_BLUE_BUT_ISOLATED(hint, mat, cell)) {
                    hint.type_ = Hint.HintType.ISOLATED_AND_EMPTY;
                    return true;
                }
            case BLUE:
            case RED:
                return false; //Si hubiera pistas que se basan en casillas rojas/azules
            default:
                return false;
        }
    }

    //Metodo que da pistas especificas para el usuario
    //Expande las que usa el generador de niveles contemplando posibles errores del usuario
    private boolean getHintFixedCell_user(Hint hint, Cell[][] mat, Cell cell) {
        if(getHintFixedCell(hint, mat, cell)) return true;
        // Caso en el que el usuario se ha equivocado
        if (hint_TOO_MANY_ADJACENT(hint, mat, cell) ||
                hint_NOT_ENOUGH_BUT_CLOSED(hint, mat, cell)) return true;
        return false;
    }

    //Metodo que da pistas especificas para el usuario
    //Expande las que usa el generador de niveles contemplando posibles errores del usuario
    private boolean getHintRegularCell_user(Hint hint, Cell[][] mat, Cell cell) {
        if(getHintRegularCell(hint, mat, cell)) return true;
        // Caso en el que el usuario se ha equivocado
        if (cell.getCurrState() == Cell.STATE.BLUE) {
            return hint_BLUE_BUT_ISOLATED(hint, mat, cell);
        }
        return false;
    }

    //region Fixed Hints
    private boolean hint_VISIBLE_CELLS_COVERED(Hint hint, Cell[][] mat, Cell cell, int i, int j, int cont) {
        if (cont != cell.getNumber()) return false; //Si no ha llegado al numero correcto pasamos
        int[] newPos = nextDiffColor(mat, cell.getX(), cell.getY(), i, j, Cell.STATE.BLUE);
        //Busca en la direccion i j la siguiente casilla no azul; si es gris, esta abierta y hay que cerrarla
        if (mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.GREY) {
            hint.type_ = Hint.HintType.VISIBLE_CELLS_COVERED;
            hint.x_ = newPos[0];
            hint.y_ = newPos[1];
            return true;
        }
        return false;
    }

    private boolean hint_CANNOT_SURPASS_LIMIT(Hint hint, Cell[][] mat, Cell cell, int i, int j, int cont) {
        if (cont >= cell.getNumber()) return false; //Si se ha llegado al numero correcto pasamos
        int[] newPos = nextDiffColor(mat, cell.getX(), cell.getY(), i, j, Cell.STATE.BLUE);
        //Si la siguiente casilla en la direccion i j es gris puede haber camino, así que miramos si hacerlo supera el numero
        if (mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.GREY) {
            int[] newNewPos = nextDiffColor(mat, newPos[0], newPos[1], i, j, Cell.STATE.BLUE);
            int newCont;
            //Si la siguiente celda no es azul no se ha salido de la matriz y hay que volver a la ultima azul, la anterior
            //Si es azul, se ha salido de la matriz y ha devuelto la ultima azul
            if (mat[newNewPos[0]][newNewPos[1]].getCurrState() != Cell.STATE.BLUE) {
                newCont = distanceBetweenPos(cell.getX(), cell.getY(), newNewPos[0] - i, newNewPos[1] - j);
            }
            else newCont = distanceBetweenPos(cell.getX(), cell.getY(), newNewPos[0], newNewPos[1]);
            //Si poner la casilla gris en azul supera el numero correcto
            if(cont + newCont > cell.getNumber()){
                hint.type_ = Hint.HintType.CANNOT_SURPASS_LIMIT;
                hint.x_ = newPos[0];
                hint.y_ = newPos[1];
                return true;
            }
        }
        return false;
    }

    private boolean hint_MUST_PLACE_BLUE(Hint hint, Cell[][] mat, Cell cell, int i, int j, int thisBlues) {
        if(thisBlues >= cell.getNumber()) return false;
        int x = cell.getX(); int y = cell.getY();
        int[] thisDirRed = nextColorCell(mat, x, y, i, j, Cell.STATE.RED);
        // Si es roja, no ha llegado al final y hay que contar hasta esa excluyendola
        if(mat[thisDirRed[0]][thisDirRed[1]].getCurrState() == Cell.STATE.RED) {
            thisDirRed[0] -= i;
            thisDirRed[1] -= j;
        }
        int otherBlues = 0;
        for(int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                if ((k + l == 2) || (k + l == 0) || (k + l == -2)) continue;
                if(i == k && j == l) continue; //No miramos la direccion a evaluar
                int[] otherDirRed = nextColorCell(mat, x, y, k, l, Cell.STATE.RED);
                // Si es roja, no ha llegado al final y hay que contar hasta esa excluyendola
                if(mat[otherDirRed[0]][otherDirRed[1]].getCurrState() == Cell.STATE.RED) {
                    otherDirRed[0] -= k;
                    otherDirRed[1] -= l;
                }
                otherBlues += distanceBetweenPos(x, y, otherDirRed[0], otherDirRed[1]);
            }
        }
        //Si las otras 3 direcciones juntas mas las casillas azules que ya ve no llegan al numero correcto
        if(otherBlues < cell.getNumber()){
            hint.x_ = thisDirRed[0];
            hint.y_ = thisDirRed[1];
            hint.type_ = Hint.HintType.MUST_PLACE_BLUE;
            return true;
        }
        return false;
    }
    //endregion

    //region Regular Hints
    private boolean hint_TOO_MANY_ADJACENT(Hint hint, Cell[][] mat, Cell cell) {
        if (calculateNumber(mat, cell.getX(), cell.getY()) <= cell.getNumber()) return false;
        hint.x_ = cell.getX();
        hint.y_ = cell.getY();
        hint.type_ = Hint.HintType.TOO_MANY_ADJACENT;
        return true;
    }

    private boolean hint_NOT_ENOUGH_BUT_CLOSED(Hint hint, Cell[][] mat, Cell cell) {
        int x = cell.getX(); int y = cell.getY();
        int blueVisible = 0;
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                int[] firstRed = nextDiffColor(mat, x, y, i, j, Cell.STATE.BLUE);
                // Si es gris, no se ha cerrado
                if (mat[firstRed[0]][firstRed[1]].getCurrState() == Cell.STATE.GREY) return false;
                //Si es roja hay que retroceder para no contarla
                if (mat[firstRed[0]][firstRed[1]].getCurrState() != Cell.STATE.BLUE)
                    blueVisible += distanceBetweenPos(cell.getX(), cell.getY(), firstRed[0] - i, firstRed[1] - j);
                else blueVisible += distanceBetweenPos(cell.getX(), cell.getY(), firstRed[0], firstRed[1]);
            }
        }
        //Si ve las que tiene que ver esta pista no aplica
        if (blueVisible >= cell.getNumber()) return false;
        hint.x_ = cell.getX();
        hint.y_ = cell.getY();
        hint.type_ = Hint.HintType.NOT_ENOUGH_BUT_CLOSED;
        return true;
    }

    private boolean hint_BLUE_BUT_ISOLATED(Hint hint, Cell[][] mat, Cell cell) {
        int x = cell.getX(); int y = cell.getY();
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                int newX = x + i, newY = y + j;
                //Para cada adyacente si no es roja no esta aislada
                if (inArray(mat, newX, newY) && mat[newX][newY].getCurrState() != Cell.STATE.RED) return false;
            }
        }
        hint.x_ = cell.getX();
        hint.y_ = cell.getY();
        hint.type_ = Hint.HintType.BLUE_BUT_ISOLATED;
        return true;
    }
    //endregion
//endregion
}
