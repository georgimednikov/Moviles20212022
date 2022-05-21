package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

/**
 * Evento genérico de input para PC y Android.
 */
public class TouchEvent {
    public int x;
    public int y;
    public TouchType type;
    public int finger; //Número del dedo. Se asigna en base al orden de llegada.

    public enum TouchType { PRESS, LIFT, DRAG} //Posibles tipos de eventos inclusivos para ambas plataformas.
}
