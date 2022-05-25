package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

public class Hint {
    //IDs de las pistas
    public enum HintType {
        //Pistas de verdad
        VISIBLE_CELLS_COVERED("Ve suficientes celdas azules"), // Tiene el numero correcto de celdas, hay que cerrarla
        CANNOT_SURPASS_LIMIT("Una dirección superaría el límite"), // Si pusieramos un azul, nos pasariamos del numero
        MUST_PLACE_BLUE("Hay una común en todos los casos"), // Si no pusieramos un azul, no seria posible llegar al numero

        //Para los jugadores de Nautilus
        TOO_MANY_ADJACENT("Ve demasiadas celdas azules"), // El numero de celdas es superior al numero
        NOT_ENOUGH_BUT_CLOSED("No ve suficientes azules"), // La casilla esta cerrada pero no tiene el numero correcto

        //Pistas para casillas no fijas
        BLUE_BUT_ISOLATED("Un azul tiene que ver otro azul"), // La celda es azul pero esta cerrada
        ISOLATED_AND_EMPTY("Esta debería ser fácil..."), // La celda es gris pero esta cerrada
        NONE("");

        public String text;

        HintType(String t) {
            text = t;
        }
    }

    public Hint(HintType t, int x, int y){
        this.type = t;
        this.x = x;
        this.y = y;
    }

    public HintType type;
    public int x = -1, y = -1;
}

