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

    private boolean fadeIn = true;
    private boolean fadeOut = false;
    private float sceneAlpha = 0f; //Alpha de la escena al hacer fade in/out
    private float fadeCurrentDuration = 0f; //Segundos que lleva haciendose un fade
    private int cellSeparation = -1; //Asignacion dinamica
    private int cellRadius = -1; //Asignacion dinamica
    private int selectedSize = -1;

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

    private boolean updateSceneFades() {
        if (fadeIn || fadeOut) {
            if (fadeCurrentDuration >= FADE_TOTAL_DURATION) {
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
                if (fadeIn) sceneAlpha = 1 - Math.min((fadeCurrentDuration / FADE_TOTAL_DURATION), 1);
                else if (fadeOut) sceneAlpha = Math.min((fadeCurrentDuration / FADE_TOTAL_DURATION), 1);
                return true;
            }
        }
        return false;
    }

    @Override
    public void update() {
        if (updateSceneFades()) return;

        TouchEvent event;
        next:
        while ((event = eng_.getInput().getEvent()) != null) {
            if (event.type != TouchEvent.TouchType.PRESS) continue; //TODO: ESTO NO DEBERIA SER ASI (?)
            if (checkCollisionCircle(eng_.getGraphics().getWidth() / 2, QUIT_POS_Y, BUTTON_SIZE, event.x, event.y)) {
                fadeOut = true;
                nextScene = POSSIBLE_SCENES.INTRO;
                continue;
            }
            int cont = STARTING_VALUE;
            for (int i = 0; i < ROWS; ++i) {
                for (int j = 0; j < ROW_SIZE; ++j) {
                    if (checkCollisionCircle(
                            MENU_OFFSET_X + cellRadius * (j + 1) + (cellRadius + cellSeparation) * j,
                            MENU_OFFSET_Y + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i,
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
        g.drawText(fonts.get("logoFont"), "Oh nO", g.getWidth() / 2, LOGO_POS_Y, true);
        g.drawText(fonts.get("textFont"), "Elija el tamaño a jugar", g.getWidth() / 2, TEXT_POS_Y, true);
        int cont = STARTING_VALUE;
        for (int i = 0; i < ROWS; ++i) {
            g.save();
            g.translate(MENU_OFFSET_X + cellRadius, MENU_OFFSET_Y + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < ROW_SIZE; ++j) {
                if ((i + j) % 2 == 0) g.setColor(colors.get("blue"));
                else g.setColor(colors.get("red"));
                g.fillCircle(0, 0, cellRadius);
                g.drawText(fonts.get("numberFont"), "" + cont++, 0, 0, true);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.drawImage(images.get("quitImage"), g.getWidth() / 2, QUIT_POS_Y, BUTTON_SIZE, BUTTON_SIZE, true);

        if (fadeIn ||fadeOut) {
            g.clear(new Color(255, 255, 255, (int)(255 * sceneAlpha)));
        }
    }
    @Override
    public boolean init() {
        Graphics g = eng_.getGraphics();

        int paintArea = g.getWidth() - 2 * MENU_OFFSET_X;
        cellRadius = (int)((paintArea * 0.9) / 2) / ROW_SIZE;
        cellSeparation = (int)(paintArea * 0.1) / (ROW_SIZE -1);

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
