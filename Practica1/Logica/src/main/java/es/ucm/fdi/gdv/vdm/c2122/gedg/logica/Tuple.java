package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

/**
 * Clase de tuplas o parejas de tipos múltiples.
 */
public class Tuple<X, Y> {
    public final X x;
    public final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }
}