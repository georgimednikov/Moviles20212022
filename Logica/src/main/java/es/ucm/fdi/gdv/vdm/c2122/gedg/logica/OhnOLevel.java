package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.Random;
import java.util.*;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;


public class OhnOLevel implements Application {

    Engine eng_;
    private final boolean DEBUG = false;
    private final float blueProb = 0.7f; //Probabilidad de que una celda sea azul en vez de roja en la solución
    private final float fixedProb = 0.5f; //Probabilidad de que una celda sea fija

    int contMistakes = 0; //Número de celdas mal puestas
    private Cell[][] board;
    private Cell[][] solBoard;
    private List<Cell> fixedBlueCells = new ArrayList<>(); //ESTO ERA UN VECTOR NO CREO HABER ROTO NADA PERO POR SI AK
    private List<Cell> previousMoves = new ArrayList<>();
    private boolean solved = false;

    //Variables de Cell
    private boolean fixedTapped = false;
    private int boardOffsetX = 0;
    private int boardOffsetY = 0;
    private int cellSeparation = 0;
    private int cellRadius = 0;

    //Variables de boton
    private int buttonOffsetX = 0;
    private int buttonOffsetY = 0;
    private int buttonSeparation = 0;
    private int buttonSize = 0;
    private int numButtons = 3;

    //Variables de pista
    private boolean givingHint = false;
    private int highlightRadius = 0;
    private int highlightPosX = 0;
    private int highlightPosY = 0;
    private String infoText;

    //DEBUG
    public OhnOLevel(int size, char[][] mat) {
        createBoard(size, mat);
        showInConsole(board);
    }

    public OhnOLevel(int size) {
        createBoard(size, null);
        showInConsole(board);
    }

    static private Random rand = new Random(System.currentTimeMillis());

    static private boolean getRandomBoolean(float p) {
        assert p > 1.0f && p < 0.0f : String.format("getRandomBoolean recibe un número entre 0 y 1: (%d)", p);
        return rand.nextFloat() < p;
    }

    //Cambia el estado de una celda. Si ha resuelto el nivel, devuelve true
    public void changeCell(int x, int y) {
        resetInterface();
        if (!board[x][y].isFixed()) {
            board[x][y].changeState();
            previousMoves.add(board[x][y]);
            if (board[x][y].getCurrState() != solBoard[x][y].getCurrState()) {
                contMistakes++;
            } else {
                contMistakes--;
            }
            solved = contMistakes == 0;
        }
    }

    private void undoMove() {
        if (previousMoves.isEmpty()) {
            infoText = "No queda nada por hacer";
            return;
        }
        ;
        Cell cell = previousMoves.remove(previousMoves.size() - 1);
        switch (cell.revertState()) {
            case BLUE:
                infoText = "Esta celda a vuelto a azul";
                break;
            case GREY:
                infoText = "Esta celda a vuelto a su estado desconocido";
                break;
            case RED:
                infoText = "Esta celda a vuelto a rojo";
                break;
        }
    }

    @Override
    public void setEngine(Engine eng) {
        this.eng_ = eng;
    }

    @Override
    public boolean init() {
        createBoard(4, null);
        return true;
    }

    @Override
    public void update() {
        if (solved) return;
        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        while (!events.isEmpty()) {
            event = events.remove(0);
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            for (int i = 0; i < board.length; ++i) {
                for (int j = 0; j < board.length; ++j) {
                    if (checkCollisionCircle(
                            boardOffsetX + cellRadius * (i + 1) + cellSeparation * i,
                            boardOffsetY + cellRadius * (j + 1) + cellSeparation * j,
                            cellRadius, event.x, event.y)) {
                        if (!board[i][j].isFixed()) changeCell(i, j);
                        else fixedTapped = true;
                        continue;
                    }
                }
            }
            for (int i = 0; i < numButtons; ++i) {
                if (checkCollisionCircle(
                        buttonOffsetX + cellRadius * (i + 1) + buttonSeparation * i,
                        buttonOffsetY,
                        cellRadius, event.x, event.y)) {
                    switch (i) {
                        case 0:
                            //Salir
                            break;
                        case 1:
                            undoMove();
                            break;
                        case 2:
                            if (givingHint) givingHint = false;
                            else {
                                givingHint = true;
                                Hint hint = giveHint_user();
                                highlightPosX = hint.x + cellRadius;
                                highlightPosY = hint.y + cellRadius;
                                infoText = hint.hintText[hint.type.ordinal()];
                            }
                            break;
                    }
                    continue;
                }
            }
        }
    }

    @Override
    public void render() {
        Graphics g = eng_.getGraphics();
        //g.clear(new Color(50, 0, 200, 0));
        Font info = g.newFont("assets/fonts/JosefinSans-Bold.ttf", 50, false);
        g.drawText(info, infoText, g.getWidth() / 2, g.getHeight() / 8, true);
        Color blue = new Color(0, 0, 255, 255);
        Color grey = new Color(120, 120, 120, 255);
        Color red = new Color(255, 0, 0, 255);
        if (givingHint) {
            g.setColor(new Color(0, 0, 0, 255));
            g.fillCircle(highlightPosX, highlightPosY, highlightRadius);
        }
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board.length; ++j) {
                switch (board[i][j].getCurrState()) {
                    case BLUE:
                        g.setColor(blue);
                        break;
                    case GREY:
                        g.setColor(grey);
                        break;
                    case RED:
                        g.setColor(red);
                        break;
                }
                g.fillCircle(boardOffsetX + cellRadius * (i + 1) + cellSeparation * i, boardOffsetY + cellRadius * (j + 1) + cellSeparation * j, cellRadius);
            }
        }
        Image icon1 = g.newImage("assets/sprites/close.png");
        g.drawImage(icon1, buttonOffsetX, buttonOffsetY, 50, 50, false);
        Image icon2 = g.newImage("assets/sprites/history.png");
        g.drawImage(icon2, buttonOffsetX + (buttonSize + buttonSeparation), buttonOffsetY, 50, 50, false);
        Image icon3 = g.newImage("assets/sprites/eye.png");
        g.drawImage(icon3, buttonOffsetX + (buttonSize + buttonSeparation) * 2, buttonOffsetY, 50, 50, false);
    }

    @Override
    public boolean close() {
        return false;
    }

    //region DEBUG
    public void showInConsole(Cell[][] mat) {
        for (int i = 0; i < mat.length; ++i) {
            for (int j = 0; j < mat[0].length; ++j) {
                Cell.STATE s = mat[i][j].getCurrState();
                switch (s) {
                    case RED:
                        System.out.print("r ");
                        break;
                    case BLUE:
                        if (mat[i][j].isFixed())
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
    private int readFromConsole(char[][] mat) {
        int fixedCells = 0;
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board.length; ++j) {
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
            if (!DEBUG) {
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
                for (Cell c : fixedBlueCells) {
                    c.setNumber(Math.min(Math.max(calculateNumber(board, c.getX(), c.getY()), rand.nextInt(size) + 1), size));
                }
            } else {
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
                if (tryAgain) {
                    solBoard[hint.x][hint.y].applyHint(hint);
                    placedCells++;
                    //showInConsole(matAux); // DEBUG
                }
                if (fixedCells + placedCells == totalCells) {
                    // Comprueba que los numeros tienen sentido, si no, reinicia
                    for (int i = 0; i < fixedBlueCells.size(); ++i) {
                        Cell c = fixedBlueCells.get(i);
                        if (calculateNumber(solBoard, c.getX(), c.getY()) != c.getNumber()) {
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
        while (inArray(mat, x + dx * i, y + dy * i) && mat[x + dx * i][y + dy * i].getCurrState() == color) {
            i++;
        }
        if (!inArray(mat, x + dx * i, y + dy * i)) {
            --i;
        }
        int[] res = {x + dx * i, y + dy * i};
        return res;
    }

    //Busca la primera casilla del color dado
    //Si no hay, devuelve la última casilla que hay.
    public int[] nextColorCell(Cell[][] mat, int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while (inArray(mat, x + dx * i, y + dy * i) && mat[x + dx * i][y + dy * i].getCurrState() != color) {
            i++;
        }
        if (!inArray(mat, x + dx * i, y + dy * i)) {
            --i;
        }
        int[] res = {x + dx * i, y + dy * i};
        return res;
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

    //Comprueba que una posición no se sale del array
    private boolean inArray(Cell[][] mat, int x, int y) {
        return ((x >= 0 && x < mat.length) && (y >= 0 && y < mat[0].length));
    }

    //Calcula la distancia entre dos casillas
    private int distanceBetweenPos(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    private boolean checkCollisionBox(int x, int y, int w, int h, int eventX, int eventY) {
        return (eventX >= x && eventX <= (x + w) &&
                eventY >= y && eventY <= (y + h));
    }

    private boolean checkCollisionCircle(int x, int y, int radius, int eventX, int eventY) {
        int vecX = Math.abs(x - eventX);
        int vecY = Math.abs(y - eventY);
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }

    private void resetInterface() {
        infoText = board.length + " x " + board.length;
        fixedTapped = givingHint = false;
    }
    //endregion

    //region Hints
    public Hint giveHint(Cell[][] mat) {
        Hint hint = new Hint();
        //Pistas basadas en celdas fijas
        for (int i = 0; i < fixedBlueCells.size(); ++i) {
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
        for (int i = -1; i <= 1; ++i) {
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
                    hint.type = Hint.HintType.ISOLATED_AND_EMPTY;
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
    public Hint giveHint_user() {
        Hint hint = new Hint();
        //Pistas basadas en celdas fijas
        for (int i = 0; i < fixedBlueCells.size(); ++i) {
            if (getHintFixedCell(hint, board, fixedBlueCells.get(i))) return hint;
        }
        //Pistas basadas en celdas ordinarias
        for (int i = 0; i < board.length; ++i) {
            for (int j = 0; j < board[0].length; ++j) {
                if (board[i][j].isFixed()) continue;
                if (getHintRegularCell(hint, board, board[i][j])) return hint;
            }
        }
        return null; //No se han encontrado pistas
    }

    //Metodo que da pistas especificas para el usuario
    //Expande las que usa el generador de niveles contemplando posibles errores del usuario
    private boolean getHintFixedCell_user(Hint hint, Cell[][] mat, Cell cell) {
        if (getHintFixedCell(hint, mat, cell)) return true;
        // Caso en el que el usuario se ha equivocado
        if (hint_TOO_MANY_ADJACENT(hint, mat, cell) ||
                hint_NOT_ENOUGH_BUT_CLOSED(hint, mat, cell)) return true;
        return false;
    }

    //Metodo que da pistas especificas para el usuario
    //Expande las que usa el generador de niveles contemplando posibles errores del usuario
    private boolean getHintRegularCell_user(Hint hint, Cell[][] mat, Cell cell) {
        if (getHintRegularCell(hint, mat, cell)) return true;
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
            hint.type = Hint.HintType.VISIBLE_CELLS_COVERED;
            hint.x = newPos[0];
            hint.y = newPos[1];
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
            } else newCont = distanceBetweenPos(newPos[0], newPos[1], newNewPos[0], newNewPos[1]);
            //Si poner la casilla gris en azul supera el numero correcto
            if (cont + newCont > cell.getNumber()) {
                hint.type = Hint.HintType.CANNOT_SURPASS_LIMIT;
                hint.x = newPos[0];
                hint.y = newPos[1];
                return true;
            }
        }
        return false;
    }

    private boolean hint_MUST_PLACE_BLUE(Hint hint, Cell[][] mat, Cell cell, int i, int j, int thisBlues) {
        if (thisBlues >= cell.getNumber()) return false;
        int x = cell.getX();
        int y = cell.getY();

        int otherBlues = 0;
        for (int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                if ((k + l == 2) || (k + l == 0) || (k + l == -2)) continue;
                if (i == k && j == l) continue; //No miramos la direccion a evaluar
                int[] otherDirRed = nextColorCell(mat, x, y, k, l, Cell.STATE.RED);
                // Si es roja, no ha llegado al final y hay que contar hasta esa excluyendola
                if (mat[otherDirRed[0]][otherDirRed[1]].getCurrState() == Cell.STATE.RED) {
                    otherDirRed[0] -= k;
                    otherDirRed[1] -= l;
                }
                int[] otherNextNoBlue = nextDiffColor(mat, x, y, k, l, Cell.STATE.BLUE);
                // Si es gris, no ha llegado al final y hay que contar hasta esa excluyendola
                if (mat[otherNextNoBlue[0]][otherNextNoBlue[1]].getCurrState() != Cell.STATE.BLUE) {
                    otherNextNoBlue[0] -= k;
                    otherNextNoBlue[1] -= l;
                }
                // Se suman las que se añadirian si se uniese desde la gris hasta la roja
                otherBlues += distanceBetweenPos(otherNextNoBlue[0], otherNextNoBlue[1], otherDirRed[0], otherDirRed[1]);
            }
        }
        //Si las otras 3 direcciones juntas mas las casillas azules que ya ve no llegan al numero correcto
        if (otherBlues + thisBlues < cell.getNumber()) {
            int[] thisFirstGrey = nextColorCell(mat, x, y, i, j, Cell.STATE.GREY);
            if (mat[thisFirstGrey[0]][thisFirstGrey[1]].getCurrState() != Cell.STATE.GREY) {
                return false;
            }
            hint.x = thisFirstGrey[0];
            hint.y = thisFirstGrey[1];
            hint.type = Hint.HintType.MUST_PLACE_BLUE;
            return true;
        }
        return false;
    }
    //endregion

    //region Regular Hints
    private boolean hint_TOO_MANY_ADJACENT(Hint hint, Cell[][] mat, Cell cell) {
        if (calculateNumber(mat, cell.getX(), cell.getY()) <= cell.getNumber()) return false;
        hint.x = cell.getX();
        hint.y = cell.getY();
        hint.type = Hint.HintType.TOO_MANY_ADJACENT;
        return true;
    }

    private boolean hint_NOT_ENOUGH_BUT_CLOSED(Hint hint, Cell[][] mat, Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        int blueVisible = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                int[] firstRed = nextDiffColor(mat, x, y, i, j, Cell.STATE.BLUE);
                // Si es gris, no se ha cerrado
                if (mat[firstRed[0]][firstRed[1]].getCurrState() == Cell.STATE.GREY) return false;
                //Si es roja hay que retroceder para no contarla
                if (mat[firstRed[0]][firstRed[1]].getCurrState() != Cell.STATE.BLUE)
                    blueVisible += distanceBetweenPos(cell.getX(), cell.getY(), firstRed[0] - i, firstRed[1] - j);
                else
                    blueVisible += distanceBetweenPos(cell.getX(), cell.getY(), firstRed[0], firstRed[1]);
            }
        }
        //Si ve las que tiene que ver esta pista no aplica
        if (blueVisible >= cell.getNumber()) return false;
        hint.x = cell.getX();
        hint.y = cell.getY();
        hint.type = Hint.HintType.NOT_ENOUGH_BUT_CLOSED;
        return true;
    }

    private boolean hint_BLUE_BUT_ISOLATED(Hint hint, Cell[][] mat, Cell cell) {
        int x = cell.getX();
        int y = cell.getY();
        for (int i = -1; i <= 1; ++i) {
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
        hint.x = cell.getX();
        hint.y = cell.getY();
        hint.type = Hint.HintType.BLUE_BUT_ISOLATED;
        return true;
    }

    //endregion
//endregion
}
