package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Application;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Engine;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;


public class OhnOMenu implements Application {

    Engine eng_;
    private int rows = 2;
    private int sizePerRow = 3;
    private int firstSize = 4;

    private int menuOffsetX = 0;
    private int menuOffsetY = 0;
    private int cellSeparation = 0;
    private int cellRadius = 0;

    private int logoPosY = 0;
    private int textPosY = 0;
    private int quitPosY = 0;
    private int buttonSize = 0;

    //Render
    Font logoFont;
    Font textFont;
    Font numberFont;
    Image quitImage;

    Color blue = new Color(0, 0, 255, 255);
    Color red = new Color(255, 0, 0, 255);

    public OhnOMenu() {
    }

    private boolean checkCollisionCircle(int centerX, int centerY, int radius, int eventX, int eventY) {
        int vecX = Math.abs(centerX - eventX);
        int vecY = Math.abs(centerY - eventY);
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }

    @Override
    public void setEngine(Engine eng) {
        this.eng_ = eng;
        Graphics g = eng_.getGraphics();
        logoFont = g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 75, false);
        textFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 40, false);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(255, 255, 255, 255), 75, false);
        quitImage = g.newImage("assets/sprites/close.png");
    }
    @Override
    public void update() {
        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        while (!events.isEmpty()) {
            event = events.remove(0);
            if (event.type != TouchEvent.TouchType.PRESS) continue; //TODO: ESTO NO DEBERIA SER ASI (?)
            if (checkCollisionCircle(eng_.getGraphics().getWidth() / 2, quitPosY, buttonSize, event.x, event.y)) {
                eng_.setApplication(new OhnO(0, null));
                continue;
            }
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < sizePerRow; ++j) {
                    if (checkCollisionCircle(menuOffsetX + cellRadius * (j + 1) + cellSeparation * j, menuOffsetY + cellRadius * (i + 1) + cellSeparation * i, cellRadius, event.x, event.y)) {
                        eng_.setApplication(new OhnOLevel(firstSize + i + j));
                        continue; //TODO: NO TIENE QUE PASAR EN EL FOR TIENE QUE PASAR EN EL WHILE
                    }
                }
            }
        }
    }
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();
        //g.clear(new Color(50, 0, 200, 0));
        g.drawText(logoFont, "Oh nO", g.getWidth() / 2, logoPosY, true);
        g.drawText(textFont, "Elija el tamaño a jugar", g.getWidth() / 2, textPosY, true);
        int posX, posY;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < sizePerRow; ++j) {
                if ((i + j) % 2 == 0) g.setColor(blue);
                else g.setColor(red);
                posX = menuOffsetX + cellRadius * (j + 1) + cellSeparation * j;
                posY = menuOffsetY + cellRadius * (i + 1) + cellSeparation * i;
                g.fillCircle(posX, posY, cellRadius);
                g.drawText(numberFont, "" + (firstSize + i + j), posX, posY, true);
            }
        }
        g.drawImage(quitImage, g.getWidth() / 2, quitPosY, buttonSize, buttonSize, true);
    }
    @Override
    public boolean init() { return true; }
    @Override
    public boolean close() {
        return true;
    }
}
