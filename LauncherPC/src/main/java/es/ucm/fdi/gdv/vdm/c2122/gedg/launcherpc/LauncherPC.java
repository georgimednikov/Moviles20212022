package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherpc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.EnginePC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnO;

public class LauncherPC {
    public static void main(String[] args){

        EnginePC pc = new EnginePC();

        int side = (int)Math.pow(args.length, 0.5f);
        char[][] mat = new char[side][side];
        for(int i = 0; i < args.length; ++i) {
            mat[i / side][i % side] = args[i].charAt(0);
        }
        OhnO g = new OhnO(5, mat);
        g.setEngine(pc);
        pc.setApplication(g);
        pc.run();
    }

}