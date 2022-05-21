package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Clase interna que se usa para representar los colores independientemente de la plataforma.
 */
public class Color {
    public int r;
    public int g;
    public int b;
    public int a;

    public Color() {
        this.r = 255;
        this.g = 255;
        this.b = 255;
        this.a = 255;
    }

    public Color(int r, int g, int b, int a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }
}
