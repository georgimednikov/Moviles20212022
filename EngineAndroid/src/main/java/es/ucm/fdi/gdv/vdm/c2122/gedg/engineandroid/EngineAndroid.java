package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.view.SurfaceView;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;

public class EngineAndroid implements Engine, Runnable {

    private Thread gameThread_;
    private volatile boolean running_;

    private InputAndroid input_;
    private GraphicsAndroid graphics_;
    private Application app_;

    public EngineAndroid(Context context) {
        input_ = new InputAndroid();
        //TODO: No tengo nada claro que graphics tenga que tener el holder
        graphics_ = new GraphicsAndroid(context);
    }

    public SurfaceView getSurfaceView() {
        return graphics_.getSurfaceView();
    }

    public void onResume() {
        if (!running_) {
            running_ = true;
            gameThread_ = new Thread(this); // Esto es el run
            gameThread_.start();
        }
    }

    public void onPause() {
        if (running_) {
            running_ = false;
            while (true) {
                try {
                    gameThread_.join();
                    gameThread_ = null;
                    break;
                } catch (InterruptedException ie) {
                    //No deberia pasar
                }
            }
        }
    }

    @Override
    public void run() {
        if (gameThread_ != Thread.currentThread()) {
            throw new RuntimeException("run() should not be called directly");
        }
        while(running_ && graphics_.init());
        while(running_) {
            app_.update();
            graphics_.lock();
            graphics_.setGamePosition();
            app_.render();
            graphics_.unlock();
        }
        app_.close();
    }
    @Override
    public void setApplication(Application a) {
        app_ = a;
        app_.init();
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
    public boolean init() { return true; }
    @Override
    public boolean close() { return true; }
}
