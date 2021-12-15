using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public struct Animation
{
    public bool active;
    public float durationTime;
    public float elapsedTime;

    public Animation(float dur)
    {
        active = false;
        elapsedTime = 0;
        durationTime = dur;
    }

    public bool UpdateTime()
    {
        elapsedTime += Time.deltaTime;
        if (elapsedTime >= durationTime)
        {
            elapsedTime = 0;
            active = false;
            return false;
        }
        else
            return true;
    }
}

public class TileAnimation : MonoBehaviour
{
    public GameObject flowEnd;
    public float bumpDuration = 0.3f;
    public float bumpSizeInc = 0.3f;
    Vector3 originalScale;

    public GameObject flowMid;
    public float waveDuration = 0.5f;
    public float waveSizeInc = 2.5f;
    GameObject flowWave;
    SpriteRenderer waveRenderer;
    float baseSize;

    Animation wave;
    Animation bump;

    private void Start()
    {
        wave = new Animation(waveDuration);
        bump = new Animation(bumpDuration);

        flowWave = Instantiate(flowMid, transform);
        waveRenderer = flowWave.GetComponent<SpriteRenderer>();

        originalScale = flowEnd.transform.localScale;
        baseSize = flowEnd.transform.localScale.x;
    }

    private void Update()
    {
        if (bump.active)
        {
            if (bump.UpdateTime())
            {
                float factor = bump.elapsedTime / bump.durationTime * 2;
                if (factor > 1f) factor += (1f - factor) * 2;
                float size = baseSize + ((baseSize * bumpSizeInc) * factor);
                flowEnd.transform.localScale = new Vector3(size, size, size);
            }
            else
            {
                flowEnd.transform.localScale = originalScale;
            }
        }

        if (wave.active)
        {
            if (wave.UpdateTime())
            {
                float factor = Mathf.Min(1, wave.elapsedTime / wave.durationTime);
                float size = baseSize + (baseSize * waveSizeInc) * factor;
                flowWave.transform.localScale = new Vector3(size, size, baseSize);
                Color color = waveRenderer.color; color.a = 1 - factor;
                waveRenderer.color = color;
            }
            else
            {
                flowWave.transform.localScale = originalScale;
                flowWave.SetActive(false);
            }
        }
    }

    public void PlayBump()
    {
        bump.active = true;
    }

    public void PlayWave()
    {
        wave.active = true;
        flowWave.SetActive(true);
    }
}
