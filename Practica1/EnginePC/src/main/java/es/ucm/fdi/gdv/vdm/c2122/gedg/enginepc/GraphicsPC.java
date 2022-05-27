package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import javax.swing.JFrame;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferStrategy;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Stack;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.*;

/**
 * Motor gráfico del proyecto para la plataforma de PC.
 */
public class GraphicsPC extends GraphicsCommon implements ComponentListener {

    private JFrame jf_; //El sistema de renderizado.
    private Graphics g_; //El canvas de JFrame.
    private Stack<Graphics> saves_; //Estados guardados del canvas.

    GraphicsPC(JFrame jf){
        jf_ = jf;
        BufferStrategy strategy = jf.getBufferStrategy();
        while(strategy == null) { strategy = jf.getBufferStrategy(); }
        g_ = strategy.getDrawGraphics();
        jf_.addComponentListener(this);
        saves_ = new Stack<>();
    }

    /**
     * Devuelve un puntero a la instancia del sistema de renderizado del motor gráfico.
     */
    public JFrame getJFrame() {
        return jf_;
    }

    /**
     * Crea una imagen a partir de un path de archivo
     */
    @Override
    public Image newImage(String name) {
        return new ImagePC(name);
    }

    /**
     * Crea una fuente de texto a partir de un path de archivo
     * @param filename path
     * @param color color
     * @param size tamaño
     * @param isBold si es negrita
     */
    @Override
    public Font newFont(String filename, Color color, int size, boolean isBold) {
        return new FontPC(filename, color, size, isBold);
    }

    /**
     * Se limpia el render buffer pintando por encima del color dado cubriendo toda la pantalla
     */
    @Override
    public void clear(Color color) {
        java.awt.Color c = g_.getColor();
        setColor(color);
        g_.fillRect(0,0, jf_.getWidth(), jf_.getHeight());
        g_.setColor(c);
    }

    /**
     * Dibuja la imagen que reciba con los parametros dados
     * @param image Imagen
     * @param x Posicion X
     * @param y Posicion Y
     * @param width Ancho
     * @param height Alto
     * @param centered Se dibuja en base a su centro a su esquina superior izquierda
     */
    @Override
    public void drawImage(Image image, int x, int y, int width, int height, boolean centered, float opacity) {
        //Se ajustan los valores a las dimensiones reales del canvas
        x = (int) (toPhysicalX(x));
        y = (int) (toPhysicalY(y));
        width = (int) (toPhysicalX(width));
        height = (int) (toPhysicalY(height));
        ImagePC img = (ImagePC) image;

        //Se añaden offsets a la imagen para centrarla si se ha pedido.
        int verticalOffset, horizontalOffset; verticalOffset = horizontalOffset = 0;
        if (centered) {
            verticalOffset = height / 2;
            horizontalOffset = width / 2;
        }
        ((Graphics2D) g_).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity)); //Cambia la opacidad a la pedida.
        g_.drawImage(img.getImage(), x - horizontalOffset, y - verticalOffset, width, height, null);
        ((Graphics2D) g_).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1)); //Se restaura el valor por defecto de la opacidad.
    }

    /**
     * Fija el color con el que se va a pintar los siguientes elementos.
     */
    @Override
    public void setColor(Color color) {
        g_.setColor(new java.awt.Color(color.r, color.g, color.b, color.a ));
    }

    /**
     * Dibuja un circulo del color previamente establecido.
     * @param cx Posicion X del centro
     * @param cy Posicion Y del centro
     * @param r Radio
     */
    @Override
    public void fillCircle(int cx, int cy, int r) {
        //Se ajustan los valores a las dimensiones reales del canvas
        cx = (int) (toPhysicalX(cx));
        cy = (int) (toPhysicalY(cy));
        int rx = (int) (toPhysicalX(r));
        int ry = (int) (toPhysicalY(r));
        g_.fillOval(cx - rx , cy - ry, 2*rx, 2*ry);
    }

    /**
     * Dibuja un texto dado con una fuente dada.
     * @param font Fuente
     * @param text Texto
     * @param x Posicion X
     * @param y Posicion Y
     * @param centered Se dibuja en base a su centro a su esquina superior izquierda
     */
    @Override
    public void drawText(Font font, String text, int x, int y, boolean centered) {
        FontPC f = (FontPC) font;
        java.awt.Font jfont = f.getFont();
        FontMetrics fm = g_.getFontMetrics(jfont);

        //Se ajustan los valores a las dimensiones reales del canvas
        x = toPhysicalX(x);
        y = toPhysicalY(y);

        // Se pone el tamaño ajustado del texto segun el canvas
        f.setRenderSize(toPhysicalX(f.originalSize_));
        // Se ponen color y fuente de jframe
        g_.setFont(jfont);
        Color c = f.getColor();
        g_.setColor(new java.awt.Color(c.r, c.g, c.b, c.a));

        //Se modifica la posición de dibujado para que aparezca centrado si se ha pedido.
        if (!centered) {
            g_.drawString(text, x, y);
            return;
        }
        //Se calcula el alto y ancho del texto para poder centrarlo
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = f.getFont().getStringBounds(text, frc);
        int rWidth = fm.stringWidth(text);
        int rHeight = (int)Math.round(r2D.getHeight());
        int tX = (int)Math.round(r2D.getX());
        int tY = (int)Math.round(r2D.getY());
        int a = x - (rWidth / 2) - tX;
        int b = y - (rHeight / 2) - tY;
        g_.drawString(text, a, b);
    }


    /**
     * Actualiza el motor de renderizado, pone en blanco el render buffer y actualiza la posicion de la ventana
     */
    public void setGraphics(Graphics g){
        g_ = g;
        clear(new Color(255,255,255,255));
        g.translate(curPosX, curPosY);
        g_.setClip(0, 0, curSizeX, curSizeY); // Ya se ha trasladado, su 0, 0 esta movido ya
    }

    /**
     * Devuelve el ancho de la ventana.
     */
    @Override
    public int getWidth() {
        return refSizeX;
    }

    /**
     * Devuelve el alto de la ventana.
     */
    @Override
    public int getHeight() {
        return refSizeY;
    }

    /**
     * Calcula el ancho de un texto con una fuente dada
     */
    @Override
    public int getTextWidth(Font font, String text) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = ((FontPC)font).getFont().getStringBounds(text, frc);
        return toPhysicalX((int)Math.round(r2D.getWidth()));
    }
    /**
     * Calcula el alto de un texto con una fuente dada
     */
    @Override
    public int getTextHeight(Font font, String text) {
        FontRenderContext frc = new FontRenderContext(null, true, true);
        Rectangle2D r2D = ((FontPC)font).getFont().getStringBounds(text, frc);
        return toPhysicalY((int)Math.round(r2D.getHeight()));
    }

    /**
     * Desplaza el canvas las unidades virtuales indicadas en los ejes X e Y haciendo
     * la transformacion necesaria para ajustarse al tamaño actual de la ventana
     */
    @Override
    public void translate(int dx, int dy) {
        dx = toPhysicalX(dx);
        dy = toPhysicalY(dy);
        g_.translate(dx, dy);
    }

    /**
     * Cambia la escala del canvas
     */
    @Override
    public void scale(float sx, float sy) {
        ((Graphics2D)g_).scale(sx, sy);
    }

    /**
     * Fija el tamaño de la ventana.
     */
    public void setWindowSize(int w, int h){
        jf_.setSize(w, h);
    }

    /**
     * Guarda la posicion del canvas
     */
    @Override
    public void save() {
        saves_.add(g_.create());
    }

    /**
     * Vuelve a la posicion anterior del canvas
     */
    @Override
    public void restore() {
        if(saves_.size() > 0)
            g_ = saves_.pop();
    }

    /**
     * Actualiza las dimensiones de un componente cuando estas cambian
     */
    @Override
    public void componentResized(ComponentEvent componentEvent) {
        Dimension dim = componentEvent.getComponent().getSize();
        adjustToWindowSize(dim.width, dim.height);
    }


    //Metodos que hay que Overridear pero no se quieren
    @Override
    public void componentMoved(ComponentEvent componentEvent) {}

    @Override
    public void componentShown(ComponentEvent componentEvent) {}

    @Override
    public void componentHidden(ComponentEvent componentEvent) {}
}
