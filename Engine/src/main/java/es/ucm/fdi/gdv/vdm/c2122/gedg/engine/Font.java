package es.ucm.fdi.gdv.vdm.c2122.gedg.engine;

import java.io.File;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;

public interface Font {
    void setColor(Color color);
    void setSize(int size);
    void setBold(boolean isBold);
    Color getColor();
    int	getSize();
}
