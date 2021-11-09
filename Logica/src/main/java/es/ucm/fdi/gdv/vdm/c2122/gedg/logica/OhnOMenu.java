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

    private int menuOffsetX = 60;
    private int menuOffsetY = 270;
    private int cellSeparation = -1; //Asignacion dinamica
    private int cellRadius = -1; //Asignacion dinamica

    private int logoPosY = 130;
    private int textPosY = 215;
    private int quitPosY = 530;
    private int buttonSize = 40;

    //Render
    private Font logoFont;
    private Font textFont;
    private Font numberFont;
    private Image quitImage;
    private Color white = new Color(255, 255, 255, 255);
    private Color blue = new Color(72, 193, 228, 255);
    private Color red = new Color(245, 53, 73, 255);

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

        int paintArea = g.getWidth() - 2 * menuOffsetX;
        cellRadius = (int)((paintArea * 0.9) / 2) / sizePerRow;
        cellSeparation = (int)(paintArea * 0.1) / (sizePerRow-1);

        logoFont = g.newFont("assets/fonts/Molle-Regular.ttf", new Color(0, 0, 0, 255), 85, false);
        textFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(0, 0, 0, 255), 30, false);
        numberFont = g.newFont("assets/fonts/JosefinSans-Bold.ttf", new Color(255, 255, 255, 255), 75, false);
        quitImage = g.newImage("assets/sprites/close.png");
    }
    @Override
    public void update() {
        TouchEvent event;
        List<TouchEvent> events = eng_.getInput().getTouchEvents();
        next:
        while (!events.isEmpty()) {
            event = events.remove(0);
            if (event.type != TouchEvent.TouchType.PRESS) continue; //TODO: ESTO NO DEBERIA SER ASI (?)
            if (checkCollisionCircle(eng_.getGraphics().getWidth() / 2, quitPosY, buttonSize, event.x, event.y)) {
                OhnOIntro app = new OhnOIntro();
                eng_.setApplication(app);
                app.setEngine(eng_);
                continue;
            }
            int cont = firstSize;
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < sizePerRow; ++j) {
                    if (checkCollisionCircle(
                            menuOffsetX + cellRadius * (j + 1) + (cellRadius + cellSeparation) * j,
                            menuOffsetY + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i,
                            cellRadius, event.x, event.y)) {
                        OhnOLevel app = new OhnOLevel(cont);
                        eng_.setApplication(app);
                        app.setEngine(eng_);
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
        g.clear(white);
        g.drawText(logoFont, "Oh nO", g.getWidth() / 2, logoPosY, true);
        g.drawText(textFont, "Elija el tamaño a jugar", g.getWidth() / 2, textPosY, true);
        int cont = firstSize;
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < sizePerRow; ++j) {
                g.setColor(new Color(0, 255, 0, 255));
                g.fillCircle(
                        menuOffsetX + cellRadius * (j + 1) + (cellRadius + cellSeparation) * j,
                        menuOffsetY + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i,
                        cellRadius + 3);
            }
        }
        for (int i = 0; i < rows; ++i) {
            g.save();
            g.translate(menuOffsetX + cellRadius, menuOffsetY + cellRadius * (i + 1) + (cellRadius + cellSeparation) * i);
            for (int j = 0; j < sizePerRow; ++j) {
                if ((i + j) % 2 == 0) g.setColor(blue);
                else g.setColor(red);
                g.fillCircle(0, 0, cellRadius);
                g.drawText(numberFont, "" + cont++, 0, 0, true);
                g.translate(cellRadius * 2 + cellSeparation, 0);
            }
            g.restore();
        }
        g.drawImage(quitImage, g.getWidth() / 2, quitPosY, buttonSize, buttonSize, true);
    }
    @Override
    public boolean init() {
        return true;
    }
    @Override
    public boolean close() {
        return true;
    }
}
