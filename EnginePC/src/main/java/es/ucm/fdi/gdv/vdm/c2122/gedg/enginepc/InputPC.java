package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputPC implements Input {

    private final int startingEvents = 5;
    private List<TouchEvent> events_;
    private List<TouchEvent> freeEvents_;

    public InputPC() {
        events_ = new ArrayList<>();
        freeEvents_ = new ArrayList<>();
        for (int i = 0; i < startingEvents; ++i) freeEvents_.add(new TouchEvent());
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

    public void mousePressed(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.PRESS);
    }

    public void mouseReleased(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.LIFT);
    }

    public void mouseDragged(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.DRAG);
    }

    private void passEvent(MouseEvent e, TouchEvent.TouchType type) {
        TouchEvent event = getEvent();
        event.type = type;
        event.x = (int)e.getX();
        event.y = (int)e.getY();
        event.finger = 0;
        addEvent(event);
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}
