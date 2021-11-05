package es.ucm.fdi.gdv.vdm.c2122.gedg.engineandroid;

import android.text.method.Touch;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Input;
import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.TouchEvent;

public class InputAndroid extends View implements Input {

    private List<TouchEvent> events_;

    public InputAndroid(Context context) {
        super(context);
        events_ = new ArrayList<>();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        TouchEvent event = new TouchEvent();
        switch(e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                event.type = TouchEvent.TouchType.PRESS;
                break;
            case MotionEvent.ACTION_UP:
                event.type = TouchEvent.TouchType.LIFT;
                break;
            case MotionEvent.ACTION_MOVE:
                event.type = TouchEvent.TouchType.DRAG;
                break;
        }
        int pointers = e.getPointerCount();
        event.finger = e.getPointerId(0);
        event.x = (int)e.getX(event.finger); event.y = (int)e.getY(event.finger);
        events_.add(event);
        return true;
    }
    @Override
    public List<TouchEvent> getTouchEvents() {
        return events_;
    }
}