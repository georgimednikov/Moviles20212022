package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import javax.lang.model.type.NullType;

public class Hint {
    public enum HintType {

        //Pistas de verdad
        VISIBLE_CELLS_COVERED, // Tiene el numero correcto de celdas, hay que cerrarla
        CANNOT_SURPASS_LIMIT, // Si pusieramos un azul, nos pasariamos del numero
        MUST_PLACE_BLUE, // Si no pusieramos un azul, no seria posible llegar al numero

        //Para los jugadores de Nautilus
        TOO_MANY_ADJACENT, // El numero de celdas es superior al numero
        NOT_ENOUGH_BUT_CLOSED, // La casilla esta cerrada pero no tiene el numero correcto

        //Pistas para casillas no fijas
        BLUE_BUT_ISOLATED, // La celda es azul pero esta cerrada
        ISOLATED_AND_EMPTY, // La celda es gris pero esta cerrada

        //Pistas adicionales que por supuesto vamos a hacer
        //MONODIRECTIONAL_INCOMPLETE,
        //JUST_FILL_ALL,
        //NOT_ENOUGH_BLUES
    }

    public static String[] hintText = {
            "VISIBLE_CELLS_COVERED",
            "CANNOT_SURPASS_LIMIT",
            "MUST_PLACE_BLUE",
            "TOO_MANY_ADJACENT",
            "NOT_ENOUGH_BUT_CLOSED",
            "BLUE_BUT_ISOLATED",
            "ISOLATED_AND_EMPTY"
    };

    public HintType type;
    public int x = -1, y = -1;
}

