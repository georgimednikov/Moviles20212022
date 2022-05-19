package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.State;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOIntro implements State {

    Engine eng_;

    //Constantes de renderizado
    private final int LOGO_POS_Y = 130;
    private final int PLAY_POS_Y = 250;
    private final int FIRST_CREDIT_POS_Y = 380;
    private final int SECOND_CREDIT_POS_Y = 415;
    private final int IMAGE_POS_Y = 530;
    private final int IMAGE_WIDTH = 40;
    private final int IMAGE_HEIGHT = 60;
    private final float SCENE_FADE_DURATION = 0.25f; //Segundos que duran los fades

    //Variables de animacion
    private boolean fadeOut = false;
    private float elapsedTime = 0f; //Segundos que lleva haciendose un fade

    //Render
    private ImageRenderer q42Image;
    private TextRender logoText;
    private TextRender playText;
    private TextRender creditText1;
    private TextRender creditText2;
    private List<ObjectRenderer> objects = new ArrayList<>();

    public OhnOIntro() {}

    /**
     * Inicializa la escena para su actualizacion/renderizado
     */
    @Override
    public void init(Engine eng) {
        eng_ = eng;
        Graphics g = eng_.getGraphics();
        logoText = new TextRender(g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false),"Oh nO", true);
        playText = new TextRender(g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 60, false), "Jugar", true);
        creditText1 = new TextRender(g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(150, 150, 150, 255), 25, false), "Un juego copiado a Q42", true);
        creditText2 = new TextRender(g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(150, 150, 150, 255), 25, false), "Creado por Martin Kool", true);
        q42Image = new ImageRenderer(g.newImage("assets/sprites/q42.png"), IMAGE_WIDTH, IMAGE_HEIGHT, true);
        objects.add(logoText);
        objects.add(playText);
        objects.add(creditText1);
        objects.add(creditText2);
        objects.add(q42Image);

        //Se le dice a todos los objetos renderizables que aparezcan progresivamente al iniciar la escena.
        for (int i = 0; i < objects.size(); ++i) objects.get(i).fadeIn(SCENE_FADE_DURATION);
    }

    /**
     * Actualiza la escena. Actualiza los tiempos de las animaciones y procesa inputs
     */
    @Override
    public void update() {
        //Se actualizan las entidades con animaciones
        if (updateScene(eng_.getDeltaTime())) return;

        //Se procesan los eventos de input
        TouchEvent event;
        while ((event = eng_.getInput().dequeueEvent()) != null) {
            if (event.type != TouchEvent.TouchType.PRESS) continue;
            //Si se ha hecho click en el texto
            if (checkCollisionBox(
                    eng_.getGraphics().getWidth() / 2,
                    PLAY_POS_Y,
                    eng_.getGraphics().getTextWidth(playText.getFont(), playText.getText()),
                    eng_.getGraphics().getTextHeight(playText.getFont(), playText.getText()),
                    event.x, event.y,true)) {
                fadeOut = true; //Flag de transicion
                for (int j = 0; j < objects.size(); ++j) objects.get(j).fadeOut(SCENE_FADE_DURATION);
            }
        }
    }
    /**
     * Renderiza la escena.
     */
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();

        g.save();
        g.translate(g.getWidth() / 2, LOGO_POS_Y);
        logoText.render(g);
        g.translate(0, PLAY_POS_Y - LOGO_POS_Y);
        playText.render(g);
        g.translate(0, FIRST_CREDIT_POS_Y - PLAY_POS_Y);
        creditText1.render(g);
        g.translate(0, SECOND_CREDIT_POS_Y - FIRST_CREDIT_POS_Y);
        creditText2.render(g);
        g.restore();

        g.save();
        g.translate(g.getWidth() / 2, IMAGE_POS_Y);
        q42Image.render(g);
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
                OhnOMenu app = new OhnOMenu();
                eng_.changeState(app);
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
