package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Scene;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;

/**
 * Clase que representa un Motor en la plataforma de PC. Procesa Inputs, crea y dibuja en pantalla y actualiza sus elementos.
 */
public class EnginePC implements Engine {

    //Instancia del sistema de renderizado.
    JFrame jf_;
    java.awt.Graphics Jgraphics_;
    BufferStrategy strategy_;

    Scene a_; //Escena actual que reproduce el motor.
    GraphicsPC g_; //Motor gráfico.
    Input i_; //Sistema de procesado de inputs.
    boolean running; //Si el motor está listo para ejecutar el bucle principal.
    private double deltaTime_;
    private double lastFrameTime_;

    public boolean init(String windowName) {
        jf_ = new JFrame(windowName);
        jf_.setSize(600,400); //Tamaño por defecto se puede cambiar con setWindowSize del motor gráfico.
        jf_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf_.setIgnoreRepaint(true);
        jf_.setVisible(true);

        //Se intenta crear una strategy, si no se puede algo va bastante mal.
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
     * Devuelve un puntero a la instancia del motor gráfico.
     */
    @Override
    public Graphics getGraphics() {
        return g_;
    }

    /**
     * Devuelve un puntero a la instancia del sistema de procesado de Input.
     */
    @Override
    public Input getInput() {
        return i_;
    }

    /**
     * Asigna una nueva escena
     * El siguiente frame se empieza a actualizar y renderizar
     */
    @Override
    public void changeScene(Scene a) {
        this.a_ = a;
    }

    /**
     * Ejecuta el bucle principal de la aplicacion, ejecutando la escena declarada.
     */
    public void run() {
        Scene currApp = null;
        while(running){
            lastFrameTime_ = System.nanoTime();

            //Se actualiza la aplicacion con la que se trabaja
            if(currApp != a_){
                currApp = a_;
                currApp.init(this);
            }

            currApp.update();
            ((InputPC)i_).releaseEvents();
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
