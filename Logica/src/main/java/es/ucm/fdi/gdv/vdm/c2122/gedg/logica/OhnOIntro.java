package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.ApplicationCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class OhnOIntro extends ApplicationCommon {

    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private float fadeCurrentDuration = 0f; //Segundos que lleva haciendose un fade
    private float fadeTotalDuration = 0.25f; //Segundos que duran los fades
    private float sceneAlpha = 0f; //Alpha de la escena al hacer fade in/out

    private int logoPosY = 130;
    private int playPosY = 250;
    private int firstCreditPosY = 380;
    private int secondCreditPosY = 415;
    private int imagePosY = 530;
    private int imageWidth = 40;
    private int imageHeight = 60;
    private String playText = "Jugar";

    //Render
    private Image q42Image;
    private Font logoFont;
    private Font playFont;
    private Font creditFont;

    public OhnOIntro() {}

    private boolean checkCollisionBox(int x, int y, int w, int h, int eventX, int eventY, boolean centered) {
        if (!centered) return (eventX >= x && eventX <= (x + w) &&
                                eventY >= y && eventY <= (y + h));
        return (eventX >= (x - w / 2) && eventX <= (x + w / 2) &&
                eventY >= (y - h / 2) && eventY <= (y + h / 2));
    }

    private boolean updateSceneFades() {
        if (fadeIn || fadeOut) {
            if (fadeCurrentDuration >= fadeTotalDuration) {
                fadeCurrentDuration = 0;
                if (fadeIn) fadeIn = false;
                else if (fadeOut) {
                    OhnOMenu app = new OhnOMenu();
                    eng_.setApplication(app);
                }
            }
            else {
                fadeCurrentDuration += eng_.getDeltaTime();
                if (fadeIn) sceneAlpha = 1 - Math.min((fadeCurrentDuration / fadeTotalDuration), 1);
                else if (fadeOut) sceneAlpha = Math.min((fadeCurrentDuration / fadeTotalDuration), 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if (updateSceneFades()) return;

        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        while (!events.isEmpty()) {
            event = events.remove(0);
            if (event.type != TouchEvent.TouchType.PRESS) continue; //TODO: ESTO NO DEBERIA SER ASI (?)
            if (checkCollisionBox(
                    eng_.getGraphics().getWidth() / 2,
                    playPosY,
                    eng_.getGraphics().getTextWidth(playFont, playText),
                    eng_.getGraphics().getTextHeight(playFont, playText),
                    event.x, event.y,true)) {
                fadeOut = true;
            }
        }
    }
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();
        g.clear(new Color(255, 255, 255, 255));
        g.drawText(logoFont, "Oh nO", g.getWidth() / 2, logoPosY, true);
        g.drawText(playFont, playText, g.getWidth() / 2, playPosY, true);
        g.drawText(creditFont, "Un juego copiado a Q42", g.getWidth() / 2, firstCreditPosY, true);
        g.drawText(creditFont, "Creado por Martin Kool", g.getWidth() / 2, secondCreditPosY, true);
        g.drawImage(q42Image, g.getWidth() / 2, imagePosY, imageWidth, imageHeight, true);

        if (fadeIn ||fadeOut) {
            g.clear(new Color(255, 255, 255, (int)(255 * sceneAlpha)));
        }
    }
    @Override
    public boolean init() {
        Graphics g = eng_.getGraphics();
        logoFont = g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false);
        playFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 60, false);
        creditFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(150, 150, 150, 255), 25, false);
        q42Image = g.newImage("assets/sprites/q42.png");
        return true;
    }
    @Override
    public boolean close() {
        return true;
    }
}
