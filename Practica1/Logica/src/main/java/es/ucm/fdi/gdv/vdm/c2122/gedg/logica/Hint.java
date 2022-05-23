package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

public class Hint {
    //IDs de las pistas
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
        NONE
    }

    //Textos para cada pista posible
    public static String[] hintText = {
            //                                   |
            "Ve suficientes celdas azules",
            "Superaría el número de una celda",
            "Sin esta no vería suficientes azules",
            "Ve demasiadas celdas azules",
            "No ve suficientes azules",
            "Es azul sin azules adyacentes",
            "Celda gris rodeada de rojas"
    };

    public Hint(HintType t, int x, int y){
        this.type = t;
        this.x = x;
        this.y = y;
    }

    public HintType type;
    public int x = -1, y = -1;
}

