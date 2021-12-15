using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InputManager : MonoBehaviour
{
    public bool inputEnabled = true;
    void Update()
    {
        if (inputEnabled && Input.GetButton("Fire1"))
        {
            Vector3 pos = Camera.main.ScreenToWorldPoint(Input.mousePosition);
            GameManager.instance.LM.BM.TouchedHere(pos);
        }
        else if (Input.GetButtonUp("Fire1"))
            GameManager.instance.LM.BM.StoppedTouching();

        if (inputEnabled && Input.touches.Length > 0)
        {
            var touch = Input.touches[0];
            if (touch.phase == TouchPhase.Began || touch.phase == TouchPhase.Moved)
            {
                Vector3 pos = Camera.main.ScreenToWorldPoint(touch.position);
                GameManager.instance.LM.BM.TouchedHere(pos);
            }
            else if (touch.phase == TouchPhase.Ended)
                GameManager.instance.LM.BM.StoppedTouching();
        }
    }
}
