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

    int boardSize;
    int numCells;
    int coloredCells = 0;
    int fixedCells = 0;
    int contMistakes = 0; //Número de celdas mal puestas
    private Cell[][] board;
    private Cell[][] solBoard;
    private List<Cell> fixedBlueCells = new ArrayList<>(); //ESTO ERA UN VECTOR NO CREO HABER ROTO NADA PERO POR SI AK
    private List<Cell> previousMoves = new ArrayList<>();
    private boolean solved = false;

    //Variables de Cell
    private boolean fixedTapped = false;
    private int boardOffsetX = 30;
    private int boardOffsetY = 130;
    private int cellSeparation = -1; //Asignacion dinamica
    private int cellRadius = -1; //Asignacion dinamica

    //Variables de boton
    private int buttonOffsetX = 75;
    private int buttonOffsetY = 530;
    private int buttonSeparation = -1; //Asignacion dinamica
    private int buttonSize = 40;
    private int numButtons = 3;

    //Variables de pista
    private boolean givingHint = false;
    private int highlightRadius = -1; //Asignacion dinamica
    private int highlightPosX = 0;
    private int highlightPosY = 0;

    //Textos
    private int infoPosY = 75;
    private int progressPosY = 500;
    private int infoRegSize = 60;
    private int infoHintSize = 25;
    private Font infoFont;
    private Font progressFont;
    private Font numberFont;
    private String infoText;
    private String progressText;
    private Color black = new Color(0, 0, 0, 255);
    private Color blue = new Color(72, 193, 228, 255);
    private Color grey = new Color(238, 237, 239, 255);
    private Color darkGrey = new Color(150, 150, 150, 255);
    private Color red = new Color(245, 53, 73, 255);
    private Color white = new Color(255, 255, 255, 255);
    private Image quitImage;
    private Image undoImage;
    private Image hintImage;
    private Image lockImage;

    public OhnOLevel(int size) {
        boardSize = size;
    }

    public boolean changeCell(int x, int y) {
        resetInterface();
        if (!board[x][y].isFixed()) {
            Cell.STATE oldState = board[x][y].getCurrState();
            board[x][y].changeState();
            previousMoves.add(board[x][y]);

            if (oldState == solBoard[x][y].getCurrState()) {
                contMistakes++;
            } else if (board[x][y].getCurrState() == solBoard[x][y].getCurrState()){
                contMistakes--;
            }
            solved = contMistakes == 0;
        }
        return board[x][y].getCurrState() != Cell.STATE.GREY;
    }

    private void undoMove() {
        infoFont.setSize(infoHintSize);
        if (previousMoves.isEmpty()) {
            infoText = "No queda nada por hacer";
            return;
        }
        Cell cell = previousMoves.remove(previousMoves.size() - 1);
        switch (cell.revertState()) {
            case BLUE:
                infoText = "Esta celda a vuelto a azul";
                break;
            case GREY:
                infoText = "Esta celda a vuelto a gris";
                break;
            case RED:
                infoText = "Esta celda a vuelto a rojo";
                break;
        }
    }

    @Override
    public void setEngine(Engine eng) {
        this.eng_ = eng;
        Graphics g = eng_.getGraphics(); //TODO: ESTO NO DEBERIA IR AQUI CREO

        int paintArea = eng_.getGraphics().getWidth() - 2 * boardOffsetX;
        cellRadius = (int)((paintArea * 0.9) / 2) / boardSize;
        cellSeparation = (int)(paintArea * 0.1) / (boardSize-1);
        int buttonArea = eng_.getGraphics().getWidth() - 2 * buttonOffsetX;
        buttonSeparation = (buttonArea - (buttonSize * numButtons)) / (numButtons - 1);
        highlightRadius = (int)Math.round(cellRadius * 1.1);

        infoFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", black, infoRegSize, true);
        progressFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", darkGrey, 25, false);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", white, cellRadius, false);
        quitImage = g.newImage("assets/sprites/close.png");
        undoImage = g.newImage("assets/sprites/history.png");
        hintImage = g.newImage("assets/sprites/eye.png");
        lockImage = g.newImage("assets/sprites/lock.png");
        infoText = boardSize + " x " + boardSize;
    }

    @Override
    public void update() {
        if (solved) {
            Application app = new OhnOMenu();
            eng_.setApplication(app);
            app.setEngine(eng_);
            return;
        }
        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        next:
        while (!events.isEmpty()) {
            event = events.remove(0);
            eng_.getGraphics().fillCircle(event.x, event.y, 10);
            if (event.type != TouchEvent.TouchType.PRESS) continue; //TODO: ESTO NO DEBERIA SER ASI (?)
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    if (checkCollisionCircle(
                            boardOffsetX + cellRadius * (i + 1) + (cellSeparation + cellRadius) * i,
                            boardOffsetY + cellRadius * (j + 1) + (cellSeparation + cellRadius) * j,
                            cellRadius, event.x, event.y)) {
                        if (!board[j][i].isFixed()) {
                            Cell.STATE oldstate = board[j][i].getCurrState();
                            changeCell(j, i);
                            if (oldstate == Cell.STATE.GREY) coloredCells++;
                            else if (board[j][i].getCurrState() == Cell.STATE.GREY) coloredCells--;
                            progressText = Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%";
                        }
                        else fixedTapped = !fixedTapped;
                        continue next;
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
                            OhnOMenu app = new OhnOMenu();
                            eng_.setApplication(app);
                            app.setEngine(eng_);
                            break;
                        case 1:
                            undoMove();
                            break;
                        case 2:
                            if (givingHint) {
                                givingHint = false;
                                resetInterface();
                            }
                            else {
                                givingHint = true;
                                Hint hint = giveHint_user();
                                highlightPosX = boardOffsetX + cellRadius * (hint.j + 1) + (cellSeparation + cellRadius) * hint.j;
                                highlightPosY = boardOffsetY + cellRadius * (hint.i + 1) + (cellSeparation + cellRadius) * hint.i;
                                infoText = hint.hintText[hint.type.ordinal()];
                                infoFont.setSize(infoHintSize);
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
        g.clear(white);
        g.drawText(infoFont, infoText, g.getWidth() / 2, infoPosY, true);
        g.drawText(progressFont, progressText, g.getWidth() / 2, progressPosY, true);
        if (givingHint) {
            g.setColor(black);
            g.fillCircle(highlightPosX, highlightPosY, highlightRadius);
        }
        for (int i = 0; i < boardSize; ++i) {
            g.save();
            g.translate(boardOffsetX + cellRadius, boardOffsetY + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < boardSize; ++j) {
                Cell.STATE cellState = board[i][j].getCurrState();
                switch (cellState) {
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
                g.fillCircle(0, 0, cellRadius);
                if (fixedTapped && cellState == Cell.STATE.RED)
                    g.drawImage(lockImage, 0, 0, cellRadius, cellRadius, true);
                if (board[i][j].isFixed() && cellState == Cell.STATE.BLUE)
                    g.drawText(numberFont, "" + board[i][j].getNumber(), 0, 0, true);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.drawImage(quitImage, buttonOffsetX, buttonOffsetY, buttonSize, buttonSize, false);
        g.drawImage(undoImage, buttonOffsetX + (buttonSize + buttonSeparation), buttonOffsetY, buttonSize, buttonSize, false);
        g.drawImage(hintImage, buttonOffsetX + (buttonSize + buttonSeparation) * 2, buttonOffsetY, buttonSize, buttonSize, false);
        g.setColor(black);
    }

    @Override
    public boolean init() {
        createBoard(boardSize);
        return true;
    }
    @Override
    public boolean close() {
        return true;
    }

    //region Auxiliary Methods
    static private Random rand = new Random(System.currentTimeMillis());

    static private boolean getRandomBoolean(float p) {
        assert p > 1.0f && p < 0.0f : String.format("getRandomBoolean recibe un número entre 0 y 1: (%d)", p);
        return rand.nextFloat() < p;
    }

    private int readFromConsole(char[][] mat) {
        int fixedCells = 0;
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
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
    private void createBoard(int size) {
        // Crea los objetos
        // Primero coloca un numero de casillas en posiciones aleatorias
        board = new Cell[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                board[i][j] = new Cell(i, j);
            }
        }
        boardSize = size;
        numCells = size * size;
        Hint hint = null;
        while (hint == null) {
            fixedCells = 0;
            fixedBlueCells = new Vector<Cell>();
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
            // Se crea la solucion en solBoard
            contMistakes = size * size - fixedCells;
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
                    progressText = Math.round((float)coloredCells / (float)numCells * 100) + "%";
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

    private boolean checkCollisionCircle(int centerX, int centerY, int radius, int eventX, int eventY) {
        int vecX = eventX - centerX;
        int vecY = eventY - centerY;
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }

    private void resetInterface() {
        infoText = boardSize + " x " + boardSize;
        infoFont.setSize(infoRegSize);
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
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
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
            hint.i = newPos[0];
            hint.j = newPos[1];
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
                hint.i = newPos[0];
                hint.j = newPos[1];
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
            hint.i = thisFirstGrey[0];
            hint.j = thisFirstGrey[1];
            hint.type = Hint.HintType.MUST_PLACE_BLUE;
            return true;
        }
        return false;
    }
    //endregion

    //region Regular Hints
    private boolean hint_TOO_MANY_ADJACENT(Hint hint, Cell[][] mat, Cell cell) {
        if (calculateNumber(mat, cell.getX(), cell.getY()) <= cell.getNumber()) return false;
        hint.i = cell.getX();
        hint.j = cell.getY();
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
        hint.i = cell.getX();
        hint.j = cell.getY();
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
        hint.i = cell.getX();
        hint.j = cell.getY();
        hint.type = Hint.HintType.BLUE_BUT_ISOLATED;
        return true;
    }

    //endregion
//endregion
}
