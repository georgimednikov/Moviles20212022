package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.ApplicationCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;


public class OhnOMenu extends ApplicationCommon {

    private enum POSSIBLE_SCENES {
        INTRO,
        MENU
    }
    private POSSIBLE_SCENES nextScene;

    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private float fadeCurrentDuration = 0f; //Segundos que lleva haciendose un fade
    private float fadeTotalDuration = 0.25f; //Segundos que duran los fades
    private float sceneAlpha = 0f; //Alpha de la escena al hacer fade in/out

    private int rows = 2;
    private int sizePerRow = 3;
    private int firstSize = 4;
    private int selectedSize = -1;

    private int menuOffsetX = 60;
    private int menuOffsetY = 270;
    private int cellSeparation = -1; //Asignacion dinamica
    private int cellRadius = -1; //Asignacion dinamica

    private int logoPosY = 130;
    private int textPosY = 215;
    private int quitPosY = 530;
    private int buttonSize = 40;

    //Render
    private Map<String, Color> colors = new HashMap<>();
    private Map<String, Font> fonts = new HashMap<>();
    private Map<String, Image> images = new HashMap<>();

    public OhnOMenu() {}

    private boolean checkCollisionCircle(int centerX, int centerY, int radius, int eventX, int eventY) {
        int vecX = Math.abs(centerX - eventX);
        int vecY = Math.abs(centerY - eventY);
        return (Math.pow(vecX, 2) + Math.pow(vecY, 2) <= Math.pow(radius, 2));
    }

    private boolean updateFades() {
        if (fadeIn || fadeOut) {
            if (fadeCurrentDuration >= fadeTotalDuration) {
                fadeCurrentDuration = 0;
                if (fadeIn) fadeIn = false;
                else if (fadeOut) {
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
        if (updateFades()) return;

        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        next:
        while (!events.isEmpty()) {
            event = events.remove(0);
            if (event.type != TouchEvent.TouchType.PRESS) continue; //TODO: ESTO NO DEBERIA SER ASI (?)
            if (checkCollisionCircle(eng_.getGraphics().getWidth() / 2, quitPosY, buttonSize, event.x, event.y)) {
                fadeOut = true;
                nextScene = POSSIBLE_SCENES.INTRO;
                continue;
            }
            int cont = firstSize;
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < sizePerRow; ++j) {
                    if (checkCollisionCircle(
                            menuOffsetX + cellRadius * (j + 1) + (cellRadius + cellSeparation) * j,
                            menuOffsetY + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i,
                            cellRadius, event.x, event.y)) {
                        fadeOut = true;
                        nextScene = POSSIBLE_SCENES.MENU;
                        selectedSize = cont;
                        continue next;
                    }
                    cont++;
                }
            }
        }
    }
    @Override
    public void render() {
        Graphics g = eng_.getGraphics();
        g.drawText(fonts.get("logoFont"), "Oh nO", g.getWidth() / 2, logoPosY, true);
        g.drawText(fonts.get("textFont"), "Elija el tamaño a jugar", g.getWidth() / 2, textPosY, true);
        int cont = firstSize;
        for (int i = 0; i < rows; ++i) {
            g.save();
            g.translate(menuOffsetX + cellRadius, menuOffsetY + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < sizePerRow; ++j) {
                if ((i + j) % 2 == 0) g.setColor(colors.get("blue"));
                else g.setColor(colors.get("red"));
                g.fillCircle(0, 0, cellRadius);
                g.drawText(fonts.get("numberFont"), "" + cont++, 0, 0, true);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.drawImage(images.get("quitImage"), g.getWidth() / 2, quitPosY, buttonSize, buttonSize, true);

        if (fadeIn ||fadeOut) {
            g.clear(new Color(255, 255, 255, (int)(255 * sceneAlpha)));
        }
    }
    @Override
    public boolean init() {
        Graphics g = eng_.getGraphics();

        int paintArea = g.getWidth() - 2 * menuOffsetX;
        cellRadius = (int)((paintArea * 0.9) / 2) / sizePerRow;
        cellSeparation = (int)(paintArea * 0.1) / (sizePerRow-1);

        colors.put("blue", new Color(72, 193, 228, 255));
        colors.put("red", new Color(245, 53, 73, 255));
        fonts.put("logoFont", g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false));
        fonts.put("textFont", g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 30, false));
        fonts.put("numberFont", g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(255, 255, 255, 255), 75, false));
        images.put("quitImage", g.newImage("assets/sprites/close.png"));
        return true;
    }
    @Override
    public boolean close() {
        return true;
    }
}
