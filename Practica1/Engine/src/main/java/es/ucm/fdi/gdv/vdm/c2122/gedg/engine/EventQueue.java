package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import java.util.ArrayList;
import java.util.List;

public class EventQueue {

    private final List<TouchEvent> queue_;

    public EventQueue(){
        queue_ = new ArrayList<>();
    }

    /**
     * Duplica el tamaño de la cola de eventos libres.
     */
    private void increasePool() {
        int increase = queue_.size();
        for (int i = 0; i < increase; ++i)
            queue_.add(new TouchEvent());
    }

    /**
     * Coge una instancia de los eventos de la pool.
     */
    public synchronized TouchEvent getItem(){
        if(queue_.isEmpty())
            increasePool();
        TouchEvent t = queue_.remove(0);
        return t;
    }

    /**
     * Devuelve un evento a la pool.
    */
    public synchronized void releaseItem(TouchEvent t){
        queue_.add(t);
    }
}
