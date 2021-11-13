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

    private double lastFrameTime_;
    private double deltaTime_;
    private InputAndroid input_;
    private GraphicsAndroid graphics_;
    private Application app_;

    public EngineAndroid(Context context) {
        graphics_ = new GraphicsAndroid(context);
        input_ = new InputAndroid(graphics_);
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

    private void updateDeltaTime() {
        double currentTime = System.nanoTime();
        double nanoElapsedTime = currentTime - lastFrameTime_;
        lastFrameTime_ = currentTime;
        deltaTime_ = nanoElapsedTime / 1.0E9;
    }
    public double getDeltaTime() { return deltaTime_; }

    @Override
    public void run() {
        if (gameThread_ != Thread.currentThread()) {
            throw new RuntimeException("run() should not be called directly");
        }
        while(running_ && graphics_.init());
        lastFrameTime_ = System.nanoTime();
        Application currApp;
        while(running_) {
            currApp = app_;
            currApp.update();
            graphics_.lock();
            graphics_.setGamePosition();
            currApp.render();
            graphics_.unlock();
            updateDeltaTime();
        }
        app_.close();
    }

    @Override
    public void setApplication(Application a) {
        app_ = a;
        a.setEngine(this);
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
