package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiene una pool de eventos por dentro a la que se puede acceder con addEvent(), lo que hace que
 * se meta un evento en la lista de eventos a la que tiene acceso el usuario mediante getEvent().
 * El usuario tiene que llamar a getEvent() en un bucle para recoger todos los eventos emitidos
 * durante el frame.
 */
public abstract class InputCommon implements Input {
    private final List<TouchEvent> events_;
    private final List<TouchEvent> freeEvents_;

    private int curPoolNumber_ = 0;

    private final int INITIAL_POOL_NUMBER = 20;
    // Para evitar que se llene toda la memoria de eventos
    private final int MAX_SIZE = 1000;

    protected InputCommon(){
        events_ = new ArrayList<>();
        freeEvents_ = new ArrayList<>();
        increasePool();
    }

    /**
     * Aumenta el tamaño de la lista de eventos libres, si no ha llegado ya al tamaño maximo.
     */
    private void increasePool() {
        if(curPoolNumber_ < MAX_SIZE) {
            curPoolNumber_ += INITIAL_POOL_NUMBER;
            for (int i = 0; i < INITIAL_POOL_NUMBER; ++i) freeEvents_.add(new TouchEvent());
        }
    }

    /**
    * Devuelve un evento de la lista de eventos libres para modificarlo, y lo mete en la lista de eventos utilizados.
    * Si la lista de eventos libres esta vacia (para evitar llenar demasiado la memoria), se coge del primer
    * evento de la lista de eventos vacios.
    */
    synchronized public TouchEvent addEvent(){
        if(freeEvents_.isEmpty()){
            increasePool();
        }
        TouchEvent aux;
        if(!freeEvents_.isEmpty()) {
            aux = freeEvents_.remove(0);
        } else {
            aux = events_.remove(0);
        }
        events_.add(aux);
        aux.x = 0;
        aux.y = 0;
        aux.finger = 0;
        aux.type = null;
        return aux;
    }

    /**
     *  Quita el ultimo evento de la pila y lo devuelve, null si no quedan.
     */
    synchronized public TouchEvent getEvent(){
        TouchEvent e = null;
        if(!events_.isEmpty()) {
            e = events_.remove(events_.size() - 1);
            freeEvents_.add(e);
        }
        return e;
    }
}
