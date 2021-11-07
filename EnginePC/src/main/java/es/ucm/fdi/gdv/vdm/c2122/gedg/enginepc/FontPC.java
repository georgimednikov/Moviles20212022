package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;



public class FontPC implements Font {

    private java.awt.Font font;
    public FontPC(String name, int size, boolean isBold) {
        font = new java.awt.Font(name, (isBold) ? java.awt.Font.BOLD : java.awt.Font.PLAIN, size);
    }

    @Override
    public void setColor(Color color) {
        
    }

    @Override
    public void setSize(int size) {
        font = font.deriveFont(size);
    }

    @Override
    public void setBold(boolean isBold) {
        font = font.deriveFont((isBold) ? java.awt.Font.BOLD : java.awt.Font.PLAIN);
    }

    @Override
    public Color getColor() {
        return new Color();
    }
    @Override
    public int getSize() {
        return font.getSize();
    }

    java.awt.Font getFont(){
        return font;
    }
}
