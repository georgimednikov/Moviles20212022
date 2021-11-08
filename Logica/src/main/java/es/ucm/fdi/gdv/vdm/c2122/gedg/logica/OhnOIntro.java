package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;


public class OhnOIntro implements Application {

    Engine eng_;

    private int logoPosY = 0;
    private int playPosY = 0;
    private int firstCreditPosY = 0;
    private int secondCreditPosY = 0;
    private int imagePosY = 0;
    private int imageWidth = 0;
    private int imageHeight = 0;
    private String playText = "Jugar";

    //Render
    Image q42Image;
    Font logoFont;
    Font playFont;
    Font creditFont;

    public OhnOIntro() {
    }

    private boolean checkCollisionBox(int x, int y, int w, int h, int eventX, int eventY, boolean centered) {
        if (!centered) return (eventX >= x && eventX <= (x + w) &&
                                eventY >= y && eventY <= (y + h));
        return (eventX >= (x - w / 2) && eventX <= (x + w / 2) &&
                eventY >= (y - h / 2) && eventY <= (y + h / 2));
    }

    @Override
    public void setEngine(Engine eng) {
        this.eng_ = eng;
        Graphics g = eng_.getGraphics();
        logoFont = g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 75, false);
        playFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 60, false);
        creditFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(150, 150, 150, 255), 30, false);
        q42Image = g.newImage("assets/sprites/q42.png");
    }
    @Override
    public void update() {
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
                    event.x, event.y,true))
                eng_.setApplication(new OhnOMenu());
        }
    }
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();
        //g.clear(new Color(50, 0, 200, 0));
        g.drawText(logoFont, "Oh nO", g.getWidth() / 2, logoPosY, true);
        g.drawText(playFont, playText, g.getWidth() / 2, playPosY, true);
        g.drawText(creditFont, "Un juego copiado a Q42", g.getWidth() / 2, firstCreditPosY, true);
        g.drawText(creditFont, "Creado por Martin Kool", g.getWidth() / 2, secondCreditPosY, true);
        g.drawImage(q42Image, g.getWidth() / 2, imagePosY, imageWidth, imageHeight, true);
    }
    @Override
    public boolean init() { return true; }
    @Override
    public boolean close() {
        return true;
    }
}
