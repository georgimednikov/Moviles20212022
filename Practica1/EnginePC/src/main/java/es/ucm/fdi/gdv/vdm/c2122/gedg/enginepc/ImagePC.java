package es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * Clase que representa una imagen en la plataforma de PC.
 */
public class ImagePC implements es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Image {

    private java.awt.Image image_; //Imagen para Java

    ImagePC(String name) {
        try {
            image_ = ImageIO.read(new File(name));
        }
        catch (IOException e)
        {
            //Si no se encuentra la imagen salta un error.
            String workingDir = System.getProperty("user.dir");
            System.out.println("Current working directory : " + workingDir);
            e.printStackTrace();
        }
    }

    /**
     * Devuelve la anchura de la imagen.
     */
    @Override
    public int getWidth() {
        return image_.getWidth(null);
    }

    /**
     * Devuelve la altura de la imagen.
     */
    @Override
    public int getHeight() {
        return image_.getHeight(null);
    }

    /**
     * Devuelve la imagen que utiliza JFrame para renderizar.
     */
    public java.awt.Image getImage(){
        return image_;
    }
}
