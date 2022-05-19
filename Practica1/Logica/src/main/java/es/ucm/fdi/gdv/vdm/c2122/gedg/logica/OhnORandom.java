package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.Random;

public class OhnORandom {
    public static Random r = new Random();

    static public boolean getRandomBoolean(float p) {
        return r.nextFloat() < p;
    }
}
