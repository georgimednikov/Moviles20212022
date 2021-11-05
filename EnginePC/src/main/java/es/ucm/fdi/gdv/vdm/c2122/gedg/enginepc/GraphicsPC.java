package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;
import java.awt.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsPC implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics  {

    private JFrame jf;
    private Graphics g;
    private Graphics save;
    GraphicsPC(JFrame jf){
        while(jf.getBufferStrategy() == null) {}
        g = jf.getBufferStrategy().getDrawGraphics();
    }

    @Override
    public Image newImage(String name) {
        return new ImagePC();
    }

    @Override
    public Font newFont(String filename, Color color, int size, boolean isBold) {
        return new FontPC(filename, size, isBold);
    }


    @Override
    public void clear(Color color) {
        setColor(color);
        g.fillRect(0,0, getWidth(), getHeight());
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height) {

    }

    @Override
    public void setColor(Color color) {
    g.setColor(new java.awt.Color(color.r, color.g, color.b, color.a ));
    }

    @Override
    public void fillCircle(int cx, int cy, int r) {
        g.fillOval(cx - r , cy - r , 2*r, 2*r);
    }

    @Override
    public void drawText(Font font, String text, int x, int y) {

    }

    @Override
    public int getWidth() {
        return jf.getWidth();
    }

    @Override
    public int getHeight() {
        return jf.getHeight();
    }

    @Override
    public void translate(int dx, int dy) {
        g.translate(dx, dy);
    }

    @Override
    public void scale(float sx, float sy) {

    }

    @Override
    public void save() {
        save = g.create();
    }

    @Override
    public void restore() {
        g = save.create();
    }
}
