package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Graphics {
    Image newImage(String name);
    Font newFont(String filename, Color color, int size, boolean isBold);
    void clear(Color color);
    void drawImage(Image image, int x, int y, int width, int height, boolean centered);
    void setColor(Color color);
    void fillCircle(int cx, int cy, int r);
    void drawText(Font font, String text, int x, int y, boolean centered);
    int getWidth();
    int getHeight();
    int getTextWidth(Font font, String string);
    int getTextHeight(Font font, String string);
    int getPosX();
    int getPosY();
    void translate(int dx, int dy);
    void scale(float sx, float sy);
    void save();
    void restore();
}
