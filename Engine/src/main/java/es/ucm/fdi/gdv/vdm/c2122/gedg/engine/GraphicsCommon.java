package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public abstract class GraphicsCommon implements Graphics {

    private int refSizeX = 400;
    private int refSizeY = 600;

    protected int curSizeX;
    protected int curSizeY;

    protected int curPosX;
    protected int curPosY;

    public void setReferenceSize(int x, int y){
        refSizeX = x;
        refSizeY = y;
    }

    protected void setCanvasSize(int x, int y){
        curSizeX = x;
        curSizeY = y;
    }

    protected void adjustToWindowSize(int x, int y){
        if((float)refSizeY / refSizeX > (float)y / x) {
            // Barras en X
            curSizeX = refSizeX * y / refSizeY;
            curSizeY = y;
            curPosX = (x - curSizeX) / 2;
            curPosY = 0;
        } else {
            // Barras en Y
            curSizeX = x;
            curSizeY = refSizeY * x / refSizeX;
            curPosX = 0;
            curPosY = (y - curSizeY) / 2;
        }
    }

    public void scale(float sx, float sy) {
        adjustToWindowSize((int) sx, (int) sy);
    }
}
