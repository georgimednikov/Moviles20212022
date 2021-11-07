package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePC implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image {
    private java.awt.Image image_;
    private ImageObserver observer_;
    private int x_, y_;

    ImagePC(String name) {
        try {
            image_ = ImageIO.read(new File(name));
        }
        catch (IOException e)
        {
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
    }

    public void setPos(int x, int y) {
        x_ = x; y_ = y;
    }

    @Override
    public void setSize(int width, int height) {
        observer_.imageUpdate(image_, 0, x_, y_, width, height);
    }

    @Override
    public int getWidth() {
        return image_.getWidth(observer_);
    }

    @Override
    public int getHeight() {
        return image_.getHeight(observer_);
    }

    public ImageObserver getObserver(){
        return observer_;
    }

    public java.awt.Image getImage(){
        return image_;
    }
}
