package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Interfaz de clase que tiene que encargarse de recoger, almacenar y proporcionar eventos de input.
 */
public interface Input {

    /**
     * Saca un evento de la pool y lo devuelve. Si no hay devuelve null.
     * Se devuelven en orden de llegada a la pool.
     */
    TouchEvent dequeueEvent();

    /**
     * Libera los eventos que se han ido pidiendo con dequeueEvent para continuar su ciclo de vida.
     */
    void releaseEvents();
}
