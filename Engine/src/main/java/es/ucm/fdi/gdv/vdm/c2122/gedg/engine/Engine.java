package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;

public interface Engine {
    Graphics getGraphics();
    Input getInput();
    void run();
    void setApplication(Application a);
}
