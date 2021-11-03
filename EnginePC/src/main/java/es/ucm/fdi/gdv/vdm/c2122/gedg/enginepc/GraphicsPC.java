package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;
import java.awt.Graphics;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsPC implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics  {

    private JFrame jf;
    private Graphics g;
    private Graphics save;
    GraphicsPC(String title){
        jf = new JFrame(title);
        g = jf.getBufferStrategy().getDrawGraphics();
    }

    @Override
    public Image newImage(String name) {
        return new ImagePC();
    }

    @Override
    public Font newFont(String filename, int size) {
        return new FontPC(filename, size);
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
        g.drawOval(cx - r, cy - r, r, r);
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
    public void translate(int dx, int dy) {
        g.translate(dx, dy);
    }

    @Override
    public void scale() {

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
