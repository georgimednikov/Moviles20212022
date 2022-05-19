package es.ucm.fdi.gdv.vdm.c2122.gedg.logica;

import es.ucm.fdi.gdv.vdm.c2122.gedg.engine.Graphics;

/**
 * Clase que representa objetos renderizables.
 * Contiene información relacionada con sus animaciones de aparecer y desaparecer (fade in/out).
 */
public class ObjectRenderer {

    protected float maxAlpha_;
    protected float minAlpha_;
    protected float alpha_;
    protected float fadeDur_; //Segundos que duran los fades
    protected float elapsedFade_; //Tiempo transcurrido
    protected boolean fading_; //Si esta haciendo un fade
    protected boolean fadeIn_; //Si esta haciendo fade-in

    public ObjectRenderer(boolean visible) {
        alpha_ = visible ? 1 : 0;
        elapsedFade_ = 0;
        maxAlpha_ = 1f;
        minAlpha_ = 0f;
    }

    /**
     * Dibuja el objeto en el canvas. Método vacío que hay que implementar heredando.
     */
    public void render(Graphics g) {}

    /**
     * Utiliza el deltaTime para actualizar los estados de las animaciones.
     */
    public void updateRenderer(double deltaTime) {
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

    /**
     * Cicla la visibilidad del objeto.
     */
    public void changeVisibility() {
        fadeIn_ = !fadeIn_;
        fading_ = true;
    }

    /**
     * Aparece esta celda progresivamente.
     * Si la celda tiene un número sociado aparece también.
     */
    public void fadeIn(float dur) {
        fadeDur_ = dur;
        fading_ = fadeIn_ = true;
    }

    /**
     * Desvanece esta celda progresivamente.
     * Si la celda tiene otra imagen asociada (número o candado) se desvanece también.
     */
    public void fadeOut(float dur) {
        fadeDur_ = dur;
        fading_ = true;
        fadeIn_ = false;
    }

    //Eventos en al terminar una animación de fade.
    protected void onFadeInEnd() {};
    protected void onFadeOutEnd() {};
}
