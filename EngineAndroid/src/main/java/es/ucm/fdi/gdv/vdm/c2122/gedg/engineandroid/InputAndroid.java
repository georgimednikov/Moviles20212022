package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.view.MotionEvent;
import android.view.View;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.InputCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid extends InputCommon implements View.OnTouchListener {

    private GraphicsAndroid g_;

    public InputAndroid(GraphicsAndroid g) {
        g_ = g;
        g_.getSurfaceView().setOnTouchListener(this); //Se añade como listener de la aplicacion
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        TouchEvent event = addEvent(); //Se coge un evento de la pool o se crea si no hay
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                event.type = TouchEvent.TouchType.PRESS;
                break;
            case MotionEvent.ACTION_UP:
                event.type = TouchEvent.TouchType.LIFT;
                break;
            case MotionEvent.ACTION_MOVE:
                event.type = TouchEvent.TouchType.DRAG;
                break;
        }
        event.finger = 0; //TODO: Esto esta mal fijo pero no se como va
        //Se transforman a coordenas virtuales para la logica
        event.x = g_.toVirtualX((int)e.getX(event.finger));
        event.y = g_.toVirtualY((int)e.getY(event.finger));
        return true; //Siempre se descarta un evento procesado
    }
}