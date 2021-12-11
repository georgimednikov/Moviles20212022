using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class InputManager : MonoBehaviour
{
    
    void Update()
    {
        if (Input.GetButton("Fire1"))
        {
            Vector3 pos = Camera.main.ScreenToWorldPoint(Input.mousePosition);
            GameManager.instance.LM.BM.TouchedHere(pos);
        }
        else if (Input.GetButtonUp("Fire1"))
            GameManager.instance.LM.BM.Untouched();
    }
}
