package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Interfaz que define tipos de fuente que pueden usar los textos.
 */
public interface Font {

    /**
     * Fija el color de la fuente con el que se va a escribir.
     */
    void setColor(Color color);

    /**
     * Fija el tamaño de la fuente con el que se va a escribir.
     */
    void setRenderSize(int size);

    /**
     * Fija el tamaño original de la fuente para poder hacer reset en caso de que haya sido modificado.
     */
    void setSize(int size);

    /**
     * Fija la negrita de la fuente con el que se va a escribir.
     */
    void setBold(boolean isBold);

    /**
     * Devuelve el tamaño de dibujado de la fuente.
     */
    int	getSize();

    /**
     * Devuelve el color del a fuente.
     */
    Color getColor();
}
