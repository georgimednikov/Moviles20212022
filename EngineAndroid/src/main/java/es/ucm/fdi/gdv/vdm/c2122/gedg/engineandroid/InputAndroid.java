package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.InputCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid extends InputCommon implements View.OnTouchListener {

    private GraphicsAndroid g_;

    private int startingEvents = 5;

    public InputAndroid(GraphicsAndroid g) {
        g_ = g;
        g_.getSurfaceView().setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        TouchEvent event = addEvent();
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
        event.x = g_.toVirtualX((int)e.getX(event.finger));
        event.y = g_.toVirtualY((int)e.getY(event.finger));
        return true;
    }
}