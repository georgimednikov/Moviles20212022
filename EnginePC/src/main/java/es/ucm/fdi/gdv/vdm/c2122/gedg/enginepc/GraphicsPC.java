package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;
import javax.swing.JFrame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

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
    public Font newFont(String filename, int size, boolean isBold) {
        return new FontPC(filename, size, isBold);
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
        g_.fillOval(cx - r , cy - r , 2*r, 2*r);
    }

    @Override
    public void drawText(Font font, String text, int x, int y, boolean centered) {
        FontPC f = (FontPC) font;
        g_.setFont(f.getFont());
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        int verticalOffset, horizontalOffset; verticalOffset = horizontalOffset = 0;
        if (centered) {
            verticalOffset = (int)(f.getFont().getStringBounds(text, frc).getHeight()) / 2;
            horizontalOffset = (int)(f.getFont().getStringBounds(text, frc).getWidth()) / 2;
        }
        g_.drawString(text, x - horizontalOffset, y - verticalOffset);
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
