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

        board = new Board(boardSize);
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
                        case 0: //Salir
                            fadeOut = true;
                            elapsedTime = 0;
                            for (int j = 0; j < objects.size(); ++j)
                                objects.get(j).fadeOut(SCENE_FADE_DURATION);
                            break;
                        case 1: //Deshacer movimiento
                            undoMove();
                            break;
                        case 2: //Dar pista
                            if (boardRenderer.isCellHighlighted()) { //Si se esta dando una pista, se deja de dar
                                boardRenderer.endHighlighting();
                                infoTextRender.fadeNewText(infoRegContent, INFO_REG_SIZE, false, TEXT_FADE_DURATION);
                                infoReset = true;
                            } else if (!gameOver) { //Si no se esta dando una pista, se empieza a dar
                                board.solve(true);
                                Hint hint = board.hint;
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
     * Dado el centro de una circunferencia, su radio y otra posición, se comprueba si ha habido una intersección.
     */
    private boolean checkCollisionCircle(int centerX, int centerY, int radius, int eventX, int eventY) {
        int vecX = eventX - centerX;
        int vecY = eventY - centerY;
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }
//endregion
}
