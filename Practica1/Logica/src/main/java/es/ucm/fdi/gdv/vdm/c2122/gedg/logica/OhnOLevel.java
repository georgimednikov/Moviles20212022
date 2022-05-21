package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Scene;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOLevel implements Scene {

    //Frases de victoria
    private final String[] YOU_WIN_TEXTS = new String[]{
            "ROCAMBOLESCO",
            "EXCELENTE",
            "INCREÍBLE",
            "PARABIÉN"
    };

    Engine eng_;

    //Constantes de renderizado

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

    //Constantes de animacion
    private final float SCENE_FADE_DURATION = 0.25f; //Segundos que duran los fades de la escena
    private final float TIME_AFTER_WIN = 1.5f;
    private final float TEXT_FADE_DURATION = 0.2f;

    //Variables de asignacion dinamica
    private int cellSeparation = -1;
    private int cellRadius = -1;
    private int buttonSeparation = -1;

    //Variables de animacion
    private boolean gameOver = false;
    private boolean fadeOut = false;
    private float elapsedTime = 0f; //Segundos que lleva haciendose un fade de la escena

    //Variables de creacion de tablero
    int boardSize;

    private Board board;
    private BoardRenderer boardRenderer;
    private List<ObjectRenderer> objects = new ArrayList<>();

    //Variables relacionadas con pistas
    private boolean lockChanged = false; //Si hay que cambiar el estado de los candados

    //Objetos de la escena y variables relacionadas
    private Color black;
    private Color white;
    private Color darkGrey;
    private Font infoFont;
    private Font progressFont;
    private Font numberFont;
    private Image lockImage;
    private ImageRenderer quitImage;
    private ImageRenderer undoImage;
    private ImageRenderer hintImage;
    private TextRender infoTextRender;
    private TextRender progressTextRender;
    private String infoRegContent; //Contenido del texto que ayuda al jugador (cambia mucho de valor)
    private boolean infoReset = true; //Si el texto informativo ha sido reseteado o no (ha vuelto a su texto por defecto)

    public OhnOLevel(int size) {
        boardSize = size;
    }

    @Override
    public void init(Engine eng) {
        eng_ = eng;
        Graphics g = eng_.getGraphics();

        //Se calculan las variables de asignacion dinamica
        //Dependen de las constantes y del tamaño de ventana

        //Área en la que se van a dibujar los elementos de la interfaz.
        int paintArea = eng_.getGraphics().getWidth() - 2 * BOARD_OFFSET_X;
        cellRadius = (int) ((paintArea * 0.9) / 2) / boardSize;
        cellSeparation = (int) (paintArea * 0.1) / (boardSize - 1);
        int buttonArea = eng_.getGraphics().getWidth() - 2 * BUTTON_OFFSET_X;
        buttonSeparation = (buttonArea - (BUTTON_SIZE * NUM_BUTTONS)) / (NUM_BUTTONS - 1);

        black = new Color(0, 0, 0, 255);
        darkGrey = new Color(150, 150, 150, 255);
        white = new Color(255, 255, 255, 255);

        infoFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", black, INFO_REG_SIZE, true);
        progressFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", darkGrey, PROGRESS_SIZE, false);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", white, cellRadius, false);

        infoRegContent = boardSize + " x " + boardSize;
        infoTextRender = new TextRender(infoFont, infoRegContent, true);
        objects.add(infoTextRender);
        progressTextRender = new TextRender(progressFont, "Placeholder", true);
        objects.add(progressTextRender);

        lockImage = g.newImage("assets/sprites/lock.png");
        quitImage = new ImageRenderer(g.newImage("assets/sprites/close.png"), BUTTON_SIZE, BUTTON_SIZE, false);
        objects.add(quitImage);
        undoImage = new ImageRenderer(g.newImage("assets/sprites/history.png"), BUTTON_SIZE, BUTTON_SIZE, false);
        objects.add(undoImage);
        hintImage = new ImageRenderer(g.newImage("assets/sprites/eye.png"), BUTTON_SIZE, BUTTON_SIZE, false);
        objects.add(hintImage);

        board = new Board(boardSize, cellRadius);
        boardRenderer = new BoardRenderer(numberFont, lockImage, boardSize, cellRadius, cellSeparation, board);
        objects.add(boardRenderer);

        progressTextRender.setText(board.donePercentage() + "%"); //Actualiza el porcentaje de progreso.

        //Se hace aparecer progresivamente todos los renderers de la escena.
        for (int i = 0; i < objects.size(); ++i) objects.get(i).fadeIn(SCENE_FADE_DURATION);
    }

    @Override
    public void update() {
        //Se actualizan las entidades con animaciones
        if (updateScene(eng_.getDeltaTime())) return;

        //Se procesan los eventos de input
        TouchEvent event;
        next:
        while ((event = eng_.getInput().dequeueEvent()) != null) {
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            //Para cada celda se comprueba si se ha hecho click en ella
            for (int i = 0; i < boardSize; ++i) {
                for (int j = 0; j < boardSize; ++j) {
                    if (checkCollisionCircle(
                            BOARD_OFFSET_X + cellRadius * (i + 1) + (cellSeparation + cellRadius) * i,
                            BOARD_OFFSET_Y + cellRadius * (j + 1) + (cellSeparation + cellRadius) * j,
                            cellRadius, event.x, event.y)) {
                        if (!board.isFixed(j, i)) //Si no es fija cambia de estado
                            changeCell(j, i);
                        else { //Si es fija se hace la animación de "golpes" y se actualizan los candados.
                            boardRenderer.bumpCell(j, i);
                            lockChanged = true;
                        }
                        continue next;
                    }
                }
            }

            //Se comrpueban los botones
            for (int i = 0; i < NUM_BUTTONS; ++i) {
                if (checkCollisionCircle(
                        BUTTON_OFFSET_X + (BUTTON_SIZE / 2) + (BUTTON_SIZE + buttonSeparation) * i,
                        BUTTON_OFFSET_Y + (BUTTON_SIZE / 2),
                        BUTTON_SIZE / 2, event.x, event.y)) {
                    switch (i) {
                        case 0:
                            fadeOut = true;
                            elapsedTime = 0;
                            for (int j = 0; j < objects.size(); ++j)
                                objects.get(j).fadeOut(SCENE_FADE_DURATION);
                            break;
                        case 1:
                            undoMove();
                            break;
                        case 2:
                            if (boardRenderer.isCellHighlighted()) { //Si se esta dando una pista, se deja de dar
                                boardRenderer.endHighlighting();
                                infoTextRender.fadeNewText(infoRegContent, INFO_REG_SIZE, false, TEXT_FADE_DURATION);
                                infoReset = true;
                            } else if (!gameOver) { //Si no se esta dando una pista, se empieza a dar
                                Hint hint = giveHint_user();
                                boardRenderer.highlightCell(hint.y_, hint.x_); //Se destaca la celda para dar la pista.
                                infoTextRender.fadeNewText(hint.hintText[hint.type_.ordinal()], INFO_HINT_SIZE, false, TEXT_FADE_DURATION); //Se muestra la pista.
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
        infoTextRender.render(g);
        g.translate(0, PROGRESS_POS_Y - INFO_POS_Y);
        progressTextRender.render(g);
        g.restore();

        g.save();
        g.translate(BOARD_OFFSET_X, BOARD_OFFSET_Y);
        boardRenderer.render(g);
        g.restore();

        g.save();
        g.translate(BUTTON_OFFSET_X, BUTTON_OFFSET_Y);
        quitImage.render(g);
        g.translate(BUTTON_SIZE + buttonSeparation, 0);
        undoImage.render(g);
        g.translate(BUTTON_SIZE + buttonSeparation, 0);
        hintImage.render(g);
        g.restore();
    }

    /**
     * Actualiza todo lo relacionado con las animaciones de la escena
     * Devuelve true si no se deben procesar inputs porque se esta realizando una animacion, false en caso contrario
     */
    private boolean updateScene(double deltaTime) {
        updateRenders(deltaTime);
        if (gameOver) { //Si se ha acabado el juego se espera antes de empezar a cambiar de escena
            if (elapsedTime >= TIME_AFTER_WIN) {
                gameOver = false;
                fadeOut = true; //Flag de transicion
                elapsedTime = 0;

                //Se le dice a todos los renderers que desaparezcan progresivamente.
                for (int i = 0; i < objects.size(); ++i)
                    if (objects.get(i) != infoTextRender)
                        objects.get(i).fadeOut(SCENE_FADE_DURATION);
            } else elapsedTime += deltaTime;
        }
        if (fadeOut) { //Hace fade out hasta desaparecer por completo y luego cambia de escena.
            if (elapsedTime >= SCENE_FADE_DURATION) {
                OhnOMenu app = new OhnOMenu();
                eng_.changeScene(app);
            } else elapsedTime += deltaTime;
        }
        return false;
    }

    /**
     * Actualiza los renderers, actualizando sus animaciones con deltaTime.
     */
    private void updateRenders(double deltaTime) {
        for (int i = 0; i < objects.size(); ++i)
            objects.get(i).updateRenderer(deltaTime);
        //Si deberian aparecer/desaparecer los candados se le dice a las celdas apropiadas que ciclen su visibilidad.
        if (!lockChanged) return;
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                if (lockChanged && boardRenderer.getType(i, j) == CellRenderer.CELL_TYPE.LOCK)
                    boardRenderer.changeLock(i, j);
            }
        }
        lockChanged = false;
    }

    //region Board Methods
    /**
     * Cambia el estado de una celda y lo que esto conlleva.
     */
    public void changeCell(int x, int y) {
        if (!infoReset) {
            infoTextRender.fadeNewText(infoRegContent, INFO_REG_SIZE, false, TEXT_FADE_DURATION);
            boardRenderer.endHighlighting();
            infoReset = true;
        }

        if (board.changeCell(x, y)) {
            gameOver = true;
            infoTextRender.fadeNewText(YOU_WIN_TEXTS[OhnORandom.r.nextInt(YOU_WIN_TEXTS.length)], INFO_WIN_SIZE, true, TEXT_FADE_DURATION);
            return;
        }
        progressTextRender.setText(board.donePercentage() + "%"); //Actualiza el porcentaje de progreso.
    }

    /**
     * Deshace el ultimo movimiento hecho de ser posible.
     * Actualiza la interfaz en base a lo que se ha hecho.
     */
    private void undoMove() {
        String text = "";
        Tuple<Integer, Integer> cellPos = board.undoMove();
        if (cellPos == null) {
            text = "No queda nada por hacer";
            boardRenderer.endHighlighting();
        }
        else {
            switch (board.getCurrState(cellPos.x, cellPos.y)) {
                case BLUE:
                    text = "Esta celda ha vuelto a azul";
                    break;
                case GREY:
                    text = "Esta celda ha vuelto a gris";
                    break;
                case RED:
                    text = "Esta celda ha vuelto a rojo";
                    break;
            }
            boardRenderer.transitionCell(cellPos.x, cellPos.y);
            boardRenderer.highlightCell(cellPos.x, cellPos.y);
            progressTextRender.setText(board.donePercentage() + "%"); //Actualiza el porcentaje de progreso.
        }
        infoTextRender.fadeNewText(text, INFO_HINT_SIZE, false, TEXT_FADE_DURATION); //Hace aparecer un texto con el string establecido.
        infoReset = false;
    }
    //endregion

    //Método DEBUG
    /*private int readFromConsole(char[][] mat) {
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
    }*/

    /**
     * Calcula la distancia entre dos casillas.
     */
    private int distanceBetweenPos(int x1, int y1, int x2, int y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * Dado el centro de una circunferencia, su radio y otra posición, se comprueba si ha habido una intersección.
     */
    private boolean checkCollisionCircle(int centerX, int centerY, int radius, int eventX, int eventY) {
        int vecX = eventX - centerX;
        int vecY = eventY - centerY;
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }

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
    public Hint giveHint_user() {
        Hint hint = new Hint();
        //Pistas basadas en celdas fijas
        for (int i = 0; i < fixedBlueCells.size(); ++i) {
            if (getHintFixedCell_user(hint, board, fixedBlueCells.get(i))) return hint;
        }
        //Pistas basadas en celdas ordinarias
        for (int i = 0; i < boardSize; ++i) {
            for (int j = 0; j < boardSize; ++j) {
                if (board[i][j].isFixed()) continue;
                if (getHintRegularCell_user(hint, board, board[i][j])) return hint;
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
        if (cont != cellLogic.getNumber())
            return false; //Si no ha llegado al numero correcto pasamos
        int[] newPos = nextDiffColor(mat, cellLogic.getX(), cellLogic.getY(), i, j, CellLogic.STATE.BLUE);
        //Busca en la direccion i j la siguiente casilla no azul; si es gris, esta abierta y hay que cerrarla
        if (mat[newPos[0]][newPos[1]].getCurrState() == CellLogic.STATE.GREY) {
            hint.type_ = Hint.HintType.VISIBLE_CELLS_COVERED;
            hint.x_ = newPos[0];
            hint.y_ = newPos[1];
            return true;
        }
        return false;
    }

    private boolean hint_CANNOT_SURPASS_LIMIT(Hint hint, CellLogic[][] mat, CellLogic cellLogic, int i, int j, int cont) {
        if (cont >= cellLogic.getNumber())
            return false; //Si se ha llegado al numero correcto pasamos
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
                hint.type_ = Hint.HintType.CANNOT_SURPASS_LIMIT;
                hint.x_ = newPos[0];
                hint.y_ = newPos[1];
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
            hint.x_ = thisFirstGrey[0];
            hint.y_ = thisFirstGrey[1];
            hint.type_ = Hint.HintType.MUST_PLACE_BLUE;
            return true;
        }
        return false;
    }
    //endregion

    //region Regular Hints
    private boolean hint_TOO_MANY_ADJACENT(Hint hint, CellLogic[][] mat, CellLogic cellLogic) {
        if (calculateNumber(mat, cellLogic.getX(), cellLogic.getY()) <= cellLogic.getNumber())
            return false;
        hint.x_ = cellLogic.getX();
        hint.y_ = cellLogic.getY();
        hint.type_ = Hint.HintType.TOO_MANY_ADJACENT;
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
                if (mat[firstRed[0]][firstRed[1]].getCurrState() == CellLogic.STATE.GREY)
                    return false;
                //Si es roja hay que retroceder para no contarla
                if (mat[firstRed[0]][firstRed[1]].getCurrState() != CellLogic.STATE.BLUE)
                    blueVisible += distanceBetweenPos(cellLogic.getX(), cellLogic.getY(), firstRed[0] - i, firstRed[1] - j);
                else
                    blueVisible += distanceBetweenPos(cellLogic.getX(), cellLogic.getY(), firstRed[0], firstRed[1]);
            }
        }
        //Si ve las que tiene que ver esta pista no aplica
        if (blueVisible >= cellLogic.getNumber()) return false;
        hint.x_ = cellLogic.getX();
        hint.y_ = cellLogic.getY();
        hint.type_ = Hint.HintType.NOT_ENOUGH_BUT_CLOSED;
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
        hint.x_ = cellLogic.getX();
        hint.y_ = cellLogic.getY();
        hint.type_ = Hint.HintType.BLUE_BUT_ISOLATED;
        return true;
    }
    //endregion
//endregion
}
