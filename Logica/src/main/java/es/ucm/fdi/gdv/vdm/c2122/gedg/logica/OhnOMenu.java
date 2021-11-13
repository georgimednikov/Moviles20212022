package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.HashMap;
import java.util.Map;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.ApplicationCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOMenu extends ApplicationCommon {

    //Escenas a las que puede transicionar esta
    private enum POSSIBLE_SCENES {
        INTRO,
        MENU
    }
    private POSSIBLE_SCENES nextScene;

    //Constantes de renderizado
    private final float FADE_TOTAL_DURATION = 0.25f; //Segundos que duran los fades
    private final int ROWS = 2;
    private final int ROW_SIZE = 3;
    private final int STARTING_VALUE = 4;
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
    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private float sceneAlpha = 0f;
    private float fadeCurrentDuration = 0f;

    //Render
    private Color blue;
    private Color red;
    private Font logoFont;
    private Font textFont;
    private Font numberFont;
    private Image quitImage;

    public OhnOMenu() {}

    /**
     * Inicializa la escena para su actualizacion/renderizado
     */
    @Override
    public void init() {
        Graphics g = eng_.getGraphics();
        int paintArea = g.getWidth() - 2 * MENU_OFFSET_X;
        cellRadius = (int)((paintArea * 0.9) / 2) / ROW_SIZE;
        cellSeparation = (int)(paintArea * 0.1) / (ROW_SIZE -1);

        blue = new Color(72, 193, 228, 255);
        red = new Color(245, 53, 73, 255);
        logoFont = g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false);
        textFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 30, false);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(255, 255, 255, 255), 75, false);
        quitImage = g.newImage("assets/sprites/close.png");
    }
    /**
     * Actualiza la escena. Actualiza los tiempos de las animaciones y procesa inputs
     */
    @Override
    public void update() {
        if (updateScene()) return;

        //Se procesan los eventos
        TouchEvent event;
        next:
        while ((event = eng_.getInput().getEvent()) != null) {
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            //Se mira si ha activado el boton
            if (checkCollisionCircle(eng_.getGraphics().getWidth() / 2, QUIT_POS_Y, BUTTON_SIZE, event.x, event.y)) {
                fadeOut = true;
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
        g.drawText(logoFont, "Oh nO", g.getWidth() / 2, LOGO_POS_Y, true);
        g.drawText(textFont, "Elija el tamaño a jugar", g.getWidth() / 2, TEXT_POS_Y, true);
        int cont = STARTING_VALUE;
        //Se dibujan las celdas
        //No hay celdas logicas asi que no hay celdas de renderizado
        //Se usa save y restore por comodidad, ya que simplifica la logica del renderizado de celdas
        for (int i = 0; i < ROWS; ++i) {
            g.save();
            g.translate(MENU_OFFSET_X + cellRadius, MENU_OFFSET_Y + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < ROW_SIZE; ++j) {
                //Se alterna el color
                if ((i + j) % 2 == 0) g.setColor(blue);
                else g.setColor(red);

                g.fillCircle(0, 0, cellRadius);
                g.drawText(numberFont, "" + cont++, 0, 0, true);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.drawImage(quitImage, g.getWidth() / 2, QUIT_POS_Y, BUTTON_SIZE, BUTTON_SIZE, true,1);

        //Realiza las animaciones si hay
        if (fadeIn ||fadeOut) {
            //Para hacer los fades se pinta por encima de la escena de blanco con un alpha que varia
            g.clear(new Color(255, 255, 255, (int)(255 * sceneAlpha)));
        }
    }

    /**
     * Actualiza todo lo relacionado con las animaciones de la escena
     * @return True si no se deben procesar inputs porque se esta realizando una animacion, false en caso contrario
     */
    private boolean updateScene() {
        if (fadeIn || fadeOut) { //Si se esta haciendo una animacion
            if (fadeCurrentDuration >= FADE_TOTAL_DURATION) { //Si se ha acabado
                fadeCurrentDuration = 0;
                if (fadeIn) fadeIn = false; //Si estaba apareciendo, se acabo la animacion
                else if (fadeOut) { //Si estaba desapareciendo se le dice al motor que cambie de escena
                    switch (nextScene) {
                        case INTRO:
                            OhnOIntro intro = new OhnOIntro();
                            eng_.setApplication(intro);
                            break;
                        case MENU:
                            OhnOLevel level = new OhnOLevel(selectedSize);
                            eng_.setApplication(level);
                            break;
                    }
                }
            }
            else { //Avanza el tiempo y el alpha de la escena
                fadeCurrentDuration += eng_.getDeltaTime();
                if (fadeIn) sceneAlpha = 1 - Math.min((fadeCurrentDuration / FADE_TOTAL_DURATION), 1);
                else if (fadeOut) sceneAlpha = Math.min((fadeCurrentDuration / FADE_TOTAL_DURATION), 1);
                return true;
            }
        }
        return false;
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
