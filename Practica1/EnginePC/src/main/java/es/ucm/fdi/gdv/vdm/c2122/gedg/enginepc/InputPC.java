package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.InputCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputPC extends InputCommon implements MouseListener, MouseMotionListener {

    private GraphicsPC g_;

    public InputPC(GraphicsPC g) {
        g_ = g;
        //Se añade como listener de la aplicacion
        g.getJFrame().addMouseListener(this);
        g.getJFrame().addMouseMotionListener(this);
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

    //Metodos que hay que overridear pero no se quieren
    @Override
    public void mouseEntered(MouseEvent mouseEvent) {}
    @Override
    public void mouseClicked(MouseEvent mouseEvent) {}
    @Override
    public void mouseExited(MouseEvent mouseEvent) {}
    @Override
    public void mouseMoved(MouseEvent mouseEvent) {}

    /**
     * Procesa un evento de raton y lo convierte en TouchEvent, que es generico
     * @param e Evento
     * @param type Tipo del evento
     */
    private void passEvent(MouseEvent e, TouchEvent.TouchType type) {
        //Se transforman a coordenas virtuales para la logica
        enqueueEvent(g_.toVirtualX((int)e.getX()), g_.toVirtualY((int)e.getY()), 0, type);
    }
}
