package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputPC implements Input, MouseListener, MouseMotionListener {

    private final int startingEvents = 5;
    private List<TouchEvent> events_;
    private List<TouchEvent> freeEvents_;
    private GraphicsPC g_;

    public InputPC(GraphicsPC g) {
        g_ = g;
        g.getJFrame().addMouseListener(this);
        g.getJFrame().addMouseMotionListener(this);
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
    public void mouseEntered(MouseEvent mouseEvent) {}
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {}
    @Override
    public void mouseExited(MouseEvent mouseEvent) {}
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {}

    private void passEvent(MouseEvent e, TouchEvent.TouchType type) {
        TouchEvent event = getEvent();
        event.type = type;

        event.x = g_.toVirtualX((int)e.getX());
        event.y = g_.toVirtualY((int)e.getY());
        event.finger = 0;
        addEvent(event);
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}
