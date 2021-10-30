package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import javax.lang.model.type.NullType;

public class Hint {
    public enum HintType {

        //Pistas de verdad
        VISIBLE_CELLS_COVERED,
        CANNOT_SURPASS_LIMIT,
        MUST_PLACE_BLUE,

        //Para los jugadores de Nautilus
        TOO_MANY_ADJACENT,
        NOT_ENOUGH_BUT_CLOSED,

        //Pistas para casillas no fijas
        BLUE_BUT_ISOLATED,
        ISOLATED_AND_EMPTY,

        //Pistas adicionales que por supuesto vamos a hacer
        //MONODIRECTIONAL_INCOMPLETE,
        //JUST_FILL_ALL,
        //NOT_ENOUGH_BLUES
    }

    public HintType type_;
    public int x_ = -1, y_ = -1;
}

