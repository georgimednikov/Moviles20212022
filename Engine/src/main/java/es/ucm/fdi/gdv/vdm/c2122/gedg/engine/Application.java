package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Application {
    void setEngine(Engine eng);

    void init();
    void update();
    void render();
}