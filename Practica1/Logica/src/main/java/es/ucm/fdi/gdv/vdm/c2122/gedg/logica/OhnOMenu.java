package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.State;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOMenu implements State {

    Engine eng_;

    //Escenas a las que puede transicionar esta
    private enum POSSIBLE_SCENES {
        INTRO,
        MENU
    }
    private POSSIBLE_SCENES nextScene;

    //Constantes de renderizado
    private final float SCENE_FADE_DURATION = 0.25f; //Segundos que duran los fades
    private final int ROWS = 2;
    private final int ROW_SIZE = 3;
    private final int STARTING_VALUE = 4; //Tamaño mínimo de los tableros.
    private final int LOGO_POS_Y = 130;
    private final int TEXT_POS_Y = 215;
    private final int QUIT_POS_Y = 530;
    private final int BUTTON_SIZE = 40;
    private final int MENU_OFFSET_X = 60;
    private final int MENU_OFFSET_Y = 270;

    //Variables de asignacion dinamica
    private int cellSeparation = -1;
    private int cellRadius = -1;
    private int selectedSize = -1;

    //Variables relacionadas con las animaciones
    private boolean fadeOut = false;
    private float elapsedTime = 0f;

    //Render
    private Color blue;
    private Color red;
    private Font numberFont;
    private TextRender logoText;
    private TextRender textText;
    private ImageRenderer quitImage;

    //Tablero de renderizado de palo. Se quiere tener CellRenderers pero no se quiere un BoardRenderer como tal,
    //ya que el menú se crea de forma irregular, con un patrón de colores fijo...
    private CellRenderer[][] menu = new CellRenderer[ROWS][ROW_SIZE];
    private List<ObjectRenderer> objects = new ArrayList<>();

    public OhnOMenu() {}

    /**
     * Inicializa la escena para su actualizacion/renderizado.
     */
    @Override
    public void init(Engine eng) {
        eng_ = eng;
        Graphics g = eng_.getGraphics();

        //Área en la que se van a dibujar los elementos de la interfaz.
        int paintArea = g.getWidth() - 2 * MENU_OFFSET_X;
        cellRadius = (int)((paintArea * 0.9) / 2) / ROW_SIZE;
        cellSeparation = (int)(paintArea * 0.1) / (ROW_SIZE -1);

        blue = new Color(72, 193, 228, 255);
        red = new Color(245, 53, 73, 255);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(255, 255, 255, 255), 75, false);
        logoText = new TextRender(g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false), "Oh nO", true);
        textText = new TextRender(g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 30, false), "Elija el tamaño a jugar", true);
        quitImage = new ImageRenderer(g.newImage("assets/sprites/close.png"), BUTTON_SIZE, BUTTON_SIZE, true);
        objects.add(logoText);
        objects.add(textText);
        objects.add(quitImage);

        int cont = STARTING_VALUE; //Número que representa el tamaño de los tableros. Empieza con el valor mínimo.
        // En cada fila se pone tantos elementos como el tamaño de fila, alternando colores.
        for (int i = 0; i < ROWS; ++i) {
            for (int j = 0; j < ROW_SIZE; ++j) {
                if ((i + j) % 2 == 0){
                    menu[i][j] = new CellRenderer(cellRadius, true);
                    menu[i][j].setState(Cell.STATE.BLUE);
                }
                else{
                    menu[i][j] = new CellRenderer(cellRadius, true);
                    menu[i][j].setState(Cell.STATE.RED);
                }
                objects.add(menu[i][j]);
                menu[i][j].setTypeNumber(numberFont, ""+cont);
                cont++;
            }
        }
        for (int i = 0; i < objects.size(); ++i) objects.get(i).fadeIn(SCENE_FADE_DURATION);
    }
    /**
     * Actualiza la escena. Actualiza los tiempos de las animaciones y procesa inputs
     */
    @Override
    public void update() {
        //Se actualizan las entidades con animaciones
        if (updateScene(eng_.getDeltaTime())) return;

        //Se procesan los eventos
        TouchEvent event;
        next:
        while ((event = eng_.getInput().dequeueEvent()) != null) {
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            //Se mira si ha activado el boton
            if (checkCollisionCircle(eng_.getGraphics().getWidth() / 2, QUIT_POS_Y, BUTTON_SIZE, event.x, event.y)) {
                fadeOut = true;
                for (int i = 0; i < objects.size(); ++i) objects.get(i).fadeOut(SCENE_FADE_DURATION);
                nextScene = POSSIBLE_SCENES.INTRO;
                continue;
            }
            //Se mira si ha activado una celda y en ese caso de que numero
            int cont = STARTING_VALUE;
            for (int i = 0; i < ROWS; ++i) {
                for (int j = 0; j < ROW_SIZE; ++j) {
                    if (checkCollisionCircle(
                            MENU_OFFSET_X + cellRadius * (j + 1) + (cellRadius + cellSeparation) * j,
                            MENU_OFFSET_Y + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i,
                            cellRadius, event.x, event.y)) {
                        fadeOut = true; //Flag de transicion
                        for (int k = 0; k < objects.size(); ++k) objects.get(k).fadeOut(SCENE_FADE_DURATION);
                        nextScene = POSSIBLE_SCENES.MENU;
                        selectedSize = cont;
                        continue next;
                    }
                    cont++;
                }
            }
        }
    }
    /**
     * Renderiza la escena
     */
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();

        g.save();
        g.translate(g.getWidth() / 2, LOGO_POS_Y);
        logoText.render(g);
        g.translate(0, TEXT_POS_Y - LOGO_POS_Y);
        textText.render(g);
        g.restore();

        for (int i = 0; i < ROWS; ++i) {
            g.save();
            g.translate(MENU_OFFSET_X + cellRadius, MENU_OFFSET_Y + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < ROW_SIZE; ++j) {
                menu[i][j].render(g);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.save();
        g.translate(g.getWidth() / 2, QUIT_POS_Y);
        quitImage.render(g);
        g.restore();
    }

    /**
     * Actualiza todo lo relacionado con las animaciones de la escena
     * @return True si no se deben procesar inputs porque se esta realizando una animacion, false en caso contrario
     */
    private boolean updateScene(double deltaTime) {
        updateRenders(deltaTime);
        if (fadeOut) {
            if (elapsedTime >= SCENE_FADE_DURATION) {
                switch (nextScene) {
                    case INTRO:
                        OhnOIntro intro = new OhnOIntro();
                        eng_.changeState(intro);
                        break;
                    case MENU:
                        OhnOLevel level = new OhnOLevel(selectedSize);
                        eng_.changeState(level);
                        break;
                }
            }
            else elapsedTime += deltaTime;
        }
        return false;
    }
    /**
     * Actualiza los renderers
     */
    private void updateRenders(double deltaTime) {
        for (int i = 0; i < objects.size(); ++i)
            objects.get(i).updateRenderer(deltaTime);
    }

    /**
     * Comprueba si un punto se encuentra dentro de una circunferencia
     * @param centerX Centro de la circunferencia
     * @param centerY Centro de la circunferencia
     * @param radius Radio de la circunferencia
     * @param eventX Posicion X del punto
     * @param eventY Posicion Y del punto
     * @return true si esta dentro
     */
    private boolean checkCollisionCircle(int centerX, int centerY, int radius, int eventX, int eventY) {
        int vecX = Math.abs(centerX - eventX);
        int vecY = Math.abs(centerY - eventY);
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }
}
