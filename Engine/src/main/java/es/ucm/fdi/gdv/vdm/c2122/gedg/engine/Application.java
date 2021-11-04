package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Application {
    boolean init();
    void update();
    void render();
    //void receiveEvents(Event e);
    boolean close();
}
