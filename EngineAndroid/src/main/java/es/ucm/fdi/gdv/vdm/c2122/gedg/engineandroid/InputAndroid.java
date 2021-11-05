package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import java.util.ArrayList;
import java.util.List;
import java.awt.event.*;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid implements MouseListener, Input {

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
        else e = freeEvents_.get(0);
        return e;
    }

    private void passEvent(MouseEvent e, TouchEvent.TouchType type){
        TouchEvent event = getEvent();
        event.type = type;
        event.finger = e.getPointerId(0); //TODO: NO SE SI LA ID ESTA BIEN (?)
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
    }
    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}