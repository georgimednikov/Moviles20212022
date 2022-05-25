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
    private final float SECONDS_UNTIL_HINT = 2.0f; //Segundos que se espera para mostrar una pista cuando se resuelve incorrectamente el nivel.

    //Variables de asignacion dinamica
    private int cellSeparation_ = -1;
    private int cellRadius_ = -1;
    private int buttonSeparation_ = -1;

    //Variables de animacion
    private boolean gameOver_ = false;
    private boolean fadeOut_ = false;
    private float timeForHint_ = 0f; //Tiempo que se lleva esperando para poner la pista cuando se completa el tablero de forma incorrecta.
    private float elapsedTime_ = 0f; //Segundos que lleva haciendose un fade de la escena
    private int elapsedFrames_ = 0;  //Cuenta hasta el segundo frame para no contar el segundo delta time de la ejecución de la escena.
                                     //Si se elige un tamaño grande de nivel puede tardar un poco en cargar, agrandando el delta time
                                     //el segundo frame y haciendo instantánea la transición de entrada en la escena. El primero también
                                     //se evita para que no empiecen las animaciones un frame y estén congeladas el siguiente.

    //Variables de creacion de tablero
    private int boardSize_;

    private Board board_;
    private BoardRenderer boardRenderer_;
    private List<ObjectRenderer> objects_ = new ArrayList<>();

    //Variables relacionadas con pistas
    private boolean lockChanged_ = false; //Si hay que cambiar el estado de los candados

    //Objetos de la escena y variables relacionadas
    private Color black_;
    private Color white_;
    private Color lightGrey_;
    private Font infoFont_;
    private Font progressFont_;
    private Font numberFont_;
    private Image lockImage_;
    private ImageRenderer quitImage_;
    private ImageRenderer undoImage_;
    private ImageRenderer hintImage_;
    private TextRender hintTextRender_;
    private TextRender progressTextRender_;
    private String infoRegContent_; //Contenido del texto que ayuda al jugador (cambia mucho de valor)
    private boolean infoReset_ = true; //Si el texto informativo ha sido reseteado o no (ha vuelto a su texto por defecto)

    public OhnOLevel(int size) {
        boardSize_ = size;
    }

    @Override
    public void init(Engine eng) {
        eng_ = eng;
        Graphics g = eng_.getGraphics();

        //Se calculan las variables de asignacion dinamica
        //Dependen de las constantes y del tamaño de ventana

        //Área en la que se van a dibujar los elementos de la interfaz.
        int paintArea = eng_.getGraphics().getWidth() - 2 * BOARD_OFFSET_X;
        cellRadius_ = (int) ((paintArea * 0.9) / 2) / boardSize_;
        cellSeparation_ = (int) (paintArea * 0.1) / (boardSize_ - 1);
        int buttonArea = eng_.getGraphics().getWidth() - 2 * BUTTON_OFFSET_X;
        buttonSeparation_ = (buttonArea - (BUTTON_SIZE * NUM_BUTTONS)) / (NUM_BUTTONS - 1);

        black_ = new Color(0, 0, 0, 255);
        lightGrey_ = new Color(150, 150, 150, 255);
        white_ = new Color(255, 255, 255, 255);

        infoFont_ = g.newFont("assets/fonts/JosefinSans-Bold.ttf", black_, INFO_REG_SIZE, true);
        progressFont_ = g.newFont("assets/fonts/JosefinSans-Bold.ttf", lightGrey_, PROGRESS_SIZE, false);
        numberFont_ = g.newFont("assets/fonts/JosefinSans-Bold.ttf", white_, cellRadius_, false);

        infoRegContent_ = boardSize_ + " x " + boardSize_;
        hintTextRender_ = new TextRender(infoFont_, infoRegContent_, true);
        objects_.add(hintTextRender_);
        progressTextRender_ = new TextRender(progressFont_, "Placeholder", true);
        objects_.add(progressTextRender_);

        lockImage_ = g.newImage("assets/sprites/lock.png");
        quitImage_ = new ImageRenderer(g.newImage("assets/sprites/close.png"), BUTTON_SIZE, BUTTON_SIZE, false);
        objects_.add(quitImage_);
        undoImage_ = new ImageRenderer(g.newImage("assets/sprites/history.png"), BUTTON_SIZE, BUTTON_SIZE, false);
        objects_.add(undoImage_);
        hintImage_ = new ImageRenderer(g.newImage("assets/sprites/eye.png"), BUTTON_SIZE, BUTTON_SIZE, false);
        objects_.add(hintImage_);

        board_ = new Board(boardSize_);
        boardRenderer_ = new BoardRenderer(numberFont_, lockImage_, boardSize_, cellRadius_, cellSeparation_, board_);
        objects_.add(boardRenderer_);

        progressTextRender_.setText(board_.donePercentage() + "%"); //Actualiza el porcentaje de progreso.

        //Se hace aparecer progresivamente todos los renderers de la escena.
        for (int i = 0; i < objects_.size(); ++i) objects_.get(i).fadeIn(SCENE_FADE_DURATION);
    }

    @Override
    public void update() {
        //Se actualizan las entidades con animaciones
        if (updateScene(eng_.getDeltaTime())) return;

        //Se procesan los eventos de input
        TouchEvent event;
        next:
        while ((event = eng_.getInput().dequeueEvent()) != null) {
            if (gameOver_ || event.type != TouchEvent.TouchType.PRESS) continue;
            //Para cada celda se comprueba si se ha hecho click en ella
            for (int i = 0; i < boardSize_; ++i) {
                for (int j = 0; j < boardSize_; ++j) {
                    if (checkCollisionCircle(
                            BOARD_OFFSET_X + cellRadius_ * (i + 1) + (cellSeparation_ + cellRadius_) * i,
                            BOARD_OFFSET_Y + cellRadius_ * (j + 1) + (cellSeparation_ + cellRadius_) * j,
                            cellRadius_, event.x, event.y)) {
                        if (!board_.isFixed(j, i)) //Si no es fija cambia de estado
                            changeCell(j, i);
                        else { //Si es fija se hace la animación de "golpes" y se actualizan los candados.
                            boardRenderer_.bumpCell(j, i);
                            lockChanged_ = true;
                        }
                        continue next;
                    }
                }
            }

            //Se comrpueban los botones
            for (int i = 0; i < NUM_BUTTONS; ++i) {
                if (checkCollisionCircle(
                        BUTTON_OFFSET_X + (BUTTON_SIZE / 2) + (BUTTON_SIZE + buttonSeparation_) * i,
                        BUTTON_OFFSET_Y + (BUTTON_SIZE / 2),
                        BUTTON_SIZE / 2, event.x, event.y)) {
                    switch (i) {
                        case 0: //Salir
                            fadeOut_ = true;
                            elapsedTime_ = 0;
                            for (int j = 0; j < objects_.size(); ++j)
                                objects_.get(j).fadeOut(SCENE_FADE_DURATION);
                            break;
                        case 1: //Deshacer movimiento
                            undoMove();
                            break;
                        case 2: //Dar pista
                            if (boardRenderer_.isCellHighlighted()) { //Si se esta dando una pista, se deja de dar
                                boardRenderer_.endHighlighting();
                                hintTextRender_.fadeNewText(infoRegContent_, INFO_REG_SIZE, false, TEXT_FADE_DURATION);
                                infoReset_ = true;
                            } else if (!gameOver_) { //Si no se esta dando una pista, se empieza a dar
                                giveHint();
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
        hintTextRender_.render(g);
        g.translate(0, PROGRESS_POS_Y - INFO_POS_Y);
        progressTextRender_.render(g);
        g.restore();

        g.save();
        g.translate(BOARD_OFFSET_X, BOARD_OFFSET_Y);
        boardRenderer_.render(g);
        g.restore();

        g.save();
        g.translate(BUTTON_OFFSET_X, BUTTON_OFFSET_Y);
        quitImage_.render(g);
        g.translate(BUTTON_SIZE + buttonSeparation_, 0);
        undoImage_.render(g);
        g.translate(BUTTON_SIZE + buttonSeparation_, 0);
        hintImage_.render(g);
        g.restore();
    }

    /**
     * Actualiza todo lo relacionado con las animaciones de la escena
     * Devuelve true si no se deben procesar inputs porque se esta realizando una animacion, false en caso contrario
     */
    private boolean updateScene(double deltaTime) {
        if (elapsedFrames_ <= 1) { elapsedFrames_++; deltaTime = 0; } //Ver declaración de elapsedFrames.

        //Si se ha rellenado el tablero pero no se ha acabado la partida, se espera SECONDS_UNTIL_HINT para mostrar una pista
        int donePercentage = board_.donePercentage();
        if (!gameOver_ && donePercentage == 100 && timeForHint_ < SECONDS_UNTIL_HINT) {
            timeForHint_ += deltaTime;
            if (timeForHint_ >= SECONDS_UNTIL_HINT) {
                giveHint();
            }
        }

        updateRenders(deltaTime);
        if (gameOver_) { //Si se ha acabado el juego se espera antes de empezar a cambiar de escena
            if (elapsedTime_ >= TIME_AFTER_WIN) {
                gameOver_ = false;
                fadeOut_ = true; //Flag de transicion
                elapsedTime_ = 0;

                //Se le dice a todos los renderers que desaparezcan progresivamente.
                for (int i = 0; i < objects_.size(); ++i)
                    if (objects_.get(i) != hintTextRender_)
                        objects_.get(i).fadeOut(SCENE_FADE_DURATION);
            } else elapsedTime_ += deltaTime;
        }
        if (fadeOut_) { //Hace fade out hasta desaparecer por completo y luego cambia de escena.
            if (elapsedTime_ >= SCENE_FADE_DURATION) {
                OhnOMenu app = new OhnOMenu();
                eng_.changeScene(app);
            } else elapsedTime_ += deltaTime;
        }
        return false;
    }

    /**
     * Actualiza los renderers, actualizando sus animaciones con deltaTime.
     */
    private void updateRenders(double deltaTime) {
        for (int i = 0; i < objects_.size(); ++i)
            objects_.get(i).updateRenderer(deltaTime);
        //Si deberian aparecer/desaparecer los candados se le dice a las celdas apropiadas que ciclen su visibilidad.
        if (!lockChanged_) return;
        for (int i = 0; i < boardSize_; ++i) {
            for (int j = 0; j < boardSize_; ++j) {
                if (lockChanged_ && boardRenderer_.getType(i, j) == CellRenderer.CELL_TYPE.LOCK)
                    boardRenderer_.changeLock(i, j);
            }
        }
        lockChanged_ = false;
    }

    //region Board Methods
    /**
     * Cambia el estado de una celda y lo que esto conlleva.
     */
    public void changeCell(int x, int y) {
        if (!infoReset_) {
            hintTextRender_.fadeNewText(infoRegContent_, INFO_REG_SIZE, false, TEXT_FADE_DURATION);
            boardRenderer_.endHighlighting();
            infoReset_ = true;
        }

        boardRenderer_.transitionCell(x, y);
        if (board_.changeCell(x, y)) {
            gameOver_ = true;
            hintTextRender_.fadeNewText(YOU_WIN_TEXTS[OhnORandom.r.nextInt(YOU_WIN_TEXTS.length)], INFO_WIN_SIZE, true, TEXT_FADE_DURATION);
        }

        progressTextRender_.setText(board_.donePercentage() + "%"); //Actualiza el porcentaje de progreso.
        timeForHint_ = 0; //Cuando se hace un input se reinicia la cuenta del tiempo de la pista que sale al completar el tablero mal.
    }

    private void giveHint() {
        board_.calculateHint();
        Hint hint = board_.hint;
        boardRenderer_.highlightCell(hint.x, hint.y); //Se destaca la celda para dar la pista.
        hintTextRender_.fadeNewText(hint.type.text, INFO_HINT_SIZE, false, TEXT_FADE_DURATION); //Se muestra la pista.
        infoReset_ = false;
    }

    /**
     * Deshace el ultimo movimiento hecho de ser posible.
     * Actualiza la interfaz en base a lo que se ha hecho.
     */
    private void undoMove() {
        String text = "";
        Tuple<Integer, Integer> cellPos = board_.undoMove();
        if (cellPos == null) {
            text = "No queda nada por hacer";
            boardRenderer_.endHighlighting();
        }
        else {
            switch (board_.getCurrState(cellPos.x, cellPos.y)) {
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
            boardRenderer_.transitionBack(cellPos.x, cellPos.y);
            boardRenderer_.highlightCell(cellPos.x, cellPos.y);
            progressTextRender_.setText(board_.donePercentage() + "%"); //Actualiza el porcentaje de progreso.
        }
        hintTextRender_.fadeNewText(text, INFO_HINT_SIZE, false, TEXT_FADE_DURATION); //Hace aparecer un texto con el string establecido.
        infoReset_ = false;
        timeForHint_ = 0; //Cuando se hace un input se reinicia la cuenta del tiempo de la pista que sale al completar el tablero mal.
    }
    //endregion

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
