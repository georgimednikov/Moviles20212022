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
        int i = 0;
        while(i < pack.numLevels)
        {
            LevelGrid g = Instantiate(levelGridPrefab, content).GetComponent<LevelGrid>();
            g.SetIndexStart(i);
            i += 30;
        }
    }
}
