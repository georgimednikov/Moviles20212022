package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.view.MotionEvent;
import android.view.View;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.InputCommon;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid extends InputCommon implements View.OnTouchListener {

    private GraphicsAndroid g_;

    private int startingEvents = 5;
    private float[] x = new float[10];
    private float[] y = new float[10];
    private boolean[] touch = new boolean[10];

    public InputAndroid(GraphicsAndroid g) {
        g_ = g;
        g_.getSurfaceView().setOnTouchListener(this); //Se añade como listener de la aplicacion
    }

    @Override
    public boolean onTouch(View view, MotionEvent e) {
        int pointerIndex = ((e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = 0;
        try {
            int mActivePointerId = e.getPointerId(pointerIndex);
            pointerId = e.findPointerIndex(mActivePointerId);
            TouchEvent.TouchType type = null;
            switch (e.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    type = TouchEvent.TouchType.PRESS;
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_POINTER_UP:
                    type = TouchEvent.TouchType.LIFT;
                    break;
                case MotionEvent.ACTION_MOVE:
                    type = TouchEvent.TouchType.DRAG;
                    break;
            }
            enqueueEvent(g_.toVirtualX((int)e.getX(pointerId)), g_.toVirtualY((int)e.getY(pointerId)), pointerId, type);
        }catch (Exception f){
            f.printStackTrace();
        }
        return true;
    }
}