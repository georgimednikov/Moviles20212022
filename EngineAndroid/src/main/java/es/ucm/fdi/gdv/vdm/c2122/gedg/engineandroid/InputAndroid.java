package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.view.MotionEvent;

import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid implements Input {

    private final int startingEvents = 5;
    private List<TouchEvent> events_;
    private List<TouchEvent> freeEvents_;

    public InputAndroid() {
        events_ = new ArrayList<>();
        for (int i = 0; i < startingEvents; ++i) events_.add(new TouchEvent());
        freeEvents_ = new ArrayList<>();
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

    public boolean createTouchEvent(MotionEvent e) {
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
        event.finger = e.getPointerId(0);
        addEvent(event);
        return true;
    }
    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}

/*    private void passEvent(MouseEvent e, TouchEvent.TouchType type){
        TouchEvent event = getEvent();
        event.type = type;
        event.finger = e.getPointerId(0);
        event.x = (int)e.getX(event.finger); event.y = (int)e.getY(event.finger);
        addEvent(event);
    }
    @Override
    public void mousePressed(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.PRESS);
    }
    @Override
    public void mouseReleased(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.LIFT);
    }
    @Override
    public void mouseDragged(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.DRAG);
    }*/