using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.Serialization;

public class PackSelect : MonoBehaviour
{
    [Header("Pack")]
    [SerializeField] LevelBundle bundle;

    [Header("RenderItem")]
    [SerializeField] Text nameText;
    [SerializeField] Image foregroundImg;
    [SerializeField] GameObject packTextPrefab;

    int bundleIndex;

    public void SetBundle(int index)
    {
        bundleIndex = index;
        bundle = GameManager.instance.levelBundles[index];
    }

    public void LoadPacks()
    {
        nameText.text = bundle.bundleName;
        foregroundImg.color = bundle.bundleColor;
        for(int i = 0; i < bundle.packs.Length; ++i)
        {
            PackText ui = Instantiate(packTextPrefab, transform).GetComponent<PackText>();
            ui.SetPackName(bundle.packs[i].levelName);
            ui.SetPackTotalLevels(bundle.packs[i].numLevels);
            ui.SetColor(bundle.bundleColor);
            ui.SetButtonEvent(bundleIndex, i);
            //ui.SetPackCompletedLevels(); TODO
        }
    }
}
