package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Interfaz de motor genérica.
 */
public interface Engine {

    /**
     * Recibe una escena y la guarda para cambiarla al final del frame actual.
     */
    void changeScene(Scene scene);

    /**
     * Devuelve el motor gráfico del motor.
     */
    Graphics getGraphics();

    /**
     * Devuelve el sistema de inputs del motor.
     */
    Input getInput();

    /**
     * Devuelve el tiempo que ha pasado entre el frame actual y el anterior.
     */
    double getDeltaTime();
}
