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
            int finished = PlayerPrefs.GetInt("finished_" + GameManager.instance.nextPack.name + "_" + (index + i + 1), 0); // TODO: meter en un playerprefs manager
            if (finished == 2) levels[i].SetStar(true);
            if (finished == 1) levels[i].SetTick(true);
            int locked = PlayerPrefs.GetInt("locked_" + GameManager.instance.nextPack.name + "_" + (index + i + 1), -1); // Si es -1, no se ha guardado en playerprefs (comportamiento segun el pack) TODO: playerprefsmanager
            if(locked == 1 || locked == -1 && GameManager.instance.nextPack.locked && (i != 0 || index != 0)) levels[i].SetLevelLocked();
            else levels[i].SetButtonEvent(index + i);
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
