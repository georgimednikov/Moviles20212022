using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class LevelGrid : MonoBehaviour
{
    [SerializeField] LevelItem[] levels;
    [SerializeField] Text descriptionText;
    int ind;
    public void SetIndexStart(int index)
    {
        ind = index;
        for(int i = 0; i < levels.Length; ++i){
            levels[i].SetLevelIndex(i + 1);
            var levelsave = GameManager.instance.GetComponent<SaveManager>().RestoreLevel(GameManager.instance.nextPack.levelName, i + 1 + index);
            int finished = levelsave.completed;
            if (finished == 2) levels[i].SetStar(true);
            if (finished == 1) levels[i].SetTick(true);
            int locked = levelsave.locked;
            if (locked == 1 || locked == -1 && GameManager.instance.nextPack.locked && (i != 0 || index != 0))
            {
                levels[i].SetLevelLocked();
                levelsave.locked = 1; // Si es -1, hay que ponerle el valor de verdad
            }
            else
            {
                levels[i].SetButtonEvent(index + i);
                levelsave.locked = 0; // Si es -1, hay que ponerle el valor de verdad
            }
        }
    }

    public void SetDescriptionText(string desc)
    {
        descriptionText.text = desc;
    }

    public void SetColor(Color32 col)
    {
        foreach (LevelItem i in levels)
        {
            i.SetColor(col);
        }
    }
}
