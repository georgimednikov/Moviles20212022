package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.view.SurfaceView;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;

public class EngineAndroid implements Engine, Runnable {

    //Variables del thread
    private Thread gameThread_;
    private volatile boolean running_;

    //Variables del deltaTime
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

    /**
     * Si no esta ya en ejecucion, se crea un nuevo hilo que corre en juego
     */
    public void onResume() {
        if (!running_) {
            running_ = true;
            gameThread_ = new Thread(this); // Esto es el run
            gameThread_.start();
        }
    }

    /**
     * Si esta en ejecucion la pausa, y espera a que el hilo muera
     */
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

    /**
     * Inicializa y ejecuta el bucle principal de la aplicacion
     */
    @Override
    public void run() {
        //Solo el hilo debe ejecutar el juego
        if (gameThread_ != Thread.currentThread()) {
            throw new RuntimeException("run() should not be called directly");
        }
        while(running_ && !graphics_.init()); //Se espera a que se inicialicen los graficos
        lastFrameTime_ = System.nanoTime();
        Application currApp;
        while(running_) { //Bucle principal
            currApp = app_; //Se actualiza la aplicacion con la que se trabaja
            updateDeltaTime();
            currApp.update();
            graphics_.lock(); //Se fija un canvas
            graphics_.setGamePosition();
            currApp.render(); //Se añaden elementos al canvas
            graphics_.unlock(); //Se desfija y renderiza
        }
    }

    /**
     * Asigna una nueva aplicacion y la inicializa
     * El siguiente frame se empieza a actualizar y renderizar
     */
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

    //No se usa en este caso init
    @Override
    public boolean init() { return true; }
}
