package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.Random;

/**
 * Clase estática de generadores de random. De esta manera no hay que tener varios randoms en el proyecto.
 */
public class OhnORandom {
    public static Random r = new Random();

    /**
     * Devuelve un booleano aleatorio con la probabilidad dada.
     */
    static public boolean getRandomBoolean(float p) {
        return r.nextFloat() < p;
    }
}
