package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

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
        return new FontPC(filename, color, size, isBold);
    }

    @Override
    public void clear(Color color) {
        java.awt.Color c = g_.getColor();
        setColor(color);
        g_.fillRect(0,0, getWidth(), getHeight());
        g_.setColor(c);
    }

    @Override
    public void drawImage(Image image, int x, int y, int width, int height, boolean centered) {
        ImagePC img = (ImagePC) image;
        img.setPos(x, y);
        int verticalOffset, horizontalOffset; verticalOffset = horizontalOffset = 0;
        if (centered) {
            verticalOffset = height / 2;
            horizontalOffset = width / 2;
        }
        g_.drawImage(img.getImage(), x - horizontalOffset, y - verticalOffset, width, height, null);
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
    public void drawText(Font font, String text, int x, int y, boolean centered) {
        FontPC f = (FontPC) font;
        g_.setFont(f.getFont());
        setColor(f.getColor());
        if (!centered) {
            g_.drawString(text, x, y);
            return;
        }
        //Stack Overflow me susurró que hiciera esto
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = f.getFont().getStringBounds(text, frc);
        int rWidth = (int)Math.round(r2D.getWidth());
        int rHeight = (int)Math.round(r2D.getHeight());
        int tX = (int)Math.round(r2D.getX());
        int tY = (int)Math.round(r2D.getY());
        int a = (int)(x - (rWidth / 2) - tX);
        int b = (int)(y - (rHeight / 2) - tY);
        g_.drawString(text, a, b);
    }

    public void setGraphics(Graphics g){
        g_ = g;
        translate(curPosX, curPosY);
        g_.setClip(0, 0, curSizeX, curSizeY); // Ya se ha trasladado, su 0, 0 esta movido ya
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
    public int getTextWidth(Font font, String text) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = ((FontPC)font).getFont().getStringBounds(text, frc);
        return (int)Math.round(r2D.getWidth());
    }

    @Override
    public int getTextHeight(Font font, String text) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = ((FontPC)font).getFont().getStringBounds(text, frc);
        return (int)Math.round(r2D.getHeight());
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
