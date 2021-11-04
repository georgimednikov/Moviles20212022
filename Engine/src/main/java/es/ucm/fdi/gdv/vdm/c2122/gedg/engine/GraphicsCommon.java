package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public abstract class GraphicsCommon implements Graphics {

    private int refSizeX;
    private int refSizeY;

    public void setReferenceSize(int x, int y){
        refSizeX = x;
        refSizeY = y;
    }

    protected void setCanvasSize(int x, int y){
        
    }
    protected void adjustToWindowSize(){

    }
}
