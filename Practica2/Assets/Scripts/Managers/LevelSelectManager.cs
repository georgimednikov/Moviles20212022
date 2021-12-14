using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class LevelSelectManager : MonoBehaviour
{
    [SerializeField] GameObject levelGridPrefab;
    [SerializeField] Text nameText;
    [SerializeField] Transform content;
    private void Start()
    {
        LoadLevels();
    }

    public void LoadLevels()
    {
        LevelPack pack = GameManager.instance.nextPack;
        nameText.text = pack.levelName;
        // nameText.color =  TODO
        int i = pack.numLevels;
        while(i > 0)
        {
            Instantiate(levelGridPrefab, content);
            i -= 30;
        }
    }
}
