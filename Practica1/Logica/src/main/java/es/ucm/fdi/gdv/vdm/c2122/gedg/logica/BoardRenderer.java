package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;

public class BoardRenderer extends ObjectRenderer {

    private int boardSize_;
    private int cellRadius_;
    private int cellSeparation_;

    private CellRenderer[][] board_;
    private Board gameState_;

    // colors: wall, fill, empty
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

    @Override
    public void render(Graphics g){
        //Se pintan las celdas
        for (int i = 0; i < boardSize_; ++i) {
            g.save();
            g.translate(cellRadius_, cellRadius_ * (i + 1) + (cellRadius_ + cellSeparation_) * i);
            for (int j = 0; j < boardSize_; ++j) {
                board_[i][j].setState(gameState_.getCurrState(i, j));
                board_[i][j].render(g);
                g.translate(cellRadius_ * 2 + cellSeparation_, 0);
            }
            g.restore();
        }
    }

    @Override
    public void updateRenderer(double deltaTime){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].updateRenderer(deltaTime);
            }
        }
    }

    @Override
    public void fadeOut(float time){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].fadeOut(time);
            }
        }
    }

    @Override
    public void fadeIn(float time){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].fadeIn(time);
            }
        }
    }

    @Override
    public void changeState(){
        for(int i = 0; i < board_.length; ++i){
            for(int j = 0; j < board_.length; ++j){
                board_[i][j].changeState();
            }
        }
    }

    public void changeLock(int x, int y){
        board_[x][y].changeLock();
    }

    public CellRenderer.CELL_TYPE getType(int x, int y){
        return board_[x][y].type_;
    }

    public void bumpCell(int x, int y){
        board_[x][y].bumpCell();
    }

    public void transitionCell(int x, int y){
        board_[x][y].transitionCell();
    }
}
