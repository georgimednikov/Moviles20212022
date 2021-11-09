package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid implements Input, View.OnTouchListener {

    private final int startingEvents = 5;
    private List<TouchEvent> events_;
    private List<TouchEvent> freeEvents_;

    public InputAndroid(GraphicsAndroid g) {
        events_ = new ArrayList<>();
        freeEvents_ = new ArrayList<>();
        for (int i = 0; i < startingEvents; ++i) freeEvents_.add(new TouchEvent());
        g.getSurfaceView().setOnTouchListener(this);
    }

    synchronized private void addEvent(TouchEvent e) {
        events_.add(e);
    }

    synchronized private TouchEvent getEvent() {
        TouchEvent e;
        if (freeEvents_.isEmpty()) e = new TouchEvent();
        else {
            e = freeEvents_.remove(0);
        }
        return e;
    }
    @Override
    public boolean onTouch(View view, MotionEvent e) {
        TouchEvent event = getEvent();
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
        event.x = (int)e.getX(event.finger);
        event.y = (int)e.getY(event.finger);
        addEvent(event);
        return true;
    }
    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}