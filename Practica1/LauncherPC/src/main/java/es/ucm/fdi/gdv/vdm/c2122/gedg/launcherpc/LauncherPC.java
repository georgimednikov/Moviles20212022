package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherpc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.EnginePC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.GraphicsPC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOIntro;

public class LauncherPC {
    public static void main(String[] args){

        EnginePC pc = new EnginePC();
        pc.init("0hn0");

        ((GraphicsPC)pc.getGraphics()).setWindowSize(400, 600);

        OhnOIntro g = new OhnOIntro();

        pc.changeState(g);
        pc.run();
    }
}