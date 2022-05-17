package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Engine {
    void changeState(State app);
    Graphics getGraphics();
    Input getInput();
    double getDeltaTime();
}
