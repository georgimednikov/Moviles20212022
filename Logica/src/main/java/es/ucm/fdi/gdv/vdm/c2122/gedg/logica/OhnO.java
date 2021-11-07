package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;
import java.util.Random;
import java.util.*;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;


public class OhnO implements Application {

    private enum State {START, LEVEL_SELECTION, GAME}
    Engine eng_;
    private final boolean DEBUG = false;
    private State currState_ = State.START;
    private final float blueProb = 0.7f; //Probabilidad de que una celda sea azul en vez de roja en la solución
    private final float fixedProb = 0.5f; //Probabilidad de que una celda sea fija

    int contMistakes = 0; //Número de celdas mal puestas
    private Cell[][] board;
    private Cell[][] solBoard;
    private List<Cell> fixedBlueCells = new ArrayList<>(); //ESTO ERA UN VECTOR NO CREO HABER ROTO NADA PERO POR SI AK
    private List<Cell> previousMoves = new ArrayList<>();
    private boolean solved = false;

    public OhnO(int size, char[][] mat) {
        createBoard(size, mat);
        showInConsole(board);
    }

    public OhnO(int size) {
        createBoard(size, null);
        showInConsole(board);
    }
    @Override
    public void setEngine(Engine eng) {
        this.eng_ = eng;
    }

    static private Random rand = new Random(System.currentTimeMillis());
    static private boolean getRandomBoolean(float p){
        assert p > 1.0f && p < 0.0f: String.format("getRandomBoolean recibe un número entre 0 y 1: (%d)", p);
        return rand.nextFloat() < p;
    }

    @Override
    public boolean init() {
        createBoard(4, null);
        return true;
    }

    @Override
    public void update() {
        switch (currState_) {
            case START:
                break;
            case LEVEL_SELECTION:
                break;
            case GAME:
                break;
        }
    }

    @Override
    public void render() {
        Graphics g = eng_.getGraphics();
        //g.clear(new Color(50, 0, 200, 0));
        switch (currState_) {
            case START:
                //Image logo = g.newImage("assets/sprites/q42.png"); g.drawImage(logo, g.getWidth() / 2, g.getHeight() / 2, 50, 75, true);
                Font ohno = g.newFont("assets/fonts/Molle-Regular.ttf", 100, false); g.drawText(ohno, "Oh nO", g.getWidth() / 2, g.getHeight() / 2, true);
                //Font jugar = g.newFont("assets/fonts/JosefinSans-Bold.ttf", 60, true); g.drawText(jugar, "Jugar", g.getWidth() / 2, g.getHeight() / 2, true);
                break;
            case LEVEL_SELECTION:
                break;
            case GAME:
                break;
        }
    }

    @Override
    public boolean close() {
        return false;
    }

    //Crea la matriz que representa el nivel de un tamaño dado
    private void createBoard(int size, char[][] mat) {
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
        while (hint == null) {
            fixedBlueCells = new Vector<Cell>();
            int fixedCells = 0;
            if(!DEBUG) {
                //Se fijan ciertas celdas con valores aleatorios
                for (int i = 0; i < size; ++i) {
                    for (int j = 0; j < size; ++j) {
                        board[i][j].resetCell(); //Por si ha habido un intento anterior
                        if (getRandomBoolean(fixedProb)) {
                            if (getRandomBoolean(blueProb)) {
                                board[i][j].fixCell(Cell.STATE.BLUE);
                                fixedBlueCells.add(board[i][j]);
                            } else
                                board[i][j].fixCell(Cell.STATE.RED);
                            fixedCells++;
                        }
                    }
                }
                for(Cell c : fixedBlueCells){
                    c.setNumber(Math.min(Math.max(calculateNumber(board, c.getX(), c.getY()), rand.nextInt(size) + 1), size));
                }
            }
            else {
                fixedCells = readFromConsole(mat);
            }
            // Se crea la solucion en solBoard
            contMistakes = size * size - fixedCells;
            solBoard = copyBoard(board);
            //showInConsole(board); // DEBUG
            int placedCells = 0;
            boolean tryAgain = true;
            int attempts = 0;
            // Intenta rellenar la matriz auxiliar con las pistas, si no es capaz, no es resoluble
            tries:
            while (tryAgain && attempts++ < 99) {
                hint = giveHint(solBoard);
                tryAgain = hint != null;
                if(tryAgain) {
                    solBoard[hint.x_][hint.y_].applyHint(hint);
                    placedCells++;
                    //showInConsole(matAux); // DEBUG
                }
                if (fixedCells + placedCells == totalCells){
                    // Comprueba que los numeros tienen sentido, si no, reinicia
                    for(int i = 0; i < fixedBlueCells.size(); ++i) {
                        Cell c = fixedBlueCells.get(i);
                        if(calculateNumber(solBoard, c.getX(), c.getY()) != c.getNumber()){
                            hint = null;
                            //showInConsole(matAux); // DEBUG
                            break tries;
                        }
                    }
                    return;
                }
            }
        }
    }

    private void doMove(Cell cell) {
        previousMoves.add(cell);
        cell.changeState();
    }

    private boolean undoMove() {
        if (previousMoves.isEmpty()) return false;
        Cell cell = previousMoves.remove(previousMoves.size() - 1);
        cell.revertState();
        return true;
    }

    //region DEBUG
    public void showInConsole(Cell[][] mat){
        for (int i = 0; i < mat.length; ++i) {
            for (int j = 0; j < mat[0].length; ++j) {
                Cell.STATE s = mat[i][j].getCurrState();
                switch (s) {
                    case RED:
                        System.out.print("r ");
                        break;
                    case BLUE:
                        if(mat[i][j].isFixed())
                            System.out.print(mat[i][j].getNumber() + " ");
                        else System.out.print("b ");
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
    //endregion

    //region Auxiliary Methods
    private int readFromConsole(char[][] mat){
        int fixedCells = 0;
        for(int i = 0; i < board.length; ++i){
            for(int j = 0; j < board.length; ++j) {
                char c = mat[i][j];

                switch (c) {
                    case 'r':
                        board[i][j].fixCell(Cell.STATE.RED);
                        fixedCells++;
                        break;
                    case '0':
                        break;
                    default: // numeros
                        int num = Character.getNumericValue(c);
                        board[i][j].fixCell(Cell.STATE.BLUE, num);
                        fixedCells++;
                        fixedBlueCells.add(board[i][j]);
                        break;
                }
            }
        }
        return fixedCells;
    }

    private Cell[][] copyBoard(Cell[][] orig) {
        Cell[][] copy = new Cell[orig.length][orig[0].length];
        for (int i = 0; i < orig.length; ++i) {
            for (int j = 0; j < orig[0].length; ++j) {
                copy[i][j] = new Cell(orig[i][j].getX(), orig[i][j].getY());
                if (orig[i][j].isFixed())
                    copy[i][j].fixCell(orig[i][j].getCurrState(), orig[i][j].getNumber());
            }
        }
        return copy;
    }

    //Busca la primera casilla con color distinto al dado
    //Si no hay, devuelve la última casilla que hay.
    public int[] nextDiffColor(Cell[][] mat, int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while(inArray(mat, x + dx * i, y + dy *i) && mat[x + dx * i][y + dy *i].getCurrState() == color) {
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
        while(inArray(mat, x + dx * i, y + dy *i) && mat[x + dx * i][y + dy *i].getCurrState() != color) {
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
    public boolean changeCell(int x, int y) {
        if (!board[x][y].isFixed()) {
            board[x][y].changeState();
            if(board[x][y] != solBoard[x][y]){
                contMistakes++;
            } else{
                contMistakes--;
            }
            return contMistakes == 0;
        }
        return false;
    }
    //endregion

//region Hints
    public Hint giveHint(Cell[][] mat) {
        Hint hint = new Hint();
        //Pistas basadas en celdas fijas
        for (int i = 0; i < fixedBlueCells.size(); ++i){
            if (getHintFixedCell(hint, mat, fixedBlueCells.get(i))) return hint;
        }
        //Pistas basadas en celdas ordinarias
        for (int i = 0; i < mat.length; ++i) {
            for (int j = 0; j < mat[0].length; ++j) {
                if (mat[i][j].isFixed()) continue;
                if (getHintRegularCell(hint, mat, mat[i][j])) return hint;
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
            if ((newPos[0] == newNewPos[0] && newPos[1] == newNewPos[1]) || mat[newNewPos[0]][newNewPos[1]].getCurrState() == Cell.STATE.BLUE) {
                newCont = distanceBetweenPos(newPos[0] - i, newPos[1] - j, newNewPos[0], newNewPos[1]);
            }
            else newCont = distanceBetweenPos(newPos[0], newPos[1], newNewPos[0], newNewPos[1]);
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
                int[] otherNextNoBlue = nextDiffColor(mat, x, y, k, l, Cell.STATE.BLUE);
                // Si es gris, no ha llegado al final y hay que contar hasta esa excluyendola
                if(mat[otherNextNoBlue[0]][otherNextNoBlue[1]].getCurrState() != Cell.STATE.BLUE) {
                    otherNextNoBlue[0] -= k;
                    otherNextNoBlue[1] -= l;
                }
                // Se suman las que se añadirian si se uniese desde la gris hasta la roja
                otherBlues += distanceBetweenPos(otherNextNoBlue[0], otherNextNoBlue[1], otherDirRed[0], otherDirRed[1]);
            }
        }
        //Si las otras 3 direcciones juntas mas las casillas azules que ya ve no llegan al numero correcto
        if(otherBlues + thisBlues < cell.getNumber()){
            int[] thisFirstGrey = nextColorCell(mat, x, y, i, j, Cell.STATE.GREY);
            if(mat[thisFirstGrey[0]][thisFirstGrey[1]].getCurrState() != Cell.STATE.GREY){
                return false;
            }
            hint.x_ = thisFirstGrey[0];
            hint.y_ = thisFirstGrey[1];
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
                //Para direccion buscar si se puede poner rojo porque no hay una azul bloqueada que podria llegar a esta
                int adv = 1;
                while (inArray(mat, x + i * adv, y + j * adv) &&
                        (mat[x + i * adv][y + j * adv].getCurrState() != Cell.STATE.RED)) {
                    if ((mat[x + i * adv][y + j * adv].getCurrState() == Cell.STATE.BLUE && mat[x + i * adv][y + j * adv].isFixed()))
                        return false;
                    adv++;
                }
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
