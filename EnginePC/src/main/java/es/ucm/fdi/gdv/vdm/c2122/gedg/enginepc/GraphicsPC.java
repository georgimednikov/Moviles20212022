package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

public class GraphicsPC extends GraphicsCommon implements ComponentListener {

    private JFrame jf_;
    private Graphics g_;
    private Graphics save_;
    GraphicsPC(JFrame jf){
        jf_ = jf;
        BufferStrategy strategy = jf.getBufferStrategy();
        while(strategy == null) { strategy = jf.getBufferStrategy(); }
        g_ = strategy.getDrawGraphics();
        jf.addComponentListener(this);
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
        g_.drawImage(((ImagePC)image).getImage(), x, y, width, height, null);
    }

    @Override
    public void setColor(Color color) {
    g_.setColor(new java.awt.Color(color.r, color.g, color.b, color.a ));
    }

    @Override
    public void fillCircle(int cx, int cy, int r) {
        //scale(curSizeX, curSizeY);
        g_.fillOval(cx - r , cy - r , 2*r, 2*r);
    }

    @Override
    public void drawText(Font font, String text, int x, int y) {
        g_.setFont(((FontPC)font).getFont());
        g_.drawString(text, x, y);
    }

    public void setGraphics(Graphics g){
        g_ = g;
        translate(curPosX, curPosY);
        g_.setClip(curPosX, curPosY, curSizeX, curSizeY);
    }

    @Override
    public int getWidth() {
        return curSizeX;
    }

    @Override
    public int getHeight() {
        return curSizeY;
    }

    @Override
    public int getPosX() {
        return curPosX - jf_.getX();
    }

    @Override
    public int getPosY() {
        return curPosY - jf_.getY();
    }

    @Override
    public void translate(int dx, int dy) {
        g_.translate(dx, dy);
    }

    @Override
    public void scale(float sx, float sy) {
        super.scale((int)sx, (int)sy);
        jf_.setSize((int)sx, (int)sy);
    }

    @Override
    public void save() {
        save_ = g_.create();
    }

    @Override
    public void restore() {
        g_ = save_.create();
    }

    @Override
    public void componentResized(ComponentEvent componentEvent) {
        Dimension dim = componentEvent.getComponent().getSize();
        scale(dim.width, dim.height);
    }

    @Override
    public void componentMoved(ComponentEvent componentEvent) {

    }

    @Override
    public void componentShown(ComponentEvent componentEvent) {

    }

    @Override
    public void componentHidden(ComponentEvent componentEvent) {

    }
}
