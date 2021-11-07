package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImagePC implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image {
    private java.awt.Image image_;
    public int x, y;

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

    @Override
    public void setSize(int width, int height) {
        image_ = image_.getScaledInstance(width,height, Image.SCALE_DEFAULT);
    }

    @Override
    public int getWidth() {
        return image_.getWidth(null);
    }

    @Override
    public int getHeight() {
        return image_.getHeight(null);
    }

    public java.awt.Image getImage(){
        return image_;
    }
}
