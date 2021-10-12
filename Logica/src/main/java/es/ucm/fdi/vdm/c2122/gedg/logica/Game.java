package es.ucm.fdi.vdm.c2122.gedg.logica;
import java.util.Random;

public class Game {
    private final float blueProb = 0.7f; //Porbabilidad de que una celda sea azul en vez de roja en la solución
    private final float fixedProb = 0.5f; //Probabilidad de que una celda sea fija

    int contMistakes = 0; //Número de celdas mal puestas
    private Cell[][] mat;

    static private Random rand = new Random(System.currentTimeMillis());
    static private boolean getRandomBoolean(float p){
        assert p > 1.0f && p < 0.0f: String.format("getRandomBoolean recibe un número entre 0 y 1: (%d)", p);
        return rand.nextFloat() < p;
    }

    //Crea la matriz que representa el nivel de un tamaño dado
    private void createBoard(int size) {
        mat = new Cell[size][size];
        //Primero crea un nivel aleatorio
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (getRandomBoolean(blueProb)) mat[i][j] = new Cell(Cell.STATE.BLUE);
                else mat[i][j] = new Cell(Cell.STATE.RED);
            }
        }
        //Después fija ciertas celdas, cuenta sus adyacentes si pertinente y desmarca las demás
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if(getRandomBoolean(fixedProb)) {
                    if(mat[i][j].getSolState() == Cell.STATE.RED) mat[i][j].fixCell();
                    else mat[i][j].fixCell(calculateNumber(mat, i, j));
                }
                else {
                    mat[i][j].setGrey();
                    contMistakes++;
                }
            }
        }
    }

    //Cuenta las celdas azules adyacentes a una dada
    private int calculateNumber(Cell[][] mat, int x, int y) {
        int count = 0;
        int i = 1;
        while(inArray(x - i, y, mat) && mat[x-i][y].getSolState() != Cell.STATE.RED){ i++; count++; }
        i = 1;
        while(inArray(x + i, y, mat) && mat[x+i][y].getSolState() != Cell.STATE.RED){ i++; count++; }
        i = 1;
        while(inArray(x, y - i, mat) && mat[x][y-i].getSolState() != Cell.STATE.RED){ i++; count++; }
        i = 1;
        while(inArray(x, y + i, mat) && mat[x][y+i].getSolState() != Cell.STATE.RED){ i++; count++; }
        return count;
    }

    //Comprueba que una posición no se sale del array
    private boolean inArray(int x, int y, Cell[][] array) {
        return ((x >= 0 && x < array.length) && (y >= 0 &&  y < array[0].length));
    }

    public Game(int size) {
        createBoard(size);
    }

    //Cambia el estado de una celda. Si ha resuelto el nivel, devuelve true
    public boolean changeCell(int i, int j) {
        if (mat[i][j].changeState()) {
            contMistakes--;
            if (contMistakes == 0) return true;
        }
        return false;
    }
}
