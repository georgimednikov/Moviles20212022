package es.ucm.fdi.gdv.vdm.c2122.gedg.launcherandroid;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid.EngineAndroid;
import es.ucm.fdi.gdv.vdm.c2122.gedg.logica.OhnOIntro;

public class MainActivity extends AppCompatActivity {

    EngineAndroid eng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        eng = new EngineAndroid(this);
        setContentView(eng.getSurfaceView());
        OhnOIntro g = new OhnOIntro();
        eng.changeState(g);
    }

    @Override
    protected void onResume(){
        super.onResume();
        eng.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        eng.onPause();
    }
}