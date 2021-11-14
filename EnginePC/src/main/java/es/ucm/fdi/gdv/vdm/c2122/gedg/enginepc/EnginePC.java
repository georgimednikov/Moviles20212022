package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.ApplicationCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;

public class EnginePC implements Engine {

    JFrame jf_;
    Application a_;
    GraphicsPC g_;
    Input i_;
    java.awt.Graphics Jgraphics_;
    BufferStrategy strategy_;
    boolean running;
    private double deltaTime_;
    private double lastFrameTime_;

    public EnginePC(){
        init();
    }

    @Override
    public boolean init() {
        jf_ = new JFrame("0hn0");
        jf_.setSize(600,400);
        jf_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf_.setIgnoreRepaint(true);
        jf_.setVisible(true);

        //Se intenta crear una strategy, si no se puede algo va bastante mal
        int intentos = 100;
        while(intentos-- > 0) {
            try {
                jf_.createBufferStrategy(2);
                break;
            }
            catch(Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        strategy_ = jf_.getBufferStrategy();
        g_ = new GraphicsPC(jf_);
        i_ = new InputPC(g_);

        running = true;

        return true;
    }

    private void updateDeltaTime() {
        double currentTime = System.nanoTime();
        double nanoElapsedTime = currentTime - lastFrameTime_;
        lastFrameTime_ = currentTime;
        deltaTime_ = nanoElapsedTime / 1.0E9;
    }

    @Override
    public double getDeltaTime() { return deltaTime_; }
    @Override
    public Graphics getGraphics() {
        return g_;
    }
    @Override
    public Input getInput() {
        return i_;
    }

    /**
     * Asigna una nueva aplicacion y la inicializa
     * El siguiente frame se empieza a actualizar y renderizar
     */
    @Override
    public void setApplication(Application a) {
        this.a_ = a;
        ((ApplicationCommon)a).setEngine(this);
        a_.init();
    }

    /**
     * Ejecuta el bucle principal de la aplicacion
     */
    @Override
    public void run() {
        while(running){
            lastFrameTime_ = System.nanoTime();
            Application currApp = a_; //Se actualiza la aplicacion con la que se trabaja
            currApp.update();
            do {
                do {
                    //Se consigue la estrategia de renderizado y la aplicacion le dice que dibujar
                    Jgraphics_ = strategy_.getDrawGraphics();
                    g_.setGraphics(Jgraphics_);
                    try {
                        currApp.render();
                    }
                    finally {
                        Jgraphics_.dispose();
                    }
                } while(strategy_.contentsRestored());
                strategy_.show(); //Se renderiza
            } while(strategy_.contentsLost());
            updateDeltaTime();
        }
    }
}
