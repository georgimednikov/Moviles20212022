package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import java.util.ArrayList;
import java.util.List;

/**
 * Tiene una pool de eventos por dentro a la que se puede acceder con enqueueEvent(), lo que hace que
 * se meta un evento en la cola de eventos a la que tiene acceso el usuario mediante dequeueEvent().
 * El usuario tiene que llamar a dequeueEvent() en un bucle para recoger todos los eventos emitidos
 * durante el frame. Si al final del frame quedan eventos por procesar, se quedan dentro
 * de la cola de eventos a procesar.
 */
public class EventPool {
    private final List<TouchEvent> enqueuedEvents_; //Eventos que hay que procesar.
    private final List<TouchEvent> dequeuedEvents_; //Elementos procesados que pueden seguir en uso y no se pueden liberar.
    private final List<TouchEvent> freeEvents_; //Eventos libres que se pueden utilizar.
    private int poolSize_ = 10;

    public EventPool(){
        enqueuedEvents_ = new ArrayList<>();
        dequeuedEvents_ = new ArrayList<>();
        freeEvents_ = new ArrayList<>();
        for (int i = 0; i < poolSize_; ++i) freeEvents_.add(new TouchEvent()); //Se crea una cantidad inicial de eventos libres.
    }


    /**
    * Devuelve un evento de la cola de eventos libres para modificarlo, y lo mete en la cola de eventos utilizados.
    */
    synchronized public void enqueueEvent(int x, int y, int finger, TouchEvent.TouchType type){
        if(freeEvents_.isEmpty()){
            for (int i = 0; i < poolSize_; ++i) freeEvents_.add(new TouchEvent());
            // Duplica el numero de eventos para que la proxima vez que haya que crear eventos ocurra cada vez
            // menos, disminuyendo el coste asintotico a O(1).
            poolSize_ = poolSize_ * 2;
        }
        TouchEvent aux = freeEvents_.remove(0);
        enqueuedEvents_.add(aux);
        aux.x = x;
        aux.y = y;
        aux.finger = finger;
        aux.type = type;
    }

    /**
     * Quita el primer evento de la cola y lo devuelve, null si no quedan.
     */
    synchronized public TouchEvent dequeueEvent(){
        TouchEvent e = null;
        if(!enqueuedEvents_.isEmpty()) {
            e = enqueuedEvents_.remove(0);
            dequeuedEvents_.add(e);
        }
        return e;
    }

    /**
     * Los eventos que se han procesado en el frame se marcan como libres para poder procesarse el frame siguiente
     * Si quedan eventos sin procesar, se mantienen en la cola de eventos a procesar
     */
    synchronized public void releaseEvents(){
        while (!enqueuedEvents_.isEmpty()){
            TouchEvent e = dequeuedEvents_.remove(0);
            freeEvents_.add(e);
        }
    }
}
