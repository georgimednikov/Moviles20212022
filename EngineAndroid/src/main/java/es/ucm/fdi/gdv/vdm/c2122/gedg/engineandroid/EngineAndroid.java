package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;

public class EngineAndroid implements  Runnable implements Engine {

    private Input input_;
    private Graphics graphics_;
    private Application app_;

    @Override
    public void run() {  }
    @Override
    public Graphics getGraphics() {
        return graphics_;
    }
    @Override
    public Input getInput() {
        return input_;
    }
    @Override
    public void setApplication(Application a) {
        app_ = a;
    }
}
