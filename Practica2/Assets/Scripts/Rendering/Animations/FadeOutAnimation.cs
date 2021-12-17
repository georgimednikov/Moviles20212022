using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.Events;
using UnityEngine.UI;

public class FadeOutAnimation : MonoBehaviour
{
    SpriteRenderer render;
    GameAnimation anim;
    Vector3 baseSize;
    public float increase;
    public float duration;

    UnityEvent finished;

    private void Awake()
    {
        render = GetComponent<SpriteRenderer>();
        baseSize = transform.localScale;
        anim = new GameAnimation(duration);
        anim.active = true;
        finished = new UnityEvent();
    }

    private void Update()
    {
        if (anim.active && !anim.UpdateWait())
        {
            if (anim.UpdateTime())
            {
                float factor = Mathf.Min(1, anim.elapsedTime / anim.durationTime);
                transform.localScale = new Vector3(baseSize.x + (baseSize.x * increase) * factor, baseSize.y + (baseSize.y * increase) * factor, baseSize.z);
                Color color = render.color; color.a = 1 - factor;
                render.color = color;
            }
            else
            {
                finished.Invoke();
                Destroy(gameObject);
            }
        }
    }

    public void AddListener(UnityAction callback)
    {
        finished.AddListener(callback);
    }
}
