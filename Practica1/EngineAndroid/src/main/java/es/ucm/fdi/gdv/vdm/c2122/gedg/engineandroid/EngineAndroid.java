package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.content.Context;
import android.view.SurfaceView;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Scene;

/**
 * Clase que representa un Motor en la plataforma de Android. Procesa Inputs, crea y dibuja en pantalla y actualiza sus elementos.
 */
public class EngineAndroid implements Engine, Runnable {

    //Variables del thread
    private Thread gameThread_;
    private volatile boolean running_; //Si está en ejecución.

    //Variables del deltaTime
    private double lastFrameTime_;
    private double deltaTime_;

    private InputAndroid input_; //Sistema de procesado de inputs.
    private GraphicsAndroid graphics_; //Motor gráfico.
    private Scene app_;

    public EngineAndroid(Context context, SurfaceView view) {
        graphics_ = new GraphicsAndroid(context, view);
        input_ = new InputAndroid(graphics_);
    }

    /**
     * Si no esta ya en ejecucion, se crea un nuevo hilo que ejecuta en juego.
     */
    public void onResume() {
        if (!running_) {
            running_ = true;
            gameThread_ = new Thread(this); // Esto es el run
            gameThread_.start();
        }
    }

    /**
     * Si esta en ejecucion la pausa, y espera a que el hilo muera.
     */
    public void onPause() {
        if (running_) {
            running_ = false;
            while (true) {
                try {
                    gameThread_.join();
                    gameThread_ = null;
                    break;
                } catch (InterruptedException ie) { //No debería pasar.
                    ie.printStackTrace();
                }
            }
        }
    }

    /**
     * Calcula el deltaTime o el tiempo que ha pasado entre el frame actual y el anterior.
     */
    private void updateDeltaTime() {
        double currentTime = System.nanoTime();
        double nanoElapsedTime = currentTime - lastFrameTime_;
        lastFrameTime_ = currentTime;
        deltaTime_ = nanoElapsedTime / 1.0E9;
    }

    /**
     * Devuelve el tiempo que ha pasado entre el frame actual y el anterior. 0 en frame 1.
     */
    @Override
    public double getDeltaTime() { return deltaTime_; }

    /**
     * Inicializa y ejecuta el bucle principal de la aplicación, ejecutando la escena declarada.
     */
    @Override
    public void run() {
        //Solo el hilo debe ejecutar el juego
        if (gameThread_ != Thread.currentThread()) {
            throw new RuntimeException("run() should not be called directly");
        }
        while(running_ && !graphics_.init()); //Se espera a que se inicialicen los graficos
        lastFrameTime_ = System.nanoTime();
        Scene currApp = null;
        while(running_) { //Bucle principal

            //Se actualiza la aplicacion con la que se trabaja
            if(currApp != app_){
                currApp = app_;
                currApp.init(this);
            }

            updateDeltaTime();
            graphics_.lock(); //Se fija un canvas
            graphics_.setGamePosition();
            currApp.update();
            ((InputAndroid)input_).releaseEvents(); // Se liberan los eventos utilizados
            currApp.render(); //Se añaden elementos al canvas
            graphics_.unlock(); //Se desfija y renderiza
        }
    }

    /**
     * Asigna una nueva escena
     * El siguiente frame se empieza a actualizar y renderizar
     */
    @Override
    public void changeScene(Scene a) {
        app_ = a;
    }

    /**
     * Devuelve un puntero a la instancia del motor gráfico.
     */
    @Override
    public Graphics getGraphics() {
        return graphics_;
    }

    /**
     * Devuelve un puntero a la instancia del sistema de procesado de Input.
     */
    @Override
    public Input getInput() {
        return input_;
    }
}
