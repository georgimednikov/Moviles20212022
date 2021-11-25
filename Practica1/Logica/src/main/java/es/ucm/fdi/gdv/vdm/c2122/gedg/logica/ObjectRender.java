package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

public class ObjectRender {

    protected float maxAlpha_;
    protected float minAlpha_;
    protected float alpha_;
    protected float fadeDur_; //Segundos que duran los fades
    protected float elapsedFade_; //Tiempo transcurrido
    protected boolean fading_; //Si esta haciendo un fade
    protected boolean fadeIn_; //Si esta haciendo fade-in

    public ObjectRender(boolean visible) {
        alpha_ = visible ? 1 : 0;
        elapsedFade_ = 0;
        maxAlpha_ = 1f;
        minAlpha_ = 0f;
    }

    public void render(Graphics g) {}

    public void updateRender(double deltaTime) {
        if (!fading_) return;
        if (elapsedFade_ >= fadeDur_) {
            elapsedFade_ = 0;
            fading_ = false;
            if (fadeIn_) onFadeInEnd();
            else onFadeOutEnd();
        } else {
            elapsedFade_ += deltaTime;
            float dist = maxAlpha_ - minAlpha_;
            if (fadeIn_) alpha_ = minAlpha_ + Math.min((elapsedFade_ / fadeDur_) * dist, dist);
            else alpha_ = maxAlpha_ - Math.min((elapsedFade_ / fadeDur_) * dist, dist);
        }
    }

    public void changeState() {
        fadeIn_ = !fadeIn_;
        fading_ = true;
    }

    public void fadeIn(float dur) {
        fadeDur_ = dur;
        fading_ = fadeIn_ = true;
    }

    public void fadeOut(float dur) {
        fadeDur_ = dur;
        fading_ = true;
        fadeIn_ = false;
    }

    protected void onFadeInEnd() {};
    protected void onFadeOutEnd() {};
}
