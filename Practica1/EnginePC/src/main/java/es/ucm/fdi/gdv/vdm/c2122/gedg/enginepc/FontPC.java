package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Color;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Font;

public class FontPC implements Font {

    int originalSize_; //Tamaño original de la fuente, NO modificado por el redimensionado de la pantalla
    private java.awt.Font font; //Fuente de Java
    private Color color_;

    public FontPC(String name, Color color, int size, boolean isBold) {
        color_ = color;
        originalSize_ = size;
        try {
            font = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, new File(name)).deriveFont((isBold) ? java.awt.Font.BOLD : java.awt.Font.PLAIN, size);
        }
        catch (IOException | FontFormatException e)
        {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
    }

    java.awt.Font getFont(){
        return font;
    }

    @Override
    //Modifica el tamaño de renderizado
    public void setRenderSize(int size) {
        if(font.getSize() == size) return;
        font = font.deriveFont((float) size);
    }

    @Override
    public void setColor(Color color) {
        color_ = color;
    }
    @Override
    public void setSize(int size) {
        originalSize_ = size;
        font = font.deriveFont((float) size);
    }
    @Override
    public void setBold(boolean isBold) {
        font = font.deriveFont((isBold) ? java.awt.Font.BOLD : java.awt.Font.PLAIN);
    }
    @Override
    public int getSize() {
        return font.getSize();
    }
    @Override
    public Color getColor() {
        return color_;
    }
}