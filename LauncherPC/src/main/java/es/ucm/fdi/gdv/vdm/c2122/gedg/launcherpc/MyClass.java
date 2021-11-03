package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherpc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.Cell;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.Game;

public class MyClass {
    public static void main(String[] args){
        int side = (int)Math.pow(args.length, 0.5f);
        char[][] mat = new char[side][side];
        for(int i = 0; i < args.length; ++i) {
            mat[i / side][i % side] = args[i].charAt(0);
        }
        Game g = new Game(5, mat);
        int a = 0;
        while(true){
            ++a;
        }
    }

}