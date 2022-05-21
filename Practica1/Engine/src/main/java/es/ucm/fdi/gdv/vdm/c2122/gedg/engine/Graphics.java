package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Interfaz del motor gráfico que se usa en el proyecto. Tiene una representación interna
 * de un canvas, una "pantalla" en el que se dibujan los elementos para luego representarlo en pantalla.
 */
public interface Graphics {

    /**
     * Dada la ruta de una imagen, devuelve la imagen cargada a la clase apropiada.
     */
    Image newImage(String name);

    /**
     * Dada la ruta de una fuente, devuelve la fuente cargada a la clase apropiada.
     */
    Font newFont(String filename, Color color, int size, boolean isBold);

    /**
     * Pinta toda la pantalla del color dado.
     */
    void clear(Color color);

    /**
     * Dibuja una imagen en la posición dada, con el ancho, alto y opacidad declarados.
     * @param centered Dicta si se dibuja centrada en la posición dada o si esta representa su esquina superior derecha.
     */
    void drawImage(Image image, int x, int y, int width, int height, boolean centered, float opacity);

    /**
     * Fija el color con el que se va a pintar los siguientes elementos.
     */
    void setColor(Color color);

    /**
     * Dibuja un círculo en la posición dada con el radio dado, del color declarado. Negro si no se ha declarado.
     */
    void fillCircle(int cx, int cy, int r);

    /**
     * Dibuja un texto con una fuente y posición dadas.
     * @param centered Dicta si se dibuja centrada en la posición dada o si esta representa su esquina superior derecha.
     */
    void drawText(Font font, String text, int x, int y, boolean centered);

    /**
     * Devuelve el ancho de la ventana.
     */
    int getWidth();

    /**
     * Devuelve el alto de la ventana.
     */
    int getHeight();

    /**
     * Calcula y devuelve la anchura de un texto dado este y su fuente.
     */
    int getTextWidth(Font font, String string);

    /**
     * Calcula y devuelve la altura de un texto dado este y su fuente.
     */
    int getTextHeight(Font font, String string);

    /**
     * Desplaza la posición del canvas las cantidades declaradas en X e Y en base a las dimensiones de la lógica.
     */
    void translate(int dx, int dy);

    /**
     * Escala el tamaño del canvas a unas dimensiones dadas (por defecto 1, 1).
     */
    void scale(float sx, float sy);

    /**
     * Guarda el estado actual del canvas. Se puede llamar varias veces consecutivas.
     */
    void save();

    /**
     * Restaura el último estado guardado del canvas y se borra. Si no hay ningún estado guardado no se hace nada.
     */
    void restore();
}
