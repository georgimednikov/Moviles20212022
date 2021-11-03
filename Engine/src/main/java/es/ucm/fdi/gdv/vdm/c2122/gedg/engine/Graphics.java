package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Graphics {
    Image newImage(String name);
    Font newFont(String filename, int size, boolean isBold);
    void clear(int color);
    void drawImage(Image image, int x, int y, int width, int height);
    void setColor(int color);
    void fillCircle(int cx, int cy, int r);
    void drawText(String text, int x, int y);
    int getWidth();
    int getHeight();
    void translate();
    void scale();
    void save();
    void restore();
}
