package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public class ApplicationCommon implements Application{
    protected Engine eng_;

    @Override
    public void setEngine(Engine eng) {
        this.eng_ = eng;
    }

    @Override
    public boolean init() {
        return false;
    }

    @Override
    public void update() {}

    @Override
    public void render() {}

    @Override
    public boolean close() {
        return false;
    }
}