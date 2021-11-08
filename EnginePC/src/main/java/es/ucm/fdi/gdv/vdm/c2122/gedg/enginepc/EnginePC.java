package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.Color;

import javax.swing.JFrame;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;

public class EnginePC implements Engine {

    JFrame jf_;
    Application a_;
    GraphicsPC g_;
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
            }
        }
        g_ = new GraphicsPC(jf_);
        running = true;
        return true;
    }

    @Override
    public Graphics getGraphics() {
        return g_;
    }
    @Override
    public Input getInput() {
        return null;
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
                    java.awt.Graphics graphics = jf_.getBufferStrategy().getDrawGraphics();

                    try {
                        a_.render();
                    }
                    finally {
                        graphics.dispose();
                    }
                } while(jf_.getBufferStrategy().contentsRestored());
                jf_.getBufferStrategy().show();
            } while(jf_.getBufferStrategy().contentsLost());
        }
        a_.close();
    }
}
