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
        SkinPack skin = GameManager.instance.skinPack;
        LevelPack pack = GameManager.instance.nextPack;
        nameText.text = pack.levelName;
        nameText.color = GameManager.instance.nextBundle.bundleColor;
        int i = 0;
        int j = 0;
        while(i < pack.numLevels)
        {
            LevelGrid g = Instantiate(levelGridPrefab, content).GetComponent<LevelGrid>();
            g.SetIndexStart(i);
            g.SetColor(skin.colors[j]);
            g.SetDescriptionText(pack.minipackName[i / 30]);
            i += 30;
            ++j;
        }
    }
}
