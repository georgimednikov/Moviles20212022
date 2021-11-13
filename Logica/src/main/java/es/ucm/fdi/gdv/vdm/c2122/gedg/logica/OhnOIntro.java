package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.ApplicationCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOIntro extends ApplicationCommon {

    //Constantes de renderizado
    private final int LOGO_POS_Y = 130;
    private final int PLAY_POS_Y = 250;
    private final int FIRST_CREDIT_POS_Y = 380;
    private final int SECOND_CREDIT_POS_Y = 415;
    private final int IMAGE_POS_Y = 530;
    private final int IMAGE_WIDTH = 40;
    private final int IMAGE_HEIGHT = 60;
    private final float FADE_TOTAL_DURATION = 0.25f; //Segundos que duran los fades

    //Variables de animacion
    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private float fadeCurrentDuration = 0f; //Segundos que lleva haciendose un fade
    private float sceneAlpha = 0f; //Alpha de la escena al hacer fade in/out

    //Render
    private Image q42Image;
    private Font logoFont;
    private Font playFont;
    private Font creditFont;
    private String playText = "Jugar"; //Variable del texto que sirve de boton. Se usa en varios sitios, es mas comodo asi

    public OhnOIntro() {}

    /**
     * Inicializa la escena para su actualizacion/renderizado
     */
    @Override
    public void init() {
        Graphics g = eng_.getGraphics();
        logoFont = g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false);
        playFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 60, false);
        creditFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(150, 150, 150, 255), 25, false);
        q42Image = g.newImage("assets/sprites/q42.png");
    }

    /**
     * Actualiza la escena. Actualiza los tiempos de las animaciones y procesa inputs
     */
    @Override
    public void update() {
        if (updateScene()) return;

        //Se procesan los eventos de input
        TouchEvent event;
        while ((event = eng_.getInput().getEvent()) != null) {
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            //Si se ha hecho click en el texto
            if (checkCollisionBox(
                    eng_.getGraphics().getWidth() / 2,
                    PLAY_POS_Y,
                    eng_.getGraphics().getTextWidth(playFont, playText),
                    eng_.getGraphics().getTextHeight(playFont, playText),
                    event.x, event.y,true)) {
                fadeOut = true; //Flag de transicion
            }
        }
    }
    /**
     * Renderiza la escena
     */
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();

        //Se dibujan textos/imagenes
        //No parece apropiado usar save/restore porque no se usan clases de renderizado
        g.drawText(logoFont, "Oh nO", g.getWidth() / 2, LOGO_POS_Y, true);
        g.drawText(playFont, playText, g.getWidth() / 2, PLAY_POS_Y, true);
        g.drawText(creditFont, "Un juego copiado a Q42", g.getWidth() / 2, FIRST_CREDIT_POS_Y, true);
        g.drawText(creditFont, "Creado por Martin Kool", g.getWidth() / 2, SECOND_CREDIT_POS_Y, true);
        g.drawImage(q42Image, g.getWidth() / 2, IMAGE_POS_Y, IMAGE_WIDTH, IMAGE_HEIGHT, true);

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
                    OhnOMenu app = new OhnOMenu();
                    eng_.setApplication(app);
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
     * Detecta si un punto se encuentra en un rectangulo dado
     * @param x Posicion X del rectangulo
     * @param y Posicion Y del rectangulo
     * @param w Ancho
     * @param h Alto
     * @param eventX Posicion X del punto
     * @param eventY Posicion Y del punto
     * @param centered Si se mira la posicion del rectangulo desde su centro o la esquina superior izquierda
     * @return true si esta dentro
     */
    private boolean checkCollisionBox(int x, int y, int w, int h, int eventX, int eventY, boolean centered) {
        if (!centered) return (eventX >= x && eventX <= (x + w) &&
                eventY >= y && eventY <= (y + h));
        return (eventX >= (x - w / 2) && eventX <= (x + w / 2) &&
                eventY >= (y - h / 2) && eventY <= (y + h / 2));
    }
}
