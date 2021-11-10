package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Application {
    void setEngine(Engine eng);

    boolean init();
    void update();
    void render();
    boolean close();
}