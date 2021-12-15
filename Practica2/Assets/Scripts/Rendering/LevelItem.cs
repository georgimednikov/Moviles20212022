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
    [SerializeField] Image levelLock;
    [SerializeField] Image transparentImg;
    [SerializeField] Image[] transparentSides;

    public void SetButtonEvent(int level)
    {
        GetComponent<Button>().onClick.AddListener(() => { GameManager.NextLevel(level); });
    }

    public void SetColor(Color32 col)
    {
        backgroundImg.color = col;
        Color color = col;
        float alpha = transparentImg.color.a;
        color.a = alpha;
        transparentImg.color = color;
        foreach (var img in transparentSides)
        {
            img.color = color;
        }
    }

    public void SetStar(bool enabled)
    {
        star.enabled = enabled;
        backgroundImg.enabled = true;
    }

    public void SetTick(bool enabled)
    {
        tick.enabled = enabled;
        backgroundImg.enabled = true;
    }

    public void SetLevelIndex(int index)
    {
        indexText.text = index.ToString();
    }

    public void SetLevelLocked()
    {
        levelLock.enabled = true;
        backgroundImg.enabled = false;
    }
}
