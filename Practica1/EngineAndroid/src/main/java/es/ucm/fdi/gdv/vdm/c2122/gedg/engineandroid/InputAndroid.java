package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.view.MotionEvent;
import android.view.View;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.EventPool;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

/**
 * Clase que detecta, guarda y permite acceder a los eventos que se generan durante la ejecución.
 */
public class InputAndroid implements View.OnTouchListener, Input {

    private GraphicsAndroid g_;
    private EventPool pool_; //Pool de eventos para evitar la constante generación y destrucción de instancias.

    public InputAndroid(GraphicsAndroid g) {
        g_ = g;
        pool_ = new EventPool();
        g_.getSurfaceView().setOnTouchListener(this); //Se añade como listener de la aplicacion
    }

    /**
     * Recoge un evento de input y la view de Android en la que ha ocurrido y la transforma a un evento
     * genérico TouchEvent que se puede usar en la lógica.
     * @param view View de Android.
     * @param e Evento de input de Android.
     */
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        //Se asigna el dedo.
        int pointerIndex = ((e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId;
        try {
            int mActivePointerId = e.getPointerId(pointerIndex);
            pointerId = e.findPointerIndex(mActivePointerId);

            //Se asigna el tipo.
            TouchEvent.TouchType type = null;
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    type = TouchEvent.TouchType.PRESS;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP:
                    type = TouchEvent.TouchType.LIFT;
                    break;
                case MotionEvent.ACTION_MOVE:
                    type = TouchEvent.TouchType.DRAG;
                    break;
            }

            //Se añade a la cola si el evento es válido.
            if (type != null)
                pool_.enqueueEvent(g_.toVirtualX((int)e.getX(pointerId)), g_.toVirtualY((int)e.getY(pointerId)), pointerId, type);
        }catch (Exception f){
            f.printStackTrace();
        }
        return true;
    }

    /**
     * Saca un evento de la pool y lo devuelve. Si no hay devuelve null.
     * Se devuelven en orden de llegada a la pool.
     */
    @Override
    public TouchEvent dequeueEvent(){
       return pool_.dequeueEvent();
    }

    /**
     * Libera los eventos que se han ido pidiendo con dequeueEvent para continuar su ciclo de vida.
     */
    @Override
    public void releaseEvents(){
        pool_.releaseEvents();
    }
}