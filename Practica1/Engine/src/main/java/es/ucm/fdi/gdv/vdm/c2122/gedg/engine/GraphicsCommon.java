package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Separa el tamaño lógico del físico de la pantalla.
 * Las posiciones que la lógica utiliza son respecto a refSize, que al llamar a toPhysical se escalan
 * según el tamaño de la ventana y se mueven según su relación de aspecto, de forma que esté colocado
 * el canvas en el centro de la pantalla pero la lógica no tenga que hacer cálculos para esto.
 */
public abstract class GraphicsCommon implements Graphics {

    //Tamaño de la ventana en la que trabaja la lógica.
    protected int refSizeX = 400;
    protected int refSizeY = 600;

    //Tamaño actual del canvas.
    protected int curSizeX;
    protected int curSizeY;

    //Posición actual del canvas.
    protected int curPosX;
    protected int curPosY;

    /**
     * Traduce una posición en el eje X de coordenadas de las que usa la lógica a coordenadas en el canvas real actual.
     */
    public int toPhysicalX(int a) { return (int)((float)a * curSizeX / refSizeX); }
    /**
     * Traduce una posición en el eje Y de coordenadas de las que usa la lógica a coordenadas en el canvas real actual.
     */
    public int toPhysicalY(int a) { return (int)((float)a * curSizeY / refSizeY); }
    /**
     * Traduce una posición en el eje X de coordenadas en el canvas real actual a las que usa la lógica.
     */
    public int toVirtualX(int a) { return (int)((float)(a - curPosX) * refSizeX / curSizeX ); }
    /**
     * Traduce una posición en el eje Y de coordenadas en el canvas real actual a las que usa la lógica.
     */
    public int toVirtualY(int a) { return (int)((float)(a - curPosY) * refSizeY / curSizeY ); }

    /**
     * Fija un nuevo tamaño de canvas virtual en el que trabaja la lógica.
     */
    public void setReferenceSize(int x, int y){
        refSizeX = x;
        refSizeY = y;
    }

    /**
     * Calcula las proporciones que se usan para "traducir" el canvas virtual que usa la lógica a la ventana real,
     * manteniendo la relación de altura/ancho que dictan las variables refSizeX y refSizeY.
     * @param x Nuevo ancho de ventana.
     * @param y Nuevo alto de ventana.
     */
    protected void adjustToWindowSize(int x, int y){
        if((float)refSizeY / refSizeX > (float)y / x) {
            // Hay que poner márgenes en X
            curSizeX = refSizeX * y / refSizeY;
            curSizeY = y;
            curPosX = (x - curSizeX) / 2;
            curPosY = 0;
        } else {
            // Hay que poner márgenes en Y
            curSizeX = x;
            curSizeY = refSizeY * x / refSizeX;
            curPosX = 0;
            curPosY = (y - curSizeY) / 2;
        }
    }
}
