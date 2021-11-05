package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.view.SurfaceView;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;

//TODO: No tengo nada claro que tenga que extendear SurfaceView
public class EngineAndroid extends SurfaceView implements Engine, Runnable {

    private Thread renderThread_;
    private boolean running_;

    private InputAndroid input_;
    private GraphicsAndroid graphics_;
    private Application app_;

    public EngineAndroid(Context context) {
        super(context);
        input_ = new InputAndroid();
        //TODO: No tengo nada claro que graphics tenga que tener el holder
        graphics_ = new GraphicsAndroid(context, getHolder());
    }

    public void setApplication(Application a) {
        app_ = a;
        app_.init();
    }

    public void resume() {
        if (!running_) {
            running_ = true;
            renderThread_ = new Thread(this);
            renderThread_.start();
        }
    }

    public void pause() {
        if (running_) {
            running_ = false;
            while (true) {
                try {
                    renderThread_.join();
                    renderThread_ = null;
                    break;
                } catch (InterruptedException ie) {
                    //No deberia pasar
                }
            }
        }
    }

    @Override
    public void run() {
        if (renderThread_ != Thread.currentThread()) {
            throw new RuntimeException("run() should not be called directly");
        }
        while(running_ && graphics_.getWidth() == 0);
        while(running_) {
            app_.update();
            graphics_.lock();
            app_.render();
            graphics_.unlock();
        }
        app_.close();
    }
    @Override
    public Graphics getGraphics() {
        return graphics_;
    }
    @Override
    public Input getInput() {
        return input_;
    }
    @Override
    public boolean close() { return false; }
}
