package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Engine {
    void setApplication(Application app);
    Graphics getGraphics();
    Input getInput();
    double getDeltaTime();
    boolean init();
    void run();
}
