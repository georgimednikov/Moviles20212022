package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherandroid;

import android.os.Bundle;
import android.view.SurfaceView;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid.EngineAndroid;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOIntro;

public class MainActivity extends AppCompatActivity {

    EngineAndroid eng;

    /**
     * Cuando se inicia el programa se llama a este método, que inicia el juego.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); //Fullscreen.
        SurfaceView view = new SurfaceView(this);
        eng = new EngineAndroid(this, view); //Se inicia el motor.
        setContentView(view); //Se le pasa la View al motor.

        //Se fija la escena inicial.
        OhnOIntro g = new OhnOIntro();
        eng.changeScene(g);
    }

    /**
     * Método que continúa el programa si deja de estar pausado.
     */
    @Override
    protected void onResume(){
        super.onResume();
        eng.onResume();
    }

    /**
     * Método que pausa el programa si se pone en segundo plano.
     */
    @Override
    protected void onPause(){
        super.onPause();
        eng.onPause();
    }
}