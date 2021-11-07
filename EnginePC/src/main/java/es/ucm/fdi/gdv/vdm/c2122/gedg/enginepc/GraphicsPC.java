package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Graphics;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsPC extends GraphicsCommon {

    private JFrame jf_;
    private Graphics g_;
    private Graphics save_;
    GraphicsPC(JFrame jf){
        jf_ = jf;
        while(jf.getBufferStrategy() == null) {}
        g_ = jf.getBufferStrategy().getDrawGraphics();
    }

    @Override
    public Image newImage(String name) {
        return new ImagePC(name);
    }

    @Override
    public Font newFont(String filename, Color color, int size, boolean isBold) {
        return new FontPC(filename, size, isBold);
    }


    @Override
    public void clear(Color color) {
        setColor(color);
        g_.fillRect(0,0, getWidth(), getHeight());
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height) {
        image.setSize(width,height);
        g_.drawImage(((ImagePC)image).getImage(), x, y, width, height, ((ImagePC)image).getObserver());
    }

    @Override
    public void setColor(Color color) {
    g_.setColor(new java.awt.Color(color.r, color.g, color.b, color.a ));
    }

    @Override
    public void fillCircle(int cx, int cy, int r) {
        g_.fillOval(cx - r , cy - r , 2*r, 2*r);
    }

    @Override
    public void drawText(Font font, String text, int x, int y) {
        g_.setFont(((FontPC)font).getFont());
        g_.drawString(text, x, y);
    }

    @Override
    public int getWidth() {
        return jf_.getWidth();
    }

    @Override
    public int getHeight() {
        return jf_.getHeight();
    }

    @Override
    public void translate(int dx, int dy) {
        g_.translate(dx, dy);
    }

    @Override
    public void scale(float sx, float sy) {
        super.scale((int)sx, (int)sy);
        translate(curPosX, curPosY);
        jf_.setSize((int)sx, (int)sy);
        jf_.getContentPane().setPreferredSize(new Dimension((int)sx, (int)sy));
    }

    @Override
    public void save() {
        save_ = g_.create();
    }

    @Override
    public void restore() {
        g_ = save_.create();
    }
}
