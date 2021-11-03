package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;


public class TouchEvent {
    public int x;
    public int y;
    public TouchType type;
    public int finger;

    public enum TouchType { PRESS, LIFT, DRAG}
}
