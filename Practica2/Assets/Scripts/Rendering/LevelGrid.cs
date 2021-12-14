using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class LevelGrid : MonoBehaviour
{
    [SerializeField] LevelItem[] levels;
    int ind;
    public void SetIndexStart(int index)
    {
        index = ind;
        for(int i = 0; i < levels.Length; ++i){
            levels[i].SetLevelIndex(index + i);
            levels[i].SetButtonEvent(index + i);
            // levels[i].SetColor(); TODO
        }
    }
}
