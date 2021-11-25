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
        TouchEvent event = addEvent();
        try {
            int mActivePointerId = e.getPointerId(pointerIndex);
            pointerId = e.findPointerIndex(mActivePointerId);
            event.finger = pointerId;
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                event.type = TouchEvent.TouchType.PRESS;
                event.x = g_.toVirtualX((int)e.getX(pointerId));
                event.y = g_.toVirtualY((int)e.getY(pointerId));
                event.finger = pointerId;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_POINTER_UP:
                event.type = TouchEvent.TouchType.LIFT;
                event.x = g_.toVirtualX((int)e.getX(pointerId));
                event.y = g_.toVirtualY((int)e.getY(pointerId));
                event.finger = pointerId;
                break;
            case MotionEvent.ACTION_MOVE:
                event.type = TouchEvent.TouchType.DRAG;
                event.x = g_.toVirtualX((int)e.getX(pointerId));
                event.y = g_.toVirtualY((int)e.getY(pointerId));
                event.finger = pointerId;
                break;
        }
        }catch (Exception f){
            f.printStackTrace();
        }
        return true;
    }
}