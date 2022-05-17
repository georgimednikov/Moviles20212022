package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface State {
    void init(Engine eng);
    void update();
    void render();
}