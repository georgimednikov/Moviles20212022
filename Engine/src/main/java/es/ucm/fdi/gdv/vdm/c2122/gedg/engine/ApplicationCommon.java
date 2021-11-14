package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public abstract class ApplicationCommon implements Application{
    protected Engine eng_;

    public void setEngine(Engine eng) {
        this.eng_ = eng;
    }
}
