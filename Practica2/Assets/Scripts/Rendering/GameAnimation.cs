using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public struct GameAnimation
{
    public bool active;
    public float durationTime;
    public float elapsedTime;
    public float waitTime;

    public GameAnimation(float dur, float wait = -1)
    {
        active = false;
        elapsedTime = 0;
        waitTime = wait;
        durationTime = dur;
    }

    public bool UpdateWait()
    {
        if (waitTime < 0) return false;
        elapsedTime += Time.deltaTime;
        if (elapsedTime >= waitTime)
        {
            elapsedTime = 0;
            waitTime = -1;
            return false;
        }
        else
            return true;
    }

    public bool UpdateTime()
    {
        elapsedTime += Time.deltaTime;
        if (elapsedTime >= durationTime)
        {
            elapsedTime = 0;
            waitTime = -1;
            active = false;
            return false;
        }
        else
        {
            return true;
        }
    }
}