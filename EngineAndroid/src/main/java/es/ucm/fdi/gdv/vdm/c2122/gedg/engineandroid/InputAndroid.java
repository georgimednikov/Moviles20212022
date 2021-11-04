package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid implements Input {

    private List<TouchEvent> events_;

    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}
