package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;



public class FontPC extends java.awt.Font implements Font {

    public FontPC(String name, int size) {
        super(name, PLAIN, size);
    }

    @Override
    public void setColor(Color color) {
        
    }

    @Override
    public void setSize(int size) {

    }

    @Override
    public void setBold(boolean isBold) {

    }

    @Override
    public Color getColor() {
        return new Color();
    }
    @Override
    public int getSize() {
        return super.getSize();
    }
}
