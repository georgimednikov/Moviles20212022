package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherpc;

import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.EnginePC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.enginepc.GraphicsPC;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.Board;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOIntro;

public class LauncherPC {
    public static void main(String[] args){
Board b = new Board(5);
        //Inicia el motor.
        EnginePC pc = new EnginePC();
        pc.init("0hn0");

        //Coge el motor gráfico del motor y le dice el tamaño de la ventana.
        ((GraphicsPC)pc.getGraphics()).setWindowSize(400, 600);

        //Se fija la primera escena del juego
        OhnOIntro g = new OhnOIntro();
        pc.changeScene(g);

        //Comienza la ejecución.
        pc.run();
    }
}