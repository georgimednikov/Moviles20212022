package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.image.BufferStrategy;

import javax.swing.JFrame;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
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

    public EnginePC(){
        init();
    }

    @Override
    public boolean init() {
        jf_ = new JFrame("Dolor");
        jf_.setSize(600,400);
        jf_.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf_.setIgnoreRepaint(true);
        jf_.setVisible(true);

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

    @Override
    public Graphics getGraphics() {
        return g_;
    }
    @Override
    public Input getInput() {
        return i_;
    }
    @Override
    public void setApplication(Application a) {
        this.a_ = a;
        a_.init();
    }
    @Override
    public boolean close(){
        running = false;
        return true;
    }
    @Override
    public void run() {
        while(running){
            a_.update();
            do {
                do {
                    Jgraphics_ = strategy_.getDrawGraphics();
                    g_.setGraphics(Jgraphics_);

                    try {
                        a_.render();
                    }
                    finally {
                        Jgraphics_.dispose();
                    }
                } while(strategy_.contentsRestored());
                strategy_.show();
            } while(strategy_.contentsLost());
        }
        a_.close();
    }
}
