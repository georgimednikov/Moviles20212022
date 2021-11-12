package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.ApplicationCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOLevel extends ApplicationCommon {

    //Textos
    private final int INFO_POS_Y = 75;
    private final int PROGRESS_POS_Y = 500;
    private final int INFO_REG_SIZE = 60;
    private final int INFO_WIN_SIZE = 35;
    private final int INFO_HINT_SIZE = 25;
    private final int PROGRESS_SIZE = 25;
    //Posiciones
    private final int BOARD_OFFSET_X = 30;
    private final int BOARD_OFFSET_Y = 130;
    private final int BUTTON_OFFSET_X = 75;
    private final int BUTTON_OFFSET_Y = 530;
    private final int BUTTON_SIZE = 40;
    private final int NUM_BUTTONS = 3;
    //Tiempos
    private final float FADE_TOTAL_DURATION = 0.25f; //Segundos que duran los fades de la escena
    private final float TIME_AFTER_WIN = 10f;
    //Probabilidades
    private final float BLUE_PROB = 0.7f; //Probabilidad de que una celda sea azul en vez de roja en la solución
    private final float FIXED_PROB = 0.5f; //Probabilidad de que una celda sea fija
    //Asignacion dinamica
    private int cellSeparation = -1;
    private int cellRadius = -1;
    private int buttonSeparation = -1;


    //Variables de transicion
    private boolean gameOver = false;
    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private float currentDuration = 0f; //Segundos que lleva haciendose un fade de la escena
    private float sceneAlpha = 0f; //Alpha de la escena al hacer fade in/out

    //Variables de creacion de tablero
    int boardSize;
    int numCells;
    int coloredCells = 0;
    int fixedCells = 0;
    int contMistakes = 0; //Número de celdas mal puestas
    private CellLogic[][] solBoard;
    private CellLogic[][] board;
    private CellRender[][] renderBoard;
    private List<CellLogic> previousMoves = new ArrayList<>();
    private List<CellLogic> fixedBlueCells = new ArrayList<>();
    private List<CellRender> lockCells = new ArrayList<>();

    //Variables relacionadas con pistas
    private boolean givingHint = false;
    private int highlightRadius = -1; //Asignacion dinamica
    private int highlightPosX = 0;
    private int highlightPosY = 0;

    //Objetos de la escena y variables relacionadas
    private Color black;
    private Color white;
    private Color darkGrey;
    private Image quitImage;
    private Image undoImage;
    private Image hintImage;
    private Image lockImage;
    private Font infoFont;
    private Font progressFont;
    private Font numberFont;
    private Text infoText;
    private Text progressText;
    private boolean infoReset = true;
    private String infoRegContent;

    public OhnOLevel(int size) {
        boardSize = size;
    }

    @Override
    public boolean init() {
        Graphics g = eng_.getGraphics();

        int paintArea = eng_.getGraphics().getWidth() - 2 * BOARD_OFFSET_X;
        cellRadius = (int)((paintArea * 0.9) / 2) / boardSize;
        cellSeparation = (int)(paintArea * 0.1) / (boardSize-1);
        int buttonArea = eng_.getGraphics().getWidth() - 2 * BUTTON_OFFSET_X;
        buttonSeparation = (buttonArea - (BUTTON_SIZE * NUM_BUTTONS)) / (NUM_BUTTONS - 1);
        highlightRadius = (int)Math.round(cellRadius * 1.1);

        black = new Color(0, 0, 0, 255);
        darkGrey = new Color(150, 150, 150, 255);
        white = new Color(255, 255, 255, 255);

        infoFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", black, INFO_REG_SIZE, true);
        progressFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", darkGrey, 25, false);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", white, cellRadius, false);

        infoRegContent = boardSize + " x " + boardSize;
        infoText = new Text(infoFont, infoRegContent, true);
        progressText = new Text(progressFont, Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%", true);

        quitImage = g.newImage("assets/sprites/close.png");
        undoImage = g.newImage("assets/sprites/history.png");
        hintImage = g.newImage("assets/sprites/eye.png");
        lockImage = g.newImage("assets/sprites/lock.png");

        createLogicBoard();
        createRenderBoard();

        return true;
    }

    @Override
    public void update() {
        double deltaTime = eng_.getDeltaTime();
        infoText.updateText(deltaTime);
        progressText.updateText(deltaTime);
        updateCellRenders(deltaTime);
        if (updateScene(deltaTime)) return;

        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        next:
        while (!events.isEmpty()) {
            event = events.remove(0);
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    if (checkCollisionCircle(
                            BOARD_OFFSET_X + cellRadius * (i + 1) + (cellSeparation + cellRadius) * i,
                            BOARD_OFFSET_Y + cellRadius * (j + 1) + (cellSeparation + cellRadius) * j,
                            cellRadius, event.x, event.y)) {
                        if (!board[j][i].isFixed())
                            changeCell(j, i);
                        else
                            for (int k = 0; k < lockCells.size(); ++k)
                                lockCells.get(k).cycleLock();
                        continue next;
                    }
                }
            }
            for (int i = 0; i < NUM_BUTTONS; ++i) {
                if (checkCollisionCircle(
                        BUTTON_OFFSET_X + cellRadius * (i + 1) + buttonSeparation * i,
                        BUTTON_OFFSET_Y,
                        cellRadius, event.x, event.y)) {
                    switch (i) {
                        case 0:
                            fadeOut = true;
                            break;
                        case 1:
                            undoMove();
                            break;
                        case 2:
                            if (givingHint) {
                                givingHint = false;
                                infoText.fade(infoRegContent, INFO_REG_SIZE, false);
                                infoReset = true;
                            }
                            else {
                                givingHint = true;
                                Hint hint = giveHint_user();
                                highlightPosX = BOARD_OFFSET_X + cellRadius * (hint.j + 1) + (cellSeparation + cellRadius) * hint.j;
                                highlightPosY = BOARD_OFFSET_Y + cellRadius * (hint.i + 1) + (cellSeparation + cellRadius) * hint.i;
                                infoText.fade(hint.hintText[hint.type.ordinal()], INFO_HINT_SIZE, false);
                                infoReset = false;
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

        g.save();
        g.translate(g.getWidth() / 2, INFO_POS_Y);
        infoText.render(g);
        g.translate(0, PROGRESS_POS_Y - INFO_POS_Y);
        progressText.render(g);
        g.restore();

        if (givingHint) {
            g.setColor(black);
            g.fillCircle(highlightPosX, highlightPosY, highlightRadius);
        }
        for (int i = 0; i < boardSize; ++i) {
            g.save();
            g.translate(BOARD_OFFSET_X + cellRadius, BOARD_OFFSET_Y + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < boardSize; ++j) {
                renderBoard[i][j].render(g);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.save();
        g.translate(BUTTON_OFFSET_X, BUTTON_OFFSET_Y);
        g.drawImage(quitImage, 0, 0, BUTTON_SIZE, BUTTON_SIZE, false);
        g.translate(BUTTON_SIZE + buttonSeparation, 0);
        g.drawImage(undoImage, 0, 0, BUTTON_SIZE, BUTTON_SIZE, false);
        g.translate(BUTTON_SIZE + buttonSeparation, 0);
        g.drawImage(hintImage, 0, 0, BUTTON_SIZE, BUTTON_SIZE, false);
        g.restore();

        if (fadeIn ||fadeOut) {
            g.clear(new Color(255, 255, 255, (int)(255 * sceneAlpha)));
        }
    }
    @Override
    public boolean close() {
        return true;
    }

    private void updateCellRenders(double deltaTime) {
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                renderBoard[i][j].updateCellRender(deltaTime);
            }
        }
    }

    private boolean updateScene(double deltaTime) {
        if (gameOver) {
            if (currentDuration >= TIME_AFTER_WIN) {
                gameOver = false;
                fadeOut = true;
                currentDuration = 0;
            }
            else currentDuration += deltaTime;
        }
        if (fadeIn || fadeOut) {
            if (currentDuration >= FADE_TOTAL_DURATION) {
                currentDuration = 0;
                if (fadeIn) fadeIn = false;
                else if (fadeOut) {
                    OhnOMenu app = new OhnOMenu();
                    eng_.setApplication(app);
                }
            }
            else {
                currentDuration += deltaTime;
                if (fadeIn) sceneAlpha = 1 - Math.min((currentDuration / FADE_TOTAL_DURATION), 1);
                else if (fadeOut) sceneAlpha = Math.min((currentDuration / FADE_TOTAL_DURATION), 1);
                return true;
            }
        }
        return false;
    }

    //region Board Methods
    public void changeCell(int x, int y) {
        if (!infoReset) {
            infoText.fade(infoRegContent, INFO_REG_SIZE, false);
            givingHint = false;
            infoReset = true;
        }
        CellLogic cell = board[x][y];
        cell.changeState();
        renderBoard[x][y].fade();
        previousMoves.add(cell);

        CellLogic.STATE prevState = cell.getPrevState();
        CellLogic.STATE currState = cell.getCurrState();
        if (prevState == solBoard[x][y].getCurrState()) contMistakes++;
        else if (currState == solBoard[x][y].getCurrState()) contMistakes--;
        if(contMistakes == 0) {
            gameOver = true;
            infoText.fade("ROCAMBOLESCO", INFO_WIN_SIZE, true);
        }
        if (prevState == CellLogic.STATE.GREY) {
            coloredCells++;
            progressText.fade(Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%", PROGRESS_SIZE, false);
        }
        else if (currState == CellLogic.STATE.GREY) {
            coloredCells--;
            progressText.fade(Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%", PROGRESS_SIZE, false);
        }
    }

    private void undoMove() {
        String text = "";
        givingHint = false;
        if (previousMoves.isEmpty()) {
            text = "No queda nada por hacer";
        }
        else {
            CellLogic cell = previousMoves.remove(previousMoves.size() - 1);
            switch (cell.revertState()) {
                case BLUE:
                    text = "Esta celda a vuelto a azul";
                    break;
                case GREY:
                    coloredCells--;
                    text = "Esta celda a vuelto a gris";
                    break;
                case RED:
                    text = "Esta celda a vuelto a rojo";
                    break;
            }
            if (cell.getCurrState() == CellLogic.STATE.GREY) {
                coloredCells--;
                progressText.fade(Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%", PROGRESS_SIZE, false);
            }
            else if (cell.getPrevState() == CellLogic.STATE.GREY) {
                coloredCells++;
                progressText.fade(Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%", PROGRESS_SIZE, false);
            }
            renderBoard[cell.getX()][cell.getY()].fade();
        }
        infoText.fade(text, INFO_HINT_SIZE, false);
        infoReset = false;
    }

    //Crea la matriz que representa el nivel de un tamaño dado
    private void createLogicBoard() {
        // Crea los objetos
        // Primero coloca un numero de casillas en posiciones aleatorias
        board = new CellLogic[boardSize][boardSize];
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                board[i][j] = new CellLogic(i, j);
            }
        }
        numCells = boardSize * boardSize;
        Hint hint = null;
        while (hint == null) {
            fixedCells = 0;
            fixedBlueCells.clear();
            //Se fijan ciertas celdas con valores aleatorios
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    board[i][j].resetCell(); //Por si ha habido un intento anterior
                    if (getRandomBoolean(FIXED_PROB)) {
                        if (getRandomBoolean(BLUE_PROB)) {
                            board[i][j].fixCell(CellLogic.STATE.BLUE);
                            fixedBlueCells.add(board[i][j]);
                        }
                        else {
                            board[i][j].fixCell(CellLogic.STATE.RED);
                        }
                        fixedCells++;
                    }
                }
            }
            for (CellLogic c : fixedBlueCells) {
                c.setNumber(Math.min(Math.max(calculateNumber(board, c.getX(), c.getY()), rand.nextInt(boardSize) + 1), boardSize));
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
                        CellLogic c = fixedBlueCells.get(i);
                        if (calculateNumber(solBoard, c.getX(), c.getY()) != c.getNumber()) {
                            hint = null;
                            break tries;
                        }
                    }
                    progressText.fade(Math.round((float)coloredCells / (float)(numCells - fixedCells) * 100) + "%", PROGRESS_SIZE, false);
                    return;
                }
            }
        }
    }

    private void createRenderBoard() {
        renderBoard = new CellRender[boardSize][boardSize];
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                CellLogic logic = board[i][j];
                CellRender render = renderBoard[i][j] = new CellRender(logic, cellRadius);
                if (logic.isFixed()) {
                    if (logic.getCurrState() == CellLogic.STATE.RED) {
                        lockCells.add(render);
                        render.setLock(lockImage);
                    }
                    else
                        render.setNumber(new Text(numberFont, ""+logic.getNumber(), true));
                }
            }
        }
    }
    //endregion

    //region Auxiliary Methods
    static private Random rand = new Random(System.currentTimeMillis());

    static private boolean getRandomBoolean(float p) {
        return rand.nextFloat() < p;
    }

    private int readFromConsole(char[][] mat) {
        int fixedCells = 0;
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                char c = mat[i][j];

                switch (c) {
                    case 'r':
                        board[i][j].fixCell(CellLogic.STATE.RED);
                        fixedCells++;
                        break;
                    case '0':
                        break;
                    default: // numeros
                        int num = Character.getNumericValue(c);
                        board[i][j].fixCell(CellLogic.STATE.BLUE, num);
                        fixedCells++;
                        fixedBlueCells.add(board[i][j]);
                        break;
                }
            }
        }
        return fixedCells;
    }

    private CellLogic[][] copyBoard(CellLogic[][] orig) {
        CellLogic[][] copy = new CellLogic[orig.length][orig[0].length];
        for (int i = 0; i < orig.length; ++i) {
            for (int j = 0; j < orig[0].length; ++j) {
                copy[i][j] = new CellLogic(orig[i][j].getX(), orig[i][j].getY());
                if (orig[i][j].isFixed())
                    copy[i][j].fixCell(orig[i][j].getCurrState(), orig[i][j].getNumber());
            }
        }
        return copy;
    }

    //Busca la primera casilla con color distinto al dado
    //Si no hay, devuelve la última casilla que hay.
    public int[] nextDiffColor(CellLogic[][] mat, int x, int y, int dx, int dy, CellLogic.STATE color) {
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
    public int[] nextColorCell(CellLogic[][] mat, int x, int y, int dx, int dy, CellLogic.STATE color) {
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
    private int calculateNumber(CellLogic[][] mat, int x, int y) {
        int count = 0;
        int[] newPos;
        newPos = nextDiffColor(mat, x, y, 1, 0, CellLogic.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != CellLogic.STATE.BLUE) {
            count += newPos[0] - x - 1;
        } else count += newPos[0] - x;
        newPos = nextDiffColor(mat, x, y, 0, 1, CellLogic.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != CellLogic.STATE.BLUE) {
            count += newPos[1] - y - 1;
        } else count += newPos[1] - y;
        newPos = nextDiffColor(mat, x, y, -1, 0, CellLogic.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != CellLogic.STATE.BLUE) {
            count += x - newPos[0] - 1;
        } else count += x - newPos[0];
        newPos = nextDiffColor(mat, x, y, 0, -1, CellLogic.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() != CellLogic.STATE.BLUE) {
            count += y - newPos[1] - 1;
        } else count += y - newPos[1];
        return count;
    }

    //Comprueba que una posición no se sale del array
    private boolean inArray(CellLogic[][] mat, int x, int y) {
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
    //endregion

    //region Hints
    public Hint giveHint(CellLogic[][] mat) {
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

    private boolean getHintFixedCell(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        int curCount = calculateNumber(mat, cellLogic.getX(), cellLogic.getY()); //Número correcto de azules adyacentes
        //Pistas que requieren mirar cada direccion
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                if (hint_VISIBLE_CELLS_COVERED(hint, mat, cellLogic, i, j, curCount) ||
                        hint_CANNOT_SURPASS_LIMIT(hint, mat, cellLogic, i, j, curCount) ||
                        hint_MUST_PLACE_BLUE(hint, mat, cellLogic, i, j, curCount)) return true;
            }
        }
        return false;
    }

    private boolean getHintRegularCell(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        CellLogic.STATE state = cellLogic.getCurrState();
        //Se mira si se aplican unas pistas u otras dependiendo del color de la celda
        switch (state) {
            case GREY:
                if (hint_BLUE_BUT_ISOLATED(hint, mat, cellLogic)) {
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
    private boolean getHintFixedCell_user(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        if (getHintFixedCell(hint, mat, cellLogic)) return true;
        // Caso en el que el usuario se ha equivocado
        if (hint_TOO_MANY_ADJACENT(hint, mat, cellLogic) ||
                hint_NOT_ENOUGH_BUT_CLOSED(hint, mat, cellLogic)) return true;
        return false;
    }

    //Metodo que da pistas especificas para el usuario
    //Expande las que usa el generador de niveles contemplando posibles errores del usuario
    private boolean getHintRegularCell_user(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        if (getHintRegularCell(hint, mat, cellLogic)) return true;
        // Caso en el que el usuario se ha equivocado
        if (cellLogic.getCurrState() == CellLogic.STATE.BLUE) {
            return hint_BLUE_BUT_ISOLATED(hint, mat, cellLogic);
        }
        return false;
    }

    //region Fixed Hints
    private boolean hint_VISIBLE_CELLS_COVERED(Hint hint, CellLogic[][] mat, CellLogic cellLogic, int i, int j, int cont) {
        if (cont != cellLogic.getNumber()) return false; //Si no ha llegado al numero correcto pasamos
        int[] newPos = nextDiffColor(mat, cellLogic.getX(), cellLogic.getY(), i, j, CellLogic.STATE.BLUE);
        //Busca en la direccion i j la siguiente casilla no azul; si es gris, esta abierta y hay que cerrarla
        if (mat[newPos[0]][newPos[1]].getCurrState() == CellLogic.STATE.GREY) {
            hint.type = Hint.HintType.VISIBLE_CELLS_COVERED;
            hint.i = newPos[0];
            hint.j = newPos[1];
            return true;
        }
        return false;
    }

    private boolean hint_CANNOT_SURPASS_LIMIT(Hint hint, CellLogic[][] mat, CellLogic cellLogic, int i, int j, int cont) {
        if (cont >= cellLogic.getNumber()) return false; //Si se ha llegado al numero correcto pasamos
        int[] newPos = nextDiffColor(mat, cellLogic.getX(), cellLogic.getY(), i, j, CellLogic.STATE.BLUE);
        //Si la siguiente casilla en la direccion i j es gris puede haber camino, así que miramos si hacerlo supera el numero
        if (mat[newPos[0]][newPos[1]].getCurrState() == CellLogic.STATE.GREY) {
            int[] newNewPos = nextDiffColor(mat, newPos[0], newPos[1], i, j, CellLogic.STATE.BLUE);
            int newCont;
            //Si la siguiente celda no es azul no se ha salido de la matriz y hay que volver a la ultima azul, la anterior
            //Si es azul, se ha salido de la matriz y ha devuelto la ultima azul
            if ((newPos[0] == newNewPos[0] && newPos[1] == newNewPos[1]) || mat[newNewPos[0]][newNewPos[1]].getCurrState() == CellLogic.STATE.BLUE) {
                newCont = distanceBetweenPos(newPos[0] - i, newPos[1] - j, newNewPos[0], newNewPos[1]);
            } else newCont = distanceBetweenPos(newPos[0], newPos[1], newNewPos[0], newNewPos[1]);
            //Si poner la casilla gris en azul supera el numero correcto
            if (cont + newCont > cellLogic.getNumber()) {
                hint.type = Hint.HintType.CANNOT_SURPASS_LIMIT;
                hint.i = newPos[0];
                hint.j = newPos[1];
                return true;
            }
        }
        return false;
    }

    private boolean hint_MUST_PLACE_BLUE(Hint hint, CellLogic[][] mat, CellLogic cellLogic, int i, int j, int thisBlues) {
        if (thisBlues >= cellLogic.getNumber()) return false;
        int x = cellLogic.getX();
        int y = cellLogic.getY();

        int otherBlues = 0;
        for (int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                if ((k + l == 2) || (k + l == 0) || (k + l == -2)) continue;
                if (i == k && j == l) continue; //No miramos la direccion a evaluar
                int[] otherDirRed = nextColorCell(mat, x, y, k, l, CellLogic.STATE.RED);
                // Si es roja, no ha llegado al final y hay que contar hasta esa excluyendola
                if (mat[otherDirRed[0]][otherDirRed[1]].getCurrState() == CellLogic.STATE.RED) {
                    otherDirRed[0] -= k;
                    otherDirRed[1] -= l;
                }
                int[] otherNextNoBlue = nextDiffColor(mat, x, y, k, l, CellLogic.STATE.BLUE);
                // Si es gris, no ha llegado al final y hay que contar hasta esa excluyendola
                if (mat[otherNextNoBlue[0]][otherNextNoBlue[1]].getCurrState() != CellLogic.STATE.BLUE) {
                    otherNextNoBlue[0] -= k;
                    otherNextNoBlue[1] -= l;
                }
                // Se suman las que se añadirian si se uniese desde la gris hasta la roja
                otherBlues += distanceBetweenPos(otherNextNoBlue[0], otherNextNoBlue[1], otherDirRed[0], otherDirRed[1]);
            }
        }
        //Si las otras 3 direcciones juntas mas las casillas azules que ya ve no llegan al numero correcto
        if (otherBlues + thisBlues < cellLogic.getNumber()) {
            int[] thisFirstGrey = nextColorCell(mat, x, y, i, j, CellLogic.STATE.GREY);
            if (mat[thisFirstGrey[0]][thisFirstGrey[1]].getCurrState() != CellLogic.STATE.GREY) {
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
    private boolean hint_TOO_MANY_ADJACENT(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        if (calculateNumber(mat, cellLogic.getX(), cellLogic.getY()) <= cellLogic.getNumber()) return false;
        hint.i = cellLogic.getX();
        hint.j = cellLogic.getY();
        hint.type = Hint.HintType.TOO_MANY_ADJACENT;
        return true;
    }

    private boolean hint_NOT_ENOUGH_BUT_CLOSED(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        int x = cellLogic.getX();
        int y = cellLogic.getY();
        int blueVisible = 0;
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                int[] firstRed = nextDiffColor(mat, x, y, i, j, CellLogic.STATE.BLUE);
                // Si es gris, no se ha cerrado
                if (mat[firstRed[0]][firstRed[1]].getCurrState() == CellLogic.STATE.GREY) return false;
                //Si es roja hay que retroceder para no contarla
                if (mat[firstRed[0]][firstRed[1]].getCurrState() != CellLogic.STATE.BLUE)
                    blueVisible += distanceBetweenPos(cellLogic.getX(), cellLogic.getY(), firstRed[0] - i, firstRed[1] - j);
                else
                    blueVisible += distanceBetweenPos(cellLogic.getX(), cellLogic.getY(), firstRed[0], firstRed[1]);
            }
        }
        //Si ve las que tiene que ver esta pista no aplica
        if (blueVisible >= cellLogic.getNumber()) return false;
        hint.i = cellLogic.getX();
        hint.j = cellLogic.getY();
        hint.type = Hint.HintType.NOT_ENOUGH_BUT_CLOSED;
        return true;
    }

    private boolean hint_BLUE_BUT_ISOLATED(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        int x = cellLogic.getX();
        int y = cellLogic.getY();
        for (int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                //Para direccion buscar si se puede poner rojo porque no hay una azul bloqueada que podria llegar a esta
                int adv = 1;
                while (inArray(mat, x + i * adv, y + j * adv) &&
                        (mat[x + i * adv][y + j * adv].getCurrState() != CellLogic.STATE.RED)) {
                    if ((mat[x + i * adv][y + j * adv].getCurrState() == CellLogic.STATE.BLUE && mat[x + i * adv][y + j * adv].isFixed()))
                        return false;
                    adv++;
                }
            }
        }
        hint.i = cellLogic.getX();
        hint.j = cellLogic.getY();
        hint.type = Hint.HintType.BLUE_BUT_ISOLATED;
        return true;
    }
    //endregion
//endregion
}
