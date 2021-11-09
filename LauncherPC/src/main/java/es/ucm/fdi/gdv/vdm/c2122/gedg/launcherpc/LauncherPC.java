package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherpc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.EnginePC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOIntro;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOLevel;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOMenu;

public class LauncherPC {
    public static void main(String[] args){

        EnginePC pc = new EnginePC();

        pc.getGraphics().scale(400, 600);

        // DEBUG
        OhnOIntro g = new OhnOIntro();
        // !DEBUG

        pc.setApplication(g);
        g.setEngine(pc);
        pc.run();
    }
}