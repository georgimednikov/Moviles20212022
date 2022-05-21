package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Interfaz que representa un nivel del juego llamado "escena".
 */
public interface Scene {

    /**
     * Inicia la escena. Recibe el motor para poder renderizarla, actualizarla y detectar inputs.
     */
    void init(Engine eng);

    /**
     * Actualiza la escena y los elementos que contiene.
     */
    void update();

    /**
     * Dibuja la escena y los elementos que contiene.
     */
    void render();
}