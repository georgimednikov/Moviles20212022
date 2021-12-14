using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class PackText : MonoBehaviour
{
    [SerializeField] Text nameText;
    [SerializeField] Text levelsText;

    int totalLevels;
    int completedLevels;

    public void SetPackName(string name)
    {
        nameText.text = name;
    }

    public void SetColor(Color32 col)
    {
        nameText.color = col;
    }

    public void SetPackTotalLevels(int levels)
    {
        totalLevels = levels;
        levelsText.text = completedLevels + "/" + totalLevels;
    }

    public void SetPackCompletedLevels(int levels)
    {
        completedLevels = levels;
        levelsText.text = completedLevels + "/" + totalLevels;
    }
}
