package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class BoardRenderer extends ObjectRenderer {

    private int boardSize_;
    private int cellRadius_; //Radio del círculo de las celdas.
    private int cellSeparation_; //Separación entre celdas.

    private Board gameState_; //Puntero a la representación lógica del tablero.
    private CellRenderer[][] board_; //Representación del tablero de renderizado, análogo al lógico.
    private CellRenderer highlightedCell_ = null; //La celda que está siendo resaltada actualmente con una sombra.

    public BoardRenderer(Font font, Image lock, int size, int cellRadius, int cellSeparation, Board gameState){
        super(true);
        boardSize_ = size;
        cellSeparation_ = cellSeparation;
        cellRadius_ = cellRadius;
        board_ = new CellRenderer[size][size];
        gameState_ = gameState;

        // Fija la información que necesitan los renderers una vez se ha terminado la generación del tablero.
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                CellRenderer render = new CellRenderer(cellRadius);
                board_[i][j] = render;
                if (gameState_.isFixed(i, j)) { //Si es fija...
                    //... y roja se añade a la lista de celdas de renderiza con candado
                    if (gameState_.getCurrState(i, j) == Cell.STATE.RED) {
                        render.setTypeLock(lock);
                    }
                    //... y azul entonces enseña su numero
                    else
                        render.setTypeNumber(font, "" + gameState_.getNumber(i, j));
                }
            }
        }
    }

    /**
     * Renderiza cada celda del tablero moviendo el canvas para dibujarlas en la posición correcta.
     */
    @Override
    public void render(Graphics g){
        //Se pintan las celdas
        for (int i = 0; i < boardSize_; ++i) { //Se avanza en vertical.
            g.save();
            g.translate(cellRadius_, cellRadius_ * (i + 1) + (cellRadius_ + cellSeparation_) * i);
            for (int j = 0; j < boardSize_; ++j) { //Se avanza en horizontal.
                board_[i][j].setState(gameState_.getCurrState(i, j)); //Se mira el estado de la celda en el tablero lógico.
                board_[i][j].render(g);
                g.translate(cellRadius_ * 2 + cellSeparation_, 0);
            }
            g.restore();
        }
    }

    /**
     * Actualiza el tiempo de las animaciones de los renderers.
     */
    @Override
    public void updateRenderer(double deltaTime){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].updateRenderer(deltaTime);
            }
        }
    }

    /**
     * Hace desaparecer progresivamente todas las celdas del tablero.
     */
    @Override
    public void fadeOut(float time){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].fadeOut(time);
            }
        }
    }

    /**
     * Hace aparecer progresivamente todas las celdas del tablero.
     */
    @Override
    public void fadeIn(float time){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].fadeIn(time);
            }
        }
    }

    /**
     * Alterna si el candado de la celda en la posición dada es visible o no.
     */
    public void changeLock(int x, int y){
        board_[x][y].changeLockVisibility();
    }

    /**
     * Devuelve el tipo de celda que hay en la posición. Pueden ser normales, con candado o con número.
     */
    public CellRenderer.CELL_TYPE getType(int x, int y){
        return board_[x][y].type_;
    }

    /**
     * Le indica a la celda en la posición dada que tiene que iniciar la animación de dar "golpes".
     */
    public void bumpCell(int x, int y){
        board_[x][y].bumpCell();
    }

    /**
     * Transiciona la celda de su color anterior al actual.
     */
    public void transitionCell(int x, int y){
        board_[x][y].transitionCell();
    }

    /**
     * Anima la celda en la posición dada de forma que transiciona a su estado anterior.
     */
    public void transitionBack(int x, int y){ board_[x][y].transitionBack(); }

    /**
     * Deja de destacar la celda destacada de haber.
     */
    public void endHighlighting() {
        if (highlightedCell_ != null) {
            highlightedCell_.setHighlight(false);
            highlightedCell_ = null;
        }
    }

    /**
     * Devuelve tru si hay una celda destacada.
     */
    public boolean isCellHighlighted() {
        return highlightedCell_ != null;
    }

    /**
     * Destaca la celda en las coordenadas indicadas.
     */
    public void highlightCell(int x, int y) {
        endHighlighting();
        board_[x][y].setHighlight(true);
        highlightedCell_ = board_[x][y];
    }
}
