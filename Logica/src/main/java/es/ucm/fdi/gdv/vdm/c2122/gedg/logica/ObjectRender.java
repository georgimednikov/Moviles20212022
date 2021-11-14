package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

public class ObjectRender {

    protected float animationDur_; //Segundos que duran los fades
    protected float alpha_;
    protected float elapsedTime_;
    protected boolean animated_;
    protected boolean fadeIn_;

    public ObjectRender(boolean visible) {
        alpha_ = visible ? 1 : 0;
        elapsedTime_ = 0;
    }

    public void render(Graphics g) {}

    public void updateRender(double deltaTime) {
        if (!animated_) return;
        if (elapsedTime_ >= animationDur_) {
            elapsedTime_ = 0;
            animated_ = false;
            if (fadeIn_) onFadeInEnd();
            else onFadeOutEnd();
        } else {
            elapsedTime_ += deltaTime;
            if (fadeIn_) alpha_ = Math.min((elapsedTime_ / animationDur_), 1);
            else alpha_ = 1 - Math.min((elapsedTime_ / animationDur_), 1);
        }
    }

    public void changeState() {
        fadeIn_ = !fadeIn_;
        animated_ = true;
    }

    public void fadeIn() {
        animated_ = fadeIn_ = true;
    }

    public void fadeOut() {
        animated_ = true;
        fadeIn_ = false;
    }

    protected void onFadeInEnd() {};
    protected void onFadeOutEnd() {};
}
