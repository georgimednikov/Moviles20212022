using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Events;

public class BoardAnimation : MonoBehaviour
{
    public float flipDuration = 0.5f;
    GameAnimation anim;
    Vector3 boardScale;
    bool appearing;

    UnityEvent animEvent;

    private void Awake()
    {
        anim = new GameAnimation(flipDuration);
    }

    private void Update()
    {
        if (anim.active && !anim.UpdateWait())
        {
            if (anim.UpdateTime())
            {
                float factor = appearing? 
                    Mathf.Min(anim.elapsedTime / anim.durationTime, 1f) :
                    Mathf.Max(1 - anim.elapsedTime / anim.durationTime, 0f);
                transform.localScale = new Vector3(boardScale.x * factor, boardScale.y, boardScale.z);
            }
            else
            {
                animEvent.Invoke();
            }
        }
    }

    public void FlipIn(float wait = -1)
    {
        if (anim.active) return;
        boardScale = transform.localScale; //La escala ya se ha ajustado y se va a multiplicar por casi 0 en el update
        appearing = true;
        anim.active = true;
        anim.waitTime = wait;
    }

    public void FlipOut(float wait = -1)
    {
        if (anim.active) return;
        boardScale = transform.localScale; //La escala ya es correcta porque ya se estaba renderizando
        appearing = false;
        anim.active = true;
        anim.waitTime = wait;
    }

    public void AddListener(UnityAction callback)
    {
        animEvent = new UnityEvent();
        animEvent.AddListener(callback);
    }
}
