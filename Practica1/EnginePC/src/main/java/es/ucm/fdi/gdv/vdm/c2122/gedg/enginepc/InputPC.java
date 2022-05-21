package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.EventPool;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

/**
 * Clase que detecta, guarda y permite acceder a los eventos que se generan durante la ejecución.
 */
public class InputPC implements MouseListener, MouseMotionListener, Input {

    private GraphicsPC g_;
    private EventPool pool_; //Pool de eventos para evitar la constante generación y destrucción de instancias.

    public InputPC(GraphicsPC g) {
        g_ = g;
        pool_ = new EventPool();

        //Se añade como listener de la aplicacion
        g.getJFrame().addMouseListener(this);
        g.getJFrame().addMouseMotionListener(this);
    }

    /**
     * Convierte la pulsación de un botón del ratón en un evento TouchEvent que se almacena en la pool.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.PRESS);
    }

    /**
     * Convierte la liberación de un botón del ratón en un evento TouchEvent que se almacena en la pool.
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        passEvent(e, TouchEvent.TouchType.LIFT);
    }

    /**
     * Convierte un movimiento del ratón en un evento TouchEvent que se almacena en la pool.
     */
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
     * Procesa un evento de raton y lo convierte en TouchEvent, que es genérico.
     * Se guardan en la pool en orden.
     * @param e Evento
     * @param type Tipo del evento
     */
    private void passEvent(MouseEvent e, TouchEvent.TouchType type) {
        //Se transforman a coordenas virtuales para la logica
        pool_.enqueueEvent(g_.toVirtualX((int)e.getX()), g_.toVirtualY((int)e.getY()), 0, type);
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
