package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;
import java.awt.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsPC extends JFrame implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics  {

    private Graphics g;
    GraphicsPC(String title){
        super(title);
        g = getBufferStrategy().getDrawGraphics();
    }

    @Override
    public Image newImage(String name) {
        return null;
    }

    @Override
    public Font newFont(String filename, int size, boolean isBold) {
        return null;
    }

    @Override
    public void clear(int color) {
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height) {

    }


    @Override
    public void setColor(int color) {

    }

    @Override
    public void fillCircle(int cx, int cy, int r) {
    }

    @Override
    public void drawText(String text, int x, int y) {

    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public void translate() {

    }

    @Override
    public void scale() {

    }

    @Override
    public void save() {

    }

    @Override
    public void restore() {

    }
}
