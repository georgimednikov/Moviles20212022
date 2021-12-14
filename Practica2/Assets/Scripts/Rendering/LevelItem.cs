using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class LevelItem : MonoBehaviour
{
    [SerializeField] Image backgroundImg;
    [SerializeField] Text indexText;
    [SerializeField] Image star;
    [SerializeField] Image tick;

    public void SetColor(Color32 col)
    {
        backgroundImg.color = col;
    }

    public void SetStar(bool enabled)
    {
        star.enabled = enabled;
    }

    public void SetTick(bool enabled)
    {
        tick.enabled = enabled;
    }

    public void SetLevelIndex(int index)
    {
        indexText.text = index.ToString();
    }
}
