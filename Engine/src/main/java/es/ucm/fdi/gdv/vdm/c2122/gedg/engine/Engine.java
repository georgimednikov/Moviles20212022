package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;

public interface Engine {
    void setApplication(Application app);
    Graphics getGraphics();
    Input getInput();
    double getDeltaTime();
    boolean init();
    boolean close();
    void run();
}
