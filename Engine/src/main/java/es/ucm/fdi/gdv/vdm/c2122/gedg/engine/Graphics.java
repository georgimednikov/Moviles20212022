package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Graphics {
    Image newImage(String name);
    Font newFont(String filename, Color color, int size);
    void clear(Color color);
    void drawImage(Image image, int x, int y, int width, int height);
    void setColor(Color color);
    void fillCircle(int cx, int cy, int r);
    void drawText(String text, int x, int y);
    int getWidth();
    int getHeight();
    void translate(int dx, int dy);
    void scale();
    void save();
    void restore();
}
