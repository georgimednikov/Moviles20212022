package es.ucm.fdi.vdm.c2122.gedg.logica;
import java.util.Random;

import static es.ucm.fdi.vdm.c2122.gedg.logica.Hint.HintType.MUST_PLACE_BLUE;


public class Game {
    private final float blueProb = 0.7f; //Porbabilidad de que una celda sea azul en vez de roja en la solución
    private final float fixedProb = 0.5f; //Probabilidad de que una celda sea fija

    int contMistakes = 0; //Número de celdas mal puestas
    private Cell[][] mat;
    private boolean solved = false;

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
        //Despues fija ciertas celdas, cuenta sus adyacentes si pertinente y desmarca las demas
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if(getRandomBoolean(fixedProb)) {
                    if(mat[i][j].getSolState() == Cell.STATE.RED) mat[i][j].fixCell();
                    else mat[i][j].fixCell(calculateNumber(i, j));
                }
                else {
                    mat[i][j].setGrey();
                    contMistakes++;
                }
            }
        }
    }

    public int[] nextDiffColor(int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while(mat[x + dx * i][y + dy *i].getSolState() == color) {
            if (!inArray(x + dx * i, y + dy *i, mat)) {
                int[] res = { -1, -1 }; return res;
            }
            i++;
        }
        int[] res = { x + dx * i, y + dy *i };
        return res;
    }

    public int[] nextSameColor(int x, int y, int dx, int dy, Cell.STATE color) {
        int i = 1;
        while(mat[x + dx * i][y + dy *i].getSolState() != color) {
            if (!inArray(x + dx * i, y + dy *i, mat)) {
                int[] res = { -1, -1 }; return res;
            }
            i++;
        }
        int[] res = { x + dx * i, y + dy *i };
        return res;
    }

    //Cuenta las celdas azules adyacentes a una dada
    private int calculateNumber(int x, int y) {
        int count = 0;
        int[] newPos;
        newPos = nextDiffColor(x, y, 1, 0, Cell.STATE.BLUE); count += newPos[0] - x;
        newPos = nextDiffColor(x, y, 0, 1, Cell.STATE.BLUE); count += newPos[1] - y;
        newPos = nextDiffColor(x, y, -1, 0, Cell.STATE.BLUE); count += x - newPos[0];
        newPos = nextDiffColor(x, y, 0, -1, Cell.STATE.BLUE); count += y - newPos[1];
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
        if (!mat[i][j].isFixed()) {
            if (mat[i][j].isRight()) {
                contMistakes++;
            }
            if (mat[i][j].changeState()) {
                contMistakes--;
                if (contMistakes == 0) solved = true;
            }
            return true;
        }
        return false;
    }

    private boolean hint_VISIBLE_CELLS_COVERED(Hint hint, int x, int y, int i, int j, int cont) {
        if (!mat[x][y].isFixed() || cont != mat[x][y].getNumber()) return false;
        int[] newPos = nextDiffColor(x, y, i, j, Cell.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.GREY) {
            hint.type_ = Hint.HintType.VISIBLE_CELLS_COVERED;
            hint.x_ = x;
            hint.y_ = y;
            return true;
        }
        return false;
    }

    private boolean hint_CANNOT_SURPASS_LIMIT(Hint hint, int x, int y, int i, int j, int cont) {
        if (!mat[x][y].isFixed() || cont >= mat[x][y].getNumber()) return false;
        int[] newPos = nextDiffColor(x, y, i, j, Cell.STATE.BLUE);
        if (mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.GREY) {
            int[] newNewPos = nextDiffColor(newPos[0], newPos[1], i, j, Cell.STATE.BLUE);
            if (newNewPos[0] < 0) return false;
            int newCont = Math.abs(newNewPos[0] - newPos[0]) + Math.abs(newNewPos[1] - newPos[1]);
            if(cont + newCont > mat[x][y].getNumber()){
                hint.type_ = Hint.HintType.CANNOT_SURPASS_LIMIT;
                hint.x_ = newPos[0];
                hint.y_ = newPos[1];
                return true;
            }
        }
        return false;
    }

    private boolean hint_MUST_PLACE_BLUE(Hint hint, int x, int y, int i, int j, int cont) {
        int[] newPos = nextDiffColor(x, y, i, j, Cell.STATE.BLUE);
        if(cont == mat[x][y].getNumber() || newPos[0] < 0 || mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.RED) return false;
        int newCont = 0;
        for(int k = -1; k <= 1; ++k) {
            for (int l = -1; l <= 1; ++l) {
                if ((k + l == 2) || (k + l == 0) || (k + l == -2)) continue;
                if(i == k && j == l) continue;
                int[] nextNoGrey = nextSameColor(x, y, k, l, Cell.STATE.GREY);
                if(nextNoGrey[0] < 0) continue;
                // -1 porque nextSameColor te deja en la casilla que no es del color
                newCont += Math.abs(nextNoGrey[0] - 1 - newPos[0]) + Math.abs(nextNoGrey[1] - 1 - newPos[1]);
            }
        }
        if(newCont + cont < mat[x][y].getNumber()){
            hint.x_ = x+i;
            hint.y_ = y+j;
            hint.type_ = MUST_PLACE_BLUE;
            return true;
        }
        return false;
    }

    public Hint giveHint() {
        Hint hint = new Hint();
        outer:
        for(int i = 0; i < mat.length; ++i){
            for(int j = 0; j < mat[0].length; ++j){
                if (getHint(hint, i, j)) break outer;
            }
        }
        if (hint.x_ < 0 || hint.y_ < 0) return null;
        return hint;
    }

    private boolean getHint(Hint hint, int x, int y) {
        int curCount = calculateNumber(x, y);
        if (!mat[x][y].isFixed() || curCount >= mat[x][y].getNumber()) return false;
        int[] newPos;
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                return hint_VISIBLE_CELLS_COVERED(hint, x, y, i, j, curCount) ||
                        hint_CANNOT_SURPASS_LIMIT(hint, x, y, i, j, curCount) ||
                        hint_MUST_PLACE_BLUE(hint, x, y, i, j, curCount);

            }
        }
        return false;
    }

    /*private boolean hint_VISIBLE_CELLS_COVERED(Hint hint, int x, int y) {
        if (!mat[x][y].isFixed()) return false;
        int[] newPos;
        int cont = 0;
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue; // Idea de Eloy Cortijo Moreno
                newPos = nextDiffColor(x, y, i, j, Cell.STATE.BLUE);
                cont += Math.abs(newPos[0] - x) + Math.abs(newPos[1] - y);
                if (mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.GREY) {
                    hint.type_ = Hint.HintType.VISIBLE_CELLS_COVERED;
                    hint.x_ = x;
                    hint.y_ = y;
                    return true;
                }
            }
        }
        return cont == mat[x][y].getNumber();
    }

    private boolean hint_CANNOT_SURPASS_LIMIT(Hint hint, int x, int y) {
        int curCount = calculateNumber(x, y);
        if (!mat[x][y].isFixed() || curCount >= mat[x][y].getNumber()) return false;
        int[] newPos;
        for(int i = -1; i <= 1; ++i) {
            for (int j = -1; j <= 1; ++j) {
                if ((i + j == 2) || (i + j == 0) || (i + j == -2)) continue;
                newPos = nextDiffColor(x, y, i, j, Cell.STATE.BLUE);
                if (mat[newPos[0]][newPos[1]].getCurrState() == Cell.STATE.GREY) {
                    int[] newnewPos = nextDiffColor(newPos[0], newPos[1], i, j, Cell.STATE.BLUE);
                    int newcont = Math.abs(newnewPos[0] - newPos[0]) + Math.abs(newnewPos[1] - newPos[1]);
                    if(curCount + newcont > mat[x][y].getNumber()){
                        hint.type_ = Hint.HintType.CANNOT_SURPASS_LIMIT;
                        hint.x_ = newPos[0];
                        hint.y_ = newPos[1];
                        return true;
                    }
                }
            }
        }
        return false;
    }*/
}
