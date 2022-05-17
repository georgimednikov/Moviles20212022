package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiene una pool de eventos por dentro a la que se puede acceder con enqueueEvent(), lo que hace que
 * se meta un evento en la cola de eventos a la que tiene acceso el usuario mediante dequeueEvent().
 * El usuario tiene que llamar a dequeueEvent() en un bucle para recoger todos los eventos emitidos
 * durante el frame.
 */
public abstract class InputCommon implements Input {
    private final List<TouchEvent> events_;
    private final List<TouchEvent> freeEvents_;

    protected InputCommon(){
        events_ = new ArrayList<>();
        freeEvents_ = new ArrayList<>();
    }


    /**
    * Devuelve un evento de la cola de eventos libres para modificarlo, y lo mete en la cola de eventos utilizados.
    * Si la cola de eventos libres esta vacia, se coge del primer
    * evento de la cola de eventos vacios.
    */
    synchronized public void enqueueEvent(int x, int y, int finger, TouchEvent.TouchType type){
        if(freeEvents_.isEmpty()){
            // TODO: hacer bien
            for (int i = 0; i < 10; ++i) freeEvents_.add(new TouchEvent());
        }
        TouchEvent aux = freeEvents_.remove(0);
        events_.add(aux);
        aux.x = x;
        aux.y = y;
        aux.finger = finger;
        aux.type = type;
    }

    /**
     *  Quita el primer evento de la cola y lo devuelve, null si no quedan.
     */
    synchronized public TouchEvent dequeueEvent(){
        TouchEvent e = null;
        TouchEvent copy = null;
        if(!events_.isEmpty()) {
            copy = new TouchEvent();
            e = events_.remove(0);
            copy.finger = e.finger;
            copy.type = e.type;
            copy.x = e.x;
            copy.y = e.y;
            freeEvents_.add(e);
        }
        return copy;
    }
}
