package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

public interface Font {
    void setColor(Color color);
    void setRenderSize(int size);
    void setSize(int size);
    void setBold(boolean isBold);
    int	getSize();
    Color getColor();
}
