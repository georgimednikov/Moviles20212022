package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherpc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.EnginePC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnO;

public class LauncherPC {
    public static void main(String[] args){

        EnginePC pc = new EnginePC();

        // DEBUG
        // Lee una matriz de los argumentos
        int side = (int)Math.pow(args.length, 0.5f);
        char[][] mat = new char[side][side];
        for(int i = 0; i < args.length; ++i) {
            mat[i / side][i % side] = args[i].charAt(0);
        }
        // !DEBUG

        pc.getGraphics().scale(400, 600);

        // DEBUG
        OhnO g = new OhnO(5, mat);
        // !DEBUG

        //OhnO g = new OhnO(5);

        g.setEngine(pc);
        pc.setApplication(g);
        pc.run();
    }
}